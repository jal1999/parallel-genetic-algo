package main;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Station {

    /**
     * Type of the station
     */
    private final int type;

    /**
     * Affinities for this type of Station
     */
    private ConcurrentHashMap<Integer, Float> affinities;

    /**
     * Creates a new instance of Station
     */
    public Station(int type, ConcurrentHashMap<Integer, Float> affinities) {
        this.type = type;
        this.affinities = affinities;
    }

    /**
     * Retrieves the type of the station
     *
     * @return the type of the station
     */
    public int getType() {
        return type;
    }

    /**
     * Retrieves the affinity values for this type of station
     *
     * @return the affinity values for this type of station.
     */
    public ConcurrentHashMap<Integer, Float> getAffinities() {
        return affinities;
    }
}
