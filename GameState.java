// File: GameState.java
import java.io.*;
import java.util.*;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private String currentLocation;
    private int energy;
    private int score;
    private Mission currentMission;
    private Set<String> visitedLocations;
    private Map<String, Location> locations;
    
    public GameState(String currentLocation, int energy, int score, 
                    Mission currentMission, Set<String> visitedLocations,
                    Map<String, Location> locations) {
        this.currentLocation = currentLocation;
        this.energy = energy;
        this.score = score;
        this.currentMission = currentMission;
        this.visitedLocations = new HashSet<>(visitedLocations);
        this.locations = new HashMap<>(locations);
    }
    
    public String getCurrentLocation() { return currentLocation; }
    public int getEnergy() { return energy; }
    public int getScore() { return score; }
    public Mission getCurrentMission() { return currentMission; }
    public Set<String> getVisitedLocations() { return new HashSet<>(visitedLocations); }
    public Map<String, Location> getLocations() { return new HashMap<>(locations); }
}