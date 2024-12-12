// File: GameGUI.java
public interface GameGUI {
    void appendToConsole(String message);
    void updateEnergyBar(int energy);
    void gameOver();
    void updateLocation(String location);
    void updateScore(int score);
    void updateMission(String missionText);
}

