package Panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HistorizePanel extends JScrollPane {
    public static final int WIDTH = 250;
    public static final int HEIGHT = 500;

    public final Color background = new Color(74, 81, 92);
    public final Color white = new Color(240, 244, 247);
    public final Color black = new Color(24, 26, 28);

    private JPanel scrollablePanel;
    public boolean isWhite = true;
    public int nbMove = 0;

    public HistorizePanel() {
        scrollablePanel = new JPanel();
        scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
        scrollablePanel.setBackground(background); // Fond clair
        scrollablePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Marges internes

        // Configuration du JScrollPane
        this.setViewportView(scrollablePanel);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Historique des mouvements",
                0,
                0,
                new Font("Arial", Font.BOLD, 14),
                new Color(50, 50, 50)
        ));

        this.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        this.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
    }


    /**
     * Adds a new move to the panel. Each move is displayed as a stylized label,
     * alternating between two background colors.
     *
     * @param move The text of the move to be added.
     */
    public void addMove(String move) {
        JLabel moveLabel = new JLabel(move);
        moveLabel.setOpaque(true);
        if (isWhite) {
            moveLabel.setBackground(white);
            moveLabel.setForeground(black);
        } else {
            moveLabel.setBackground(black);
            moveLabel.setForeground(white);
        }
        moveLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        moveLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10) // Espacement interne
        ));

        scrollablePanel.add(moveLabel);
        scrollablePanel.add(Box.createRigidArea(new Dimension(0, 5))); // Espacement entre les éléments
        scrollablePanel.revalidate();
        scrollablePanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = this.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });

        isWhite = !isWhite;
        nbMove += 1;
    }


    /**
     * Clears all moves from the panel and resets the move counter.
     */
    public void clearPanel() {
        nbMove = 0;
        scrollablePanel.removeAll();
        scrollablePanel.revalidate();
        scrollablePanel.repaint();
    }

    private static class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        /**
         * Configures the colors of the scroll bar track and thumb.
         */
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(150, 150, 150);
            this.trackColor = new Color(230, 230, 230);
        }

        /**
         * Creates an invisible button for the decrease direction.
         *
         * @param orientation The orientation of the button.
         * @return A JButton with zero dimensions.
         */
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        /**
         * Creates an invisible button for the increase direction.
         *
         * @param orientation The orientation of the button.
         * @return A JButton with zero dimensions.
         */
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        /**
         * Creates an invisible button with zero size.
         *
         * @return A JButton with no visible size.
         */
        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
}
