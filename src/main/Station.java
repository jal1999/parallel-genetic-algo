package main;

public class Station {

    /**
     * Type of the station
     */
    private final int type;

    /**
     * Creates a new instance of Station
     */
    public Station(int type) {
        this.type = type;
    }

    /**
     * Retrieves the type of the station
     *
     * @return the type of the station
     */
    public int getType() {
        return type;
    }
}
