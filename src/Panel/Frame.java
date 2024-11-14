package Panel;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    /**
     * Constructs the main application frame with a fixed size and title,
     * sets default close operation, layout, and centers the window on screen.
     */
    public Frame() {
        super("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setVisible(true);
        setSize(1500, 900);
        setLocationRelativeTo(null);
    }
}
