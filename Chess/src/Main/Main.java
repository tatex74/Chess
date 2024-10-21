package Main;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static int currentColor;

    public static void main(String[] args) {
        JFrame window = new JFrame("Echec");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel gp = new GamePanel();

        window.setLayout(new BorderLayout());
        window.add(gp, BorderLayout.CENTER);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();
    }
}