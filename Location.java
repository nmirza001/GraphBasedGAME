
import java.io.Serializable;
import java.util.*;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private String description;
    private final List<Enemy> enemies;
    private boolean visited;
    private final Map<String, String> properties;

    public Location(String name) {
        this.name = name.toLowerCase();
        this.enemies = new ArrayList<>();
        this.visited = false;
        this.properties = new HashMap<>();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    
    public List<Enemy> getEnemies() { return new ArrayList<>(enemies); }
    public void addEnemy(Enemy enemy) { enemies.add(enemy); }
    public void removeEnemy(Enemy enemy) { enemies.remove(enemy); }
    public boolean hasEnemies() { return !enemies.isEmpty(); }
    
    public void addProperty(String key, String value) { properties.put(key, value); }
    public String getProperty(String key) { return properties.get(key); }
    public Map<String, String> getAllProperties() { return new HashMap<>(properties); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Location: ").append(name)
          .append("\nDescription: ").append(description);
        
        if (!enemies.isEmpty()) {
            sb.append("\nEnemies present: ");
            enemies.forEach(enemy -> sb.append("\n- ").append(enemy.getName()));
        }
        
        if (!properties.isEmpty()) {
            sb.append("\nProperties:");
            properties.forEach((key, value) -> 
                sb.append("\n- ").append(key).append(": ").append(value));
        }
        
        return sb.toString();
    }
}
