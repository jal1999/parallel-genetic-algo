package main;

public class Population {

    /** Data store of Generations in the population */
    private Generation[] gens;

    /** The best floor configuration so far */
    private Station[][] bestFloorPlan;

    /** The best generation score so far */
    private double bestScore;

    /** Creates a new Population */
    public Population() {
        this.bestFloorPlan = null;
    }

    /**
     * @return the data store holding the Generations for the Population
     */
    public Generation[] getGens() {
        return gens;
    }

    /**
     * @param gens new set of Generations to be the value of this Population's generations.
     */
    public void setGens(Generation[] gens) {
        this.gens = gens;
    }

    /**
     * @return the Generation with the best score thus far in the Population.
     */
    public Station[][] getBestFloorPlan() {
        return this.bestFloorPlan;
    }

    /**
     * @param bestFloorPlan new best floor configuration thus far.
     */
    public void setBestFloorPlan(Station[][] bestFloorPlan) {
        this.bestFloorPlan = bestFloorPlan;
    }

    /**
     * @return the best score thus far.
     */
    public double getBestScore() {
        return this.bestScore;
    }

    /**
     * @param score new best score
     */
    public void setBestScore(double score) {
        this.bestScore = score;
    }
}
