package Panel;

import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelManager {
    public static final int WIDTH = 1500;
    public static final int HEIGHT = 870;
    Frame frame;
    TitlePanel titlePanel;
    GamePanel gamePanel;

    public static Color backGroundColor = new Color(40, 44, 52);
    public static Color textColor = new Color(220, 220, 220);

    /**
     * Main entry point of the application. Creates an instance of PanelManager to initialize the UI.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new PanelManager();
    }

    /**
     * Constructs the PanelManager, initializes the main frame, and sets the title panel as the default view.
     */
    public PanelManager() {
        frame = new Frame();
        titlePanel = new TitlePanel(this);
        frame.setContentPane(titlePanel);
        frame.setSize(WIDTH, HEIGHT);
    }

    /**
     * Launches the game by creating a GamePanel instance with the specified game mode
     * and setting it as the content pane of the main frame.
     *
     * @param gameMode The game mode to start (e.g., player vs. player).
     */
    public void launchGame(int gameMode) {
        gamePanel = new GamePanel(this, gameMode);
        frame.setContentPane(gamePanel);
        frame.revalidate();
    }

    /**
     * Returns to the title panel by setting it as the content pane of the main frame.
     */
    public void returnToTitle() {
        frame.setContentPane(titlePanel);
        frame.revalidate();
    }
}
