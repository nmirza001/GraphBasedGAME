import java.util.*;
import java.io.*;

public class GameData implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<String, Location> locations;
    Map<String, Set<String>> connections;
    List<Mission> missions; 

    public GameData() {
        locations = new HashMap<>();
        connections = new HashMap<>();
        missions = new ArrayList<>();
        createMissions(); 
    }

    public void loadLocations(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String locationName = line.trim().toLowerCase();
                Location location = new Location(locationName);
                
                line = reader.readLine();
                if (line != null) {
                    location.setDescription(line.trim());
                }
                
                locations.put(locationName, location);
            }
        }
    }

    public void loadConnections(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String locationName = line.trim().toLowerCase();
                if (!locations.containsKey(locationName)) {
                    System.err.println("Location " + locationName + " not found. Skipping.");
                    continue;
                }
                
                String countLine = reader.readLine();
                if (countLine != null) {
                    try {
                        int connectionCount = Integer.parseInt(countLine.trim());
                        Set<String> connectedLocations = new HashSet<>();
                        
                        for (int i = 0; i < connectionCount; i++) {
                            String connected = reader.readLine().trim().toLowerCase();
                            connectedLocations.add(connected);
                        }
                        
                        connections.put(locationName, connectedLocations);
                        reader.readLine(); 
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid connection count for " + locationName);
                    }
                }
            }
        }
    }

    public void loadEnemies(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String locationName = line.trim().toLowerCase();
                if (locationName.isEmpty()) continue;
                
                if (!locations.containsKey(locationName)) {
                    System.err.println("Location " + locationName + " not found for enemy");
                    continue;
                }
                
                Location location = locations.get(locationName);
                String enemyName = reader.readLine().trim();
                int health = Integer.parseInt(reader.readLine().trim());
                int attackPower = Integer.parseInt(reader.readLine().trim());
                
                Enemy enemy = new Enemy(enemyName, health, attackPower);
                location.addEnemy(enemy);
            }
        }
    }

    private void createMissions() {
        
        missions.add(new Mission("Space Pirate Hunt", "jupiter", "Pirate", 
            "Eliminate the Pirate terrorizing Jupiter's shipping lanes.", 300));
        missions.add(new Mission("Scout Elimination", "ganymede", "Scout", 
            "Neutralize the Scout before it can report back to its fleet.", 350));
        missions.add(new Mission("Colony Defense", "proxima_centauri_b", "Invader", 
            "Defend the colony from the Invader.", 400));
        missions.add(new Mission("Quantum Crisis", "neptune", "Quantum", 
            "Stop the Quantum entity threatening deep space operations.", 450));
        missions.add(new Mission("AI Containment", "kepler_186f", "DefenseAI", 
            "Contain the rogue DefenseAI system.", 375));
        
        
        missions.add(new Mission("Methane Study", "titan", null, 
            "Study the Beast's territory in Titan's methane lakes.", 200));
        missions.add(new Mission("Mars Investigation", "mars", null, 
            "Investigate the Warrior's impact on Mars ruins.", 250));
        missions.add(new Mission("Venus Analysis", "venus", null, 
            "Research the Plasma's effect on Venus's atmosphere.", 275));
        missions.add(new Mission("Europa Discovery", "europa", null,
            "Study the Leviathan's underwater habitat.", 225));
        missions.add(new Mission("Saturn Survey", "saturn", null,
            "Assess the Raider's damage to Saturn's rings.", 325));
    }

    public Mission getRandomMission() {
        if (missions.isEmpty()) {
            createMissions();
        }
        if (!missions.isEmpty()) {
            return missions.get(new Random().nextInt(missions.size()));
        }
        return null;
    }
    
    public List<Mission> getMissions() {
        return missions;
    }
}