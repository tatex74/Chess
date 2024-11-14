package Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {
    private Color defaultColor = new Color(70, 130, 180); // Bleu initial
    private Color hoverColor = new Color(100, 149, 237);  // Bleu clair
    private Color pressedColor = new Color(65, 105, 225); // Bleu foncé

    /**
     * Constructs a custom-styled button with rounded corners, specific color states,
     * and a hover/pressed color effect.
     *
     * @param text The label text displayed on the button.
     */
    public Button(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);  // Important pour afficher la couleur de fond
        setBackground(defaultColor); // Couleur initiale
        setForeground(Color.WHITE); // Couleur du texte
        setBorder(BorderFactory.createEmptyBorder()); // Supprime la bordure visuelle

        // Effet de survol (hover)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
            }
        });
    }

    /**
     * Paints the button component with rounded corners and the background color
     * defined by the button’s current state (default, hover, or pressed).
     *
     * @param g The Graphics object used to render the button.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Dessiner un bouton arrondi avec la couleur de fond définie
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Coins arrondis
        super.paintComponent(g);
    }

    /**
     * Overrides the default setContentAreaFilled method to prevent filling
     * the button background with the default look and feel, allowing for custom
     * background colors.
     *
     * @param b Ignored to maintain custom styling.
     */
    @Override
    public void setContentAreaFilled(boolean b) {
        // Ne rien faire ici pour garder l'arrière-plan personnalisé
    }
}
