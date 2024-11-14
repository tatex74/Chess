package Panel;

import Logic.Game;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    private final JButton playerVsPlayerButton;
    private final JButton playerVsComputerButton;
    private final JButton loadGameButton;

    /**
     * Constructs the title panel with buttons for selecting game modes and loading a saved game.
     * Sets up button positions, background color, and initializes action listeners.
     *
     * @param panelManager The PanelManager instance that handles panel transitions.
     */
    public TitlePanel(PanelManager panelManager) {

        setSize(PanelManager.WIDTH, PanelManager.HEIGHT);
        setLayout(null);

        setBackground(PanelManager.backGroundColor);

        playerVsPlayerButton = new Button("Joueur contre Joueur");
        playerVsComputerButton = new Button("Joueur contre Ordinateur");
        loadGameButton = new Button("Charger une Partie");

        playerVsPlayerButton.setBounds(600, 300, 300, 50);
        playerVsComputerButton.setBounds(600, 400, 300, 50);
        loadGameButton.setBounds(600, 500, 300, 50);

        add(playerVsPlayerButton);
        add(playerVsComputerButton);
        add(loadGameButton);

        setActionListener(panelManager);
    }

    /**
     * Paints the title and customizes the graphical settings, including
     * font properties, to render the main title text.
     *
     * @param g The Graphics object used to draw on the panel.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Garamond", Font.PLAIN, 50));
        g2.setColor(PanelManager.textColor);
        g2.drawString("Echec", 690, 200);
    }

    /**
     * Sets action listeners for the buttons, defining actions for starting a player vs. player game,
     * a player vs. computer game, or loading a previously saved game.
     *
     * @param panelManager The PanelManager instance that facilitates panel transitions.
     */
    public void setActionListener(PanelManager panelManager) {
        playerVsPlayerButton.addActionListener(_ -> panelManager.launchGame(Game.PLAYERVSPLAYER));
        playerVsComputerButton.addActionListener(_ -> panelManager.launchGame(Game.PLAYERVSCOMPUTER));
        loadGameButton.addActionListener(_ -> panelManager.launchGame(Game.LOADGAME));
    }
}
