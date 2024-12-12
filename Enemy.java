// File: Enemy.java
// Author: Nasir Mirza & Conor
// Date: December 9, 2024

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents an enemy in a game with attributes such as health, attack power, 
 * weaknesses, and defeated status.
 */
public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;            // Name of the enemy
    private int maxHealth;          // Maximum health points of the enemy
    private int currentHealth;      // Current health points of the enemy
    private int attackPower;        // Attack power of the enemy
    private List<String> weaknesses; // List to store weaknesses of the enemy
    private boolean defeated;       // Whether the enemy is defeated or not

    /**
     * Constructs an Enemy object with specified name, health, and attack power.
     * 
     * @param name The name of the enemy.
     * @param health The maximum health of the enemy.
     * @param attackPower The attack power of the enemy.
     */
    public Enemy(String name, int health, int attackPower) {
        this.name = name;
        this.maxHealth = health;
        this.currentHealth = health;
        this.attackPower = attackPower;
        this.weaknesses = new ArrayList<>();
        this.defeated = false;
    }

    /**
     * Reduces the enemy's health by the specified damage. If health reaches 0 or below, 
     * the enemy is marked as defeated.
     * 
     * @param damage The amount of damage to deal to the enemy.
     */
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            defeated = true;
        }
    }

    /**
     * Gets the name of the enemy.
     * 
     * @return The name of the enemy.
     */
    public String getName() { 
        return name; 
    }

    /**
     * Gets the current health of the enemy.
     * 
     * @return The current health of the enemy.
     */
    public int getCurrentHealth() { 
        return currentHealth; 
    }

    /**
     * Gets the maximum health of the enemy.
     * 
     * @return The maximum health of the enemy.
     */
    public int getMaxHealth() { 
        return maxHealth; 
    }

    /**
     * Gets the attack power of the enemy.
     * 
     * @return The attack power of the enemy.
     */
    public int getAttackPower() { 
        return attackPower; 
    }

    /**
     * Checks if the enemy is defeated.
     * 
     * @return True if the enemy is defeated, false otherwise.
     */
    public boolean isDefeated() { 
        return defeated; 
    }

    /**
     * Calculates the percentage of health remaining for the enemy.
     * 
     * @return The health percentage (from 0 to 100).
     */
    public int getHealthPercentage() {
        return (int)((currentHealth * 100.0f) / maxHealth);
    }

    /**
     * Returns a string representation of the enemy's status, including 
     * its name, current health, maximum health, and attack power.
     * 
     * @return A string representing the enemy.
     */
    @Override
    public String toString() {
        return String.format("%s (Health: %d/%d, Attack: %d)", 
            name, currentHealth, maxHealth, attackPower);
    }
}
