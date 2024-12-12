import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class GameEngine {

    private final GameData gameData;
    private final GameGUI gameGUI;
    private String currentLocation;
    private int energy;
    private int score;
    private Mission currentMission;
    private Set<String> visitedLocations;
    private Random random;
    
   
    private static final int INITIAL_ENERGY = 100;
    private static final int MOVE_ENERGY_COST = 10;
    private static final int COMBAT_ENERGY_COST = 15;
    private static final int SEARCH_ENERGY_COST = 5;
    private static final String SAVE_DIR = "saves/";
    
   
    private static final int MISSIONS_REQUIRED_FOR_WIN = 5;
    private static final int SCORE_REQUIRED_FOR_WIN = 1000;
    private static final Set<String> CRITICAL_LOCATIONS = new HashSet<>(Arrays.asList(
        "mars", "jupiter", "titan", "proxima_centauri_b", "venus"
    ));
    private int completedMissions = 0;
    private Set<String> discoveredCriticalLocations;

    public GameEngine(GameData gameData, GameGUI gameGUI) {
        this.gameData = gameData;
        this.gameGUI = gameGUI;
        this.energy = INITIAL_ENERGY;
        this.score = 0;
        this.visitedLocations = new HashSet<>();
        this.discoveredCriticalLocations = new HashSet<>();
        this.random = new Random();
    }

    public void startGame(String startLocation) {
        currentLocation = startLocation.toLowerCase();
        visitedLocations.add(currentLocation);
        Location location = gameData.locations.get(currentLocation);

        if (location != null) {
            location.setVisited(true);
            gameGUI.updateLocation(location.getName());
            gameGUI.appendToConsole("Starting exploration at " + location.getName());
            gameGUI.appendToConsole(location.getDescription());
        }

        generateMission();
        gameGUI.updateScore(score);
        gameGUI.updateEnergyBar(energy);
    }

    private void generateMission() {
        if (currentMission == null) {
            currentMission = gameData.getRandomMission();
            if (currentMission != null) {
                gameGUI.updateMission("Current Mission: " + currentMission.getTitle());
                gameGUI.appendToConsole("\nNew Mission Acquired!\n" + currentMission.toString());
            }
        }
    }

    public boolean moveTo(String destination) {
        destination = destination.toLowerCase();
        Set<String> possibleMoves = getPossibleMoves();
        
        if (!possibleMoves.contains(destination)) {
            gameGUI.appendToConsole("Cannot move to " + destination + " from current location.");
            return false;
        }

        if (energy < MOVE_ENERGY_COST) {
            gameGUI.appendToConsole("Insufficient energy for movement!");
            return false;
        }

        currentLocation = destination;
        energy -= MOVE_ENERGY_COST;
        visitedLocations.add(destination);
        gameData.locations.get(destination).setVisited(true);
        
        
        if (CRITICAL_LOCATIONS.contains(destination)) {
            if (!discoveredCriticalLocations.contains(destination)) {
                discoveredCriticalLocations.add(destination);
                gameGUI.appendToConsole("\n🌟 You've discovered a critical location: " + destination + "!");
                if (discoveredCriticalLocations.size() == CRITICAL_LOCATIONS.size()) {
                    gameGUI.appendToConsole("\n📍 You've discovered all critical locations in the galaxy!");
                }
            }
        }

        gameGUI.updateEnergyBar(energy);
        gameGUI.updateLocation(destination);
        handleLocationArrival();
        checkVictoryConditions();
        
        return true;
    }

    private void checkVictoryConditions() {
        boolean hasEnoughMissions = completedMissions >= MISSIONS_REQUIRED_FOR_WIN;
        boolean hasEnoughScore = score >= SCORE_REQUIRED_FOR_WIN;
        boolean hasDiscoveredAllCritical = discoveredCriticalLocations.containsAll(CRITICAL_LOCATIONS);
        
        if (hasEnoughMissions || hasEnoughScore || hasDiscoveredAllCritical) {
            triggerVictory(hasEnoughMissions, hasEnoughScore, hasDiscoveredAllCritical);
        }
    }

    private void triggerVictory(boolean missions, boolean score, boolean exploration) {
        StringBuilder victoryMessage = new StringBuilder("\n🎉 CONGRATULATIONS! You've won the game! 🎉\n\n");
        victoryMessage.append("Victory achieved through:\n");
        
        if (missions) {
            victoryMessage.append("- Completing ").append(completedMissions)
                         .append(" missions (required: ").append(MISSIONS_REQUIRED_FOR_WIN).append(")\n");
        }
        if (score) {
            victoryMessage.append("- Achieving a score of ").append(this.score)
                         .append(" (required: ").append(SCORE_REQUIRED_FOR_WIN).append(")\n");
        }
        if (exploration) {
            victoryMessage.append("- Discovering all critical locations in the galaxy\n");
        }
        
        victoryMessage.append("\nFinal Statistics:\n")
                     .append("- Total Score: ").append(this.score).append("\n")
                     .append("- Missions Completed: ").append(completedMissions).append("\n")
                     .append("- Locations Discovered: ").append(visitedLocations.size()).append("\n")
                     .append("- Energy Remaining: ").append(energy).append("\n");
                     
        gameGUI.appendToConsole(victoryMessage.toString());
        
        int choice = JOptionPane.showConfirmDialog(null,
            "Congratulations! You've won the game!\nWould you like to start a new game?",
            "Victory!",
            JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    public void initiateCombat(Enemy enemy) {
        if (energy < COMBAT_ENERGY_COST) {
            gameGUI.appendToConsole("Insufficient energy for combat!");
            return;
        }

        gameGUI.appendToConsole("Engaging in combat with " + enemy.getName());
        
        while (!enemy.isDefeated() && energy >= COMBAT_ENERGY_COST) {
           
            int playerDamage = calculatePlayerDamage();
            enemy.takeDamage(playerDamage);
            gameGUI.appendToConsole("You deal " + playerDamage + " damage to " + enemy.getName());
            
            
            if (!enemy.isDefeated()) {
                int enemyDamage = calculateEnemyDamage(enemy);
                energy -= enemyDamage;
                gameGUI.appendToConsole(enemy.getName() + " deals " + enemyDamage + " damage");
                gameGUI.updateEnergyBar(energy);
            }
            
            gameGUI.appendToConsole(String.format(
                "Status - Enemy Health: %d%%, Your Energy: %d",
                enemy.getHealthPercentage(), 
                energy
            ));
        }

        if (enemy.isDefeated()) {
            handleCombatVictory(enemy);
        } else {
            handleCombatDefeat();
        }
    }

    private int calculatePlayerDamage() {
        int baseDamage = 15 + random.nextInt(10);
        boolean criticalHit = random.nextDouble() < 0.2; // 20% chance
        return criticalHit ? baseDamage * 2 : baseDamage;
    }

    private int calculateEnemyDamage(Enemy enemy) {
        return Math.max(5, enemy.getAttackPower() + random.nextInt(10) - 5);
    }

    private void handleCombatVictory(Enemy enemy) {
        gameGUI.appendToConsole("Victory! " + enemy.getName() + " has been defeated!");
        score += 100;
        gameGUI.updateScore(score);
        gameData.locations.get(currentLocation).removeEnemy(enemy);
        
        // Check if this combat completes a mission
        if (currentMission != null && currentMission.isComplete(currentLocation, enemy)) {
            completeMission();
            generateMission();
            displayGameStatus();
        }
        
        checkVictoryConditions();
    }

    private void handleCombatDefeat() {
        gameGUI.appendToConsole("Combat failed - insufficient energy!");
        if (energy <= 0) {
            gameGUI.gameOver();
        }
    }

    private void completeMission() {
        if (currentMission != null) {
            score += currentMission.getReward();
            completedMissions++;
            gameGUI.appendToConsole(String.format(
                "\n🎉 Mission Complete: %s\nReward: %d points\nTotal Missions Completed: %d/%d",
                currentMission.getTitle(),
                currentMission.getReward(),
                completedMissions,
                MISSIONS_REQUIRED_FOR_WIN
            ));
            
            
            gameGUI.updateScore(score);
            currentMission = null;
            gameGUI.updateMission("Current Mission: None");
            
            checkVictoryConditions();
        }
    }

    private void handleLocationArrival() {
        Location location = gameData.locations.get(currentLocation);
        gameGUI.appendToConsole("\nArrived at " + location.getName());
        gameGUI.appendToConsole(location.getDescription());
        
        if (location.hasEnemies()) {
            gameGUI.appendToConsole("\nWarning: Enemies detected!");
            location.getEnemies().forEach(enemy -> 
                gameGUI.appendToConsole("- " + enemy.toString())
            );
        }

        
        if (currentMission != null && 
            currentMission.getTargetLocation().equalsIgnoreCase(currentLocation) && 
            currentMission.getTargetEnemy() == null) {
            gameGUI.appendToConsole("🎯 You've reached the mission target location!");
            completeMission();
            generateMission();
            displayGameStatus();
        }
    }

    private void displayGameStatus() {
        if (currentMission != null) {
            gameGUI.appendToConsole("\nCurrent Mission Status:");
            gameGUI.appendToConsole(currentMission.toString());
        }
    }

    public List<String> searchLocationsDFS(String propertyKey, String propertyValue) {
        if (energy < SEARCH_ENERGY_COST) {
            gameGUI.appendToConsole("Insufficient energy for search operation!");
            return null;
        }

        Set<String> visited = new HashSet<>();
        List<String> foundLocations = new ArrayList<>();
        dfsSearch(currentLocation, propertyKey, propertyValue, visited, foundLocations);
        
        energy -= SEARCH_ENERGY_COST;
        gameGUI.updateEnergyBar(energy);
        return foundLocations;
    }

    private void dfsSearch(String location, String propertyKey, String propertyValue, 
                         Set<String> visited, List<String> results) {
        visited.add(location);
        
        Location loc = gameData.locations.get(location);
        if (loc != null && propertyValue.equals(loc.getProperty(propertyKey))) {
            results.add(location);
        }
        
        Set<String> neighbors = gameData.connections.getOrDefault(location, new HashSet<>());
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                dfsSearch(neighbor, propertyKey, propertyValue, visited, results);
            }
        }
    }

    public void saveGame(String filename) throws IOException {
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(SAVE_DIR + filename))) {
            GameState state = new GameState(
                currentLocation,
                energy,
                score,
                currentMission,
                visitedLocations,
                gameData.locations
            );
            out.writeObject(state);
        }
    }

    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(SAVE_DIR + filename))) {
            GameState state = (GameState) in.readObject();
            
            currentLocation = state.getCurrentLocation();
            energy = state.getEnergy();
            score = state.getScore();
            currentMission = state.getCurrentMission();
            visitedLocations = state.getVisitedLocations();
            gameData.locations = state.getLocations();
            
            gameGUI.updateLocation(currentLocation);
            gameGUI.updateEnergyBar(energy);
            gameGUI.updateScore(score);
            handleLocationArrival();
        }
    }

    private void restartGame() {
        energy = INITIAL_ENERGY;
        score = 0;
        completedMissions = 0;
        discoveredCriticalLocations.clear();
        visitedLocations.clear();
        currentMission = null;
        startGame("earth");
    }

    
    public String getCurrentLocation() { return currentLocation; }
    public String getCurrentLocationDescription() {
        Location loc = gameData.locations.get(currentLocation);
        return loc != null ? loc.getDescription() : "Unknown location.";
    }
    public int getEnergy() { return energy; }
    public int getScore() { return score; }
    public Mission getCurrentMission() { return currentMission; }
    public Set<String> getVisitedLocations() { return new HashSet<>(visitedLocations); }
    public Set<String> getPossibleMoves() {
        return new HashSet<>(gameData.connections.getOrDefault(currentLocation, new HashSet<>()));
    }
    public int getCompletedMissions() { return completedMissions; }
}