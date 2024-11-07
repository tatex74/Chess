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

    public static void main(String[] args) {
        new PanelManager();
    }

    public PanelManager() {
        frame = new Frame();
        titlePanel = new TitlePanel(this);
        frame.setContentPane(titlePanel);
        frame.setSize(WIDTH, HEIGHT);
    }

    public void launchGame(int gameMode) {
        gamePanel = new GamePanel(this, gameMode);
        frame.setContentPane(gamePanel);
        frame.revalidate();
    }

    public void returnToTitle() {
        frame.setContentPane(titlePanel);
        frame.revalidate();
    }
}
