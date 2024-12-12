import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.imageio.ImageIO;

public class SpaceExplorationAdventure extends JFrame implements GameGUI {
    private static final long serialVersionUID = 1L;
    
    private static final Color BABY_BLUE = new Color(137, 207, 240);
    private static final Color DEEP_RED = new Color(139, 0, 0);
    private static final Color DARKER_BLUE = new Color(100, 149, 237);
    private static final Color BACKGROUND_BLACK = new Color(25, 25, 25);
    private static final Color LIGHT_RED = new Color(255, 99, 71);

    
    private GameData gameData;
    private GameEngine gameEngine;

   
    private JTextArea gameConsole;
    private JTextField commandInput;
    private JLabel locationLabel;
    private JLabel scoreLabel;
    private JLabel missionLabel;
    private JProgressBar energyBar;
    private JLabel locationImageLabel;
    private JLabel logoLabel;
    private JPanel imagePanel;
    private JPanel sidebar;
    private JPanel statsPanel;
    private Map<String, JButton> quickActionButtons;

    public SpaceExplorationAdventure() {
        setTitle("Space Exploration Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));

        quickActionButtons = new HashMap<>();

        
        setupLookAndFeel();

        JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
        mainContainer.setBackground(BACKGROUND_BLACK);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainContainer);


        createTopPanel();
        createLeftPanel();
        createCenterPanel();
        createRightPanel();
        createBottomPanel();

        loadGameData();

     
        initializeGame();

      
        commandInput.addActionListener(e -> {
            processCommand(commandInput.getText());
            commandInput.setText("");
        });

        
        initializeQuickActions();

        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("ProgressBar.foreground", DEEP_RED);
            UIManager.put("ProgressBar.background", Color.DARK_GRAY);
            UIManager.put("TextArea.background", BACKGROUND_BLACK);
            UIManager.put("TextArea.foreground", LIGHT_RED);
            UIManager.put("TextField.background", BACKGROUND_BLACK);
            UIManager.put("TextField.foreground", LIGHT_RED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_BLACK);
        
        try {
            Image logoImg = ImageIO.read(new File("logo.jpg"))
                                 .getScaledInstance(200, 80, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(logoImg));
        } catch (IOException e) {
            logoLabel = new JLabel("Space Exploration Adventure", SwingConstants.CENTER);
            logoLabel.setForeground(DEEP_RED);
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        }
        
        statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(BACKGROUND_BLACK);
        
        scoreLabel = createStyledLabel("Score: 0");
        energyBar = createStyledProgressBar();
        
        statsPanel.add(scoreLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(createStyledLabel("Energy:"));
        statsPanel.add(energyBar);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }

    private void createLeftPanel() {
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(300, 0));
        imagePanel.setBackground(BACKGROUND_BLACK);
        imagePanel.setBorder(createStyledBorder("Location View"));

        locationImageLabel = new JLabel();
        locationImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        locationLabel = createStyledLabel("Current Location");
        locationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        imagePanel.add(locationLabel, BorderLayout.NORTH);
        imagePanel.add(locationImageLabel, BorderLayout.CENTER);
        
        add(imagePanel, BorderLayout.WEST);
    }

    private void createCenterPanel() {
        JPanel consolePanel = new JPanel(new BorderLayout(0, 10));
        consolePanel.setBackground(BACKGROUND_BLACK);
        consolePanel.setBorder(createStyledBorder("Mission Log"));

        gameConsole = new JTextArea();
        gameConsole.setEditable(false);
        gameConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
        gameConsole.setBackground(BACKGROUND_BLACK);
        gameConsole.setForeground(LIGHT_RED);
        gameConsole.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(gameConsole);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        consolePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(consolePanel, BorderLayout.CENTER);
    }

