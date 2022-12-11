package main;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class GeneticAlgorithm {

    /**
     * Population associated with the genetic algorithm
     */
    private final Population pop;

    /**
     * Adjacency hash table of affinities for each machine type
     */
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Float>> affinities;

    /** Min heap based priority queue for keeping track of worsts performing generations
     * at each iteration
     */
    PriorityQueue<Generation> minQ = new PriorityQueue<>();

    /** Max heap based priority queue for keeping track of the best performing generations
     * at each iteration
     */
    PriorityQueue<Generation> maxQ = new PriorityQueue<>(Collections.reverseOrder());

    /**
     * Creates a new instance of GeneticAlgorithm
     */
    public GeneticAlgorithm() {
        this.affinities = this.generateAffinities();
        this.pop = this.initPopulation();
    }

    /**
     * Retrieves the population associated with the genetic algorithm.
     *
     * @return population associated with the genetic algorithm.
     */
    public Population getPop() {
        return pop;
    }

    /**
     * Generates a randomized floor plan.
     *
     * @return a randomized floor plan
     */
    private Station[][] initRandomFloorPlan() {
        Station[][] floorPlan = new Station[Constants.NUM_ROWS][Constants.NUM_COLS];

        for (int i = 0; i < Constants.NUM_ROWS; ++i) {
            for (int j = 0; j < Constants.NUM_COLS; ++j) {
                int r = ThreadLocalRandom.current().nextInt(Constants.NUM_TYPES);
                floorPlan[i][j] = new Station(r);
            }
        }
        return floorPlan;
    }

    /**
     * Generates the initial Population associated with the genetic algorithm.
     *
     * @return initial Population associated with the genetic algorithm.
     */
    private Population initPopulation() {
        Population p = new Population();
        Generation[] gens = new Generation[Constants.NUM_GENS];

        for (int i = 0; i < Constants.NUM_GENS; ++i) {
            Generation g = new Generation(i, this.initRandomFloorPlan());
            gens[i] = g;
        }

        p.setGens(gens);
        return p;
    }

    /**
     * Generates random affinity values for each type of machine to each other type of
     * machine. Every item in the hash table is another hash table of size N - 1, where
     * N is the number of possible types of machines.
     *
     * @return an adjacency hash table of affinity values.
     */
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Float>> generateAffinities() {
        ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Float>> map = new ConcurrentHashMap<>();
        final ExecutorService ex = Executors.newWorkStealingPool();

        /* Loop through each possible station type */
        for (int i = 0; i < Constants.NUM_TYPES; ++i) {
            final int iCopy = i;

            ex.execute(() -> {

                /* Create hash table to store the affinities for station of type i */
                ConcurrentHashMap<Integer, Float> currMap = new ConcurrentHashMap<>();

                /* For each type of station (excluding current type), store its affinity for that type */
                for (int j = 0; j < Constants.NUM_TYPES; ++j) {
                    if (j == iCopy) continue;

                    /* Put a random value (0 <= val <= 1) in the hash table */
                    currMap.put(j, ThreadLocalRandom.current().nextFloat());
                }
                /* Store the current hash table in the overarching hash table */
                map.put(iCopy, currMap);
            });
        }
        ex.shutdown();
        while (!ex.isTerminated()) ;
        return map;
    }

    /**
     * Computes the score of a given instance of Generation.
     * <p>
     * For each possible pair of stations in the Generation's floor plan
     * (excluding stations of the same type), it computes the
     * average distance of the two stations ((xDist + yDist) / 2), and adds
     * affinity / avgDistance to the running total score  for the Generation.
     *
     * @param gen instance of Generation whose score is being computed.
     */
    private void computeScore(Generation gen) {
        ReentrantLock floorLock = new ReentrantLock();
        ReentrantLock minQLock = new ReentrantLock();
        ReentrantLock maxQLock = new ReentrantLock();

        double totalScore = 0;

        for (int i = 0; i < Constants.NUM_ROWS; i++) {

            for (int j = 0; j < Constants.NUM_COLS; ++j) {
                Station currStation = gen.getFloorPlan()[i][j];
                ConcurrentHashMap<Integer, Float> currTypeAffinities = this.affinities.get(currStation.getType());

                for (int k = 0; k < Constants.NUM_ROWS; ++k) {

                    for (int l = 0; l < Constants.NUM_COLS; ++l) {
                        if (gen.getFloorPlan()[k][l].getType() == currStation.getType()) continue;

                        /* Averaging out the distance to make the value be more generalizable */
                        double distance = (Math.abs(i - k) + Math.abs(j - l)) / 2.0;
                        totalScore += currTypeAffinities.get(gen.getFloorPlan()[k][l].getType()) / distance;
                    }
                }
            }
        }

        /* Add to min heap */
        minQLock.lock();
        try {
            minQ.add(gen);
        } finally {
            minQLock.unlock();
        }

        /* Add to max heap */
        maxQLock.lock();
        try {
            maxQ.add(gen);
        } finally {
            maxQLock.unlock();
        }

        /* If this is the best one so far, update bookkeeping */
        if (this.pop.getBestFloorPlan() == null || totalScore >= this.pop.getBestScore()) {
            floorLock.lock();
            try {
                this.pop.setBestFloorPlan(gen.getFloorPlan());
                this.pop.setBestScore(totalScore);
            } finally {
                floorLock.unlock();
            }
        }
    }

    /**
     * Computes the scores of each Generation in the associated Population.
     * <p>
     * Each Generation gets its own thread, and calls computeScore(Generation gen).
     */
    public void computeScores() {
        ExecutorService ex = Executors.newWorkStealingPool();

        for (int i = 0; i < Constants.NUM_GENS; ++i) {
            final int iCopy = i;
            ex.execute(() -> {
                this.computeScore(this.pop.getGens()[iCopy]);
            });
        }
        ex.shutdown(); /* Fazer */
        while (!ex.isTerminated()) ;
    }

    public void mutate(Generation g) {
        for (int i = 0; i < 2; ++i) {
            int randomRow = ThreadLocalRandom.current().nextInt(g.getFloorPlan().length);
            int randomCol = ThreadLocalRandom.current().nextInt(g.getFloorPlan()[0].length);
            int randomType = ThreadLocalRandom.current().nextInt(Constants.NUM_TYPES);

            g.getFloorPlan()[randomRow][randomCol] = new Station(randomType);
        }
    }

    public Generation[] getWorst() {
        Generation[] worst = new Generation[] {this.minQ.poll(), this.minQ.poll()};

        /* Clear it, so it's empty for the next iteration */
        this.minQ.clear();
        return worst;
    }

    public Generation[] getBest() {
        Generation[] best = new Generation[] {this.maxQ.poll(), this.maxQ.poll()};

        /* Clear it, so it's empty for the next iteration */
        this.maxQ.clear();
        return best;
    }

    public Generation[] mate(Generation[] bestGens) {
        Exchanger<Station[][]> exchanger = new Exchanger<>();
        ExecutorService ex = Executors.newWorkStealingPool();
        Generation[] children = new Generation[2];

        for (int i = 0; i < 2; ++i) {
            int iCopy = i;
            ex.execute(() -> {
                Station[][] otherOne;
                try {
                    otherOne = exchanger.exchange(bestGens[iCopy].getFloorPlan());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Station[][] floorPlan = new Station[Constants.NUM_ROWS][Constants.NUM_COLS];

                /* First half -> current floorPlan */
                for (int j = 0; j < Constants.NUM_ROWS / 2; ++j) {
                    for (int k = 0; k < Constants.NUM_COLS; ++k) {
                        floorPlan[j][k] = bestGens[iCopy].getFloorPlan()[j][k];
                    }
                }
                if (otherOne[0][0] == null)
                    throw new IllegalStateException("Did not receive the exchange.");

                /* Second half -> other floor plan */
                for (int j = Constants.NUM_ROWS / 2; j < Constants.NUM_ROWS; ++j) {
                    System.arraycopy(otherOne[j], 0, floorPlan[j], 0, Constants.NUM_COLS);
                }
                Generation offspring = new Generation(bestGens[iCopy].getIdx(), floorPlan);
                children[iCopy] = offspring;
            });
        }
        ex.shutdown();
        while (!ex.isTerminated());
        return children;
    }

    public void runIteration() {
        // compute scores
        this.computeScores();
        // find best, worst
        Generation[] best = this.getBest();
        Generation[] worst = this.getWorst();
        // make 2 offspring
        Generation[] offspring = this.mate(best);
        // replace worst with 2 offspring
        for (int i = 0; i < 2; ++i) {
            this.pop.getGens()[worst[i].getIdx()] = offspring[i];
        }

        Arrays.asList(this.pop.getGens()).forEach(g -> {
            if (ThreadLocalRandom.current().nextInt(11) == 5)
                this.mutate(g);
        });
        System.out.println(this.pop.getBestScore());
    }
}