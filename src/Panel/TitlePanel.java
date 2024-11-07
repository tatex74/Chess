package Panel;

import Logic.Game;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    private final JButton playerVsPlayerButton;
    private final JButton playerVsComputerButton;
    private final JButton loadGameButton;


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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Garamond", Font.PLAIN, 50));
        g2.setColor(PanelManager.textColor);
        g2.drawString("Echec", 690, 200);
    }

    public void setActionListener(PanelManager panelManager) {
        playerVsPlayerButton.addActionListener(_ -> panelManager.launchGame(Game.PLAYERVSPLAYER));
        playerVsComputerButton.addActionListener(_ -> panelManager.launchGame(Game.PLAYERVSCOMPUTER));
        loadGameButton.addActionListener(_ -> panelManager.launchGame(Game.LOADGAME));
    }
}
