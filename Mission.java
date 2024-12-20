

import java.io.Serializable;

import java.io.Serializable;

public class Mission implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String targetLocation;
    private final String targetEnemy;
    private final String description;
    private final int reward;
    private boolean completed;

    public Mission(String title, String targetLocation, String targetEnemy, 
                  String description, int reward) {
        this.title = title;
        this.targetLocation = targetLocation.toLowerCase();
        this.targetEnemy = targetEnemy;
        this.description = description;
        this.reward = reward;
        this.completed = false;
    }

    public boolean isComplete(String currentLocation, Enemy defeatedEnemy) {
        if (!completed) {
            boolean locationMatches = currentLocation.equalsIgnoreCase(targetLocation);
            boolean enemyMatches = targetEnemy == null || 
                                 (defeatedEnemy != null && 
                                  targetEnemy.equalsIgnoreCase(defeatedEnemy.getName()));
            completed = locationMatches && enemyMatches;
        }
        return completed;
    }

    public String getTitle() { return title; }
    public String getTargetLocation() { return targetLocation; }
    public String getTargetEnemy() { return targetEnemy; }
    public String getDescription() { return description; }
    public int getReward() { return reward; }
    public boolean isCompleted() { return completed; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Target Location: ").append(targetLocation);
        if (targetEnemy != null) {
            sb.append("\nTarget Enemy: ").append(targetEnemy);
        }
        sb.append("\nReward: ").append(reward).append(" points");
        return sb.toString();
    }
}