    private void createRightPanel() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BACKGROUND_BLACK);
        sidebar.setBorder(createStyledBorder("Status"));
        sidebar.setPreferredSize(new Dimension(200, 0));

        missionLabel = createStyledLabel("Current Mission: None");
        missionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] actions = {"Look", "Moves", "Mission", "Save Game"};
        for (String action : actions) {
            JButton button = createStyledButton(action);
            quickActionButtons.put(action, button);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        }

        sidebar.add(missionLabel);
        sidebar.add(Box.createVerticalStrut(20));
        quickActionButtons.values().forEach(button -> {
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10));
        });

        add(sidebar, BorderLayout.EAST);
    }

    private void createBottomPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(BACKGROUND_BLACK);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        commandInput = new JTextField();
        commandInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        commandInput.setCaretColor(LIGHT_RED);
        
        JButton submitButton = createStyledButton("Enter");
        submitButton.addActionListener(e -> processCommand(commandInput.getText()));

        inputPanel.add(createStyledLabel("Command:"), BorderLayout.WEST);
        inputPanel.add(commandInput, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void loadGameData() {
        try {
            gameData = new GameData();
            gameData.loadLocations("locations.txt");
            gameData.loadConnections("connections.txt");
            gameData.loadEnemies("enemies.txt");
        } catch (IOException e) {
            handleError("Error loading game data", e);
        }
    }

    private void initializeGame() {
        gameEngine = new GameEngine(gameData, this);
        gameEngine.startGame("earth");
        displayGameStatus();
    }

    private void initializeQuickActions() {
        quickActionButtons.get("Look").addActionListener(e -> processCommand("look"));
        quickActionButtons.get("Moves").addActionListener(e -> displayPossibleMoves());
        quickActionButtons.get("Mission").addActionListener(e -> displayGameStatus());
        quickActionButtons.get("Save Game").addActionListener(e -> handleSaveGame());
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LIGHT_RED);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private JProgressBar createStyledProgressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(150, 20));
        bar.setBorder(BorderFactory.createLineBorder(DEEP_RED));
        return bar;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(DEEP_RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(LIGHT_RED, 1));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(LIGHT_RED);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(DEEP_RED);
            }
        });
        return button;
    }

    private Border createStyledBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DEEP_RED),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                LIGHT_RED
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    private class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = DEEP_RED;
            trackColor = BACKGROUND_BLACK;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }

    private void processCommand(String command) {
        if (command == null || command.trim().isEmpty()) return;
        
        command = command.trim().toLowerCase();
        String[] parts = command.split("\\s+");
        
        if (parts.length == 0) return;
        String action = parts[0];

        try {
            switch (action) {
                case "move":
                    handleMoveCommand(parts);
                    break;
                case "look":
                    appendToConsole(gameEngine.getCurrentLocationDescription());
                    displayPossibleMoves();
                    break;
                case "moves":
                    displayPossibleMoves();
                    break;
                case "status":
                    displayGameStatus();
                    break;
                case "fight":
                    handleFightCommand(parts);
                    break;
                case "help":
                    displayHelp();
                    break;
                case "save":
                    handleSaveGame();
                    break;
                case "load":
                    handleLoadGame();
                    break;
                case "search":
                    handleSearchCommand(parts);
                    break;
                default:
                    appendToConsole("Unknown command. Type 'help' for commands.");
            }
        } catch (Exception e) {
            handleError("Error processing command", e);
        }
    }

    private void handleMoveCommand(String[] parts) {
        if (parts.length < 2) {
            appendToConsole("Move where? Specify a location.");
            displayPossibleMoves();
            return;
        }

        String destination = parts[1].toLowerCase();
        Set<String> possibleMoves = gameEngine.getPossibleMoves();
        
        if (!possibleMoves.contains(destination)) {
            appendToConsole("Cannot move to " + destination + " from current location.");
            displayPossibleMoves();
            return;
        }

        if (gameEngine.moveTo(destination)) {
            appendToConsole("Moved to " + destination);
            
            
            Mission currentMission = gameEngine.getCurrentMission();
            if (currentMission != null && 
                currentMission.getTargetLocation().equalsIgnoreCase(destination) && 
                currentMission.getTargetEnemy() == null) {
                
                appendToConsole("\n🎯 You've reached the mission target location!");
                if (!currentMission.isCompleted()) {
                    appendToConsole("Mission will complete automatically...");
                }
            }
        } else {
            appendToConsole("Failed to move to " + destination + " (insufficient energy?)");
            displayPossibleMoves();
        }
    }

    private void handleFightCommand(String[] parts) {
        if (parts.length < 2) {
            appendToConsole("Fight what? Specify an enemy.");
            return;
        }

        String enemyName = parts[1];
        Location currentLoc = gameData.locations.get(gameEngine.getCurrentLocation());
        
        if (currentLoc != null) {
            Optional<Enemy> enemy = currentLoc.getEnemies().stream()
                .filter(e -> e.getName().toLowerCase().contains(enemyName))
                .findFirst();
                
            if (enemy.isPresent()) {
                gameEngine.initiateCombat(enemy.get());
            } else {
                appendToConsole("No such enemy here: " + enemyName);
            }
        }
    }

    private void handleSearchCommand(String[] parts) {
        if (parts.length < 3) {
            appendToConsole("Usage: search <property> <value>");
            return;
        }

        String property = parts[1];
        String value = parts[2];
        List<String> locations = gameEngine.searchLocationsDFS(property, value);
        
        if (locations == null || locations.isEmpty()) {
            appendToConsole("No locations found with " + property + " = " + value);
        } else {
            appendToConsole("Found locations:");
            locations.forEach(loc -> appendToConsole("- " + loc));
        }
    }

    private void displayPossibleMoves() {
        Set<String> moves = gameEngine.getPossibleMoves();
        if (moves.isEmpty()) {
            appendToConsole("\nNo available moves from current location!");
        } else {
            appendToConsole("\nPossible moves from " + gameEngine.getCurrentLocation() + ":");
            moves.forEach(move -> appendToConsole("  - " + move));
        }
    }

    private void displayGameStatus() {
        appendToConsole("\nCurrent Status:");
        appendToConsole("Location: " + gameEngine.getCurrentLocation());
        appendToConsole("Energy: " + gameEngine.getEnergy());
        appendToConsole("Score: " + gameEngine.getScore());
        
        Mission currentMission = gameEngine.getCurrentMission();
        if (currentMission != null) {
            appendToConsole("\nCurrent Mission:");
            appendToConsole(currentMission.toString());
        } else {
            appendToConsole("\nNo active mission. Visit different locations to find missions!");
        }
        
        displayPossibleMoves();
    }

    private void handleSaveGame() {
        try {
            gameEngine.saveGame("savegame.dat");
            appendToConsole("Game saved successfully!");
        } catch (IOException e) {
            handleError("Error saving game", e);
        }
    }

    private void handleLoadGame() {
        try {
            gameEngine.loadGame("savegame.dat");
            appendToConsole("Game loaded successfully!");
        } catch (Exception e) {
            handleError("Error loading game", e);
        }
    }

    private void displayHelp() {
        appendToConsole("\nAvailable Commands:");
        appendToConsole("  move <location> - Move to a specified location");
        appendToConsole("  look           - Examine current location");
        appendToConsole("  moves          - Show available moves from current location");
        appendToConsole("  status         - Display current game status");
        appendToConsole("  fight <enemy>  - Engage in combat with an enemy");
        appendToConsole("  search <property> <value> - Search for locations");
        appendToConsole("  save           - Save current game");
        appendToConsole("  load           - Load saved game");
        appendToConsole("  help           - Show this help message");

        appendToConsole("\nHow to Complete Missions:");
        appendToConsole("1. For Exploration Missions (no enemy required):");
        appendToConsole("   - Simply reach the target location (e.g., 'move titan')");
        appendToConsole("   - Mission completes automatically on arrival");
        appendToConsole("   - A new mission is assigned immediately");
        
        appendToConsole("\n2. For Combat Missions (enemy required):");
        appendToConsole("   - Go to the target location (e.g., 'move jupiter')");
        appendToConsole("   - Fight the specific enemy (e.g., 'fight pirate')");
        appendToConsole("   - Use only the enemy's simple name (e.g., 'pirate' not 'space pirate')");
        appendToConsole("   - Mission completes after defeating the right enemy");

        appendToConsole("\nWays to Win:");
        appendToConsole("1. Complete 5 missions");
        appendToConsole("2. Reach 1000 points");
        appendToConsole("3. Discover all critical locations:");
        appendToConsole("   - Mars, Jupiter, Titan, Proxima Centauri b, Venus");
        
        appendToConsole("\nEnergy Costs:");
        appendToConsole("- Moving: 10 energy");
        appendToConsole("- Fighting: 15 energy");
        appendToConsole("- Searching: 5 energy");
        
        displayPossibleMoves();
    }

    private void handleError(String message, Exception e) {
        String errorMessage = message + ": " + e.getMessage();
        appendToConsole("ERROR: " + errorMessage);
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    
    @Override
    public void appendToConsole(String message) {
        if (gameConsole != null) {
            gameConsole.append(message + "\n");
            gameConsole.setCaretPosition(gameConsole.getDocument().getLength());
        }
    }

    @Override
    public void updateEnergyBar(int energy) {
        if (energyBar != null) {
            energyBar.setValue(energy);
        }
    }

    @Override
    public void gameOver() {
        appendToConsole("Game Over! You've run out of energy.");
        commandInput.setEnabled(false);
        quickActionButtons.values().forEach(button -> button.setEnabled(false));
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Game Over! Would you like to start a new game?",
            "Game Over",
            JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        
        commandInput.setEnabled(true);
        quickActionButtons.values().forEach(button -> button.setEnabled(true));
        gameConsole.setText("");
        
        
        loadGameData();
        initializeGame();
    }

    @Override
    public void updateLocation(String location) {
        locationLabel.setText("Location: " + location);
        updateLocationImage(location);
    }

    @Override
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void updateMission(String missionText) {
        missionLabel.setText(missionText);
    }

    private void updateLocationImage(String location) {
        String imagePath = "images/" + location + ".jpg";
        try {
            Image img = ImageIO.read(new File(imagePath));
            locationImageLabel.setIcon(new ImageIcon(img.getScaledInstance(
                280, 200, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            locationImageLabel.setIcon(null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpaceExplorationAdventure::new);
    }
}