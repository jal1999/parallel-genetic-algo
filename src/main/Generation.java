package main;

public class Generation implements Comparable<Generation> {

    /** Current score of the Generation */
    public double score;

    /** Index of the Generation in overarching Population's data store */
    private final int idx;

    /** The current floor configuration for the Generation */
    private Station[][] floorPlan;

    /** Creates a new Generation */
    public Generation(int idx, Station[][] floorPlan) {
        this.score = -1;
        this.idx = idx;
        this.floorPlan = floorPlan;
    }

    /**
     * @return the current score of the Generation
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the value which the current score of the Generation will be set to.
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @return index of the Generation in the overarching Population's data store.
     */
    public int getIdx() {
        return idx;
    }

    /**
     * @return the current floor configuration of the Generation
     */
    public Station[][] getFloorPlan() {
        return floorPlan;
    }

    /**
     * @param floorPlan the floor configuration in which to set this Generation's floorPlan to.
     */
    public void setFloorPlan(Station[][] floorPlan) {
        this.floorPlan = floorPlan;
    }

    @Override
    public int compareTo(Generation g) {
        return this.score > g.score ? 1 : 0;
    }
}
