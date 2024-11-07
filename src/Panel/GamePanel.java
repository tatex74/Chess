package Panel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Logic.*;
import Piece.*;

public class GamePanel extends JPanel implements Runnable { //La classe GamePanel hérite de JPanel (composant graphique Swing) et implémente l'interface Runnable, ce qui permet d'exécuter du code dans un thread séparé. La méthode run() sera appelée quand le thread démarre, mais ici elle est vide, en attente de l'ajout du code du jeu.
    public static final int FPS = 60;
    private Thread gameThread;
    private Board board;
    private final String WHITE = "Blanc";
    private final String BLACK = "Black";
    private String status = "En cours";
    private String currentPlayer;

    private boolean repaintTeam = false;

    //boutons
    private final JButton abandonButton;
    private final JButton titleScreenButton;
    private final JButton resetButton;
    private final JButton saveButton;

    Mouse mouse;
    Game game;


    public GamePanel(PanelManager panelManager, int gameMode) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(PanelManager.backGroundColor);
        setLayout(null); // Utiliser un layout null pour positionner les boutons manuellement

        // Initialiser les boutons
        abandonButton = new Button("Abandonner");
        titleScreenButton = new Button("Retour à l'écran titre");
        resetButton = new Button("Réinitialiser la partie");
        saveButton = new Button("Sauvegarder la partie");

        // Positionner les boutons
        abandonButton.setBounds(1200, 600, 200, 40);
        titleScreenButton.setBounds(1200, 650, 200, 40);
        resetButton.setBounds(1200, 700, 200, 40);
        saveButton.setBounds(1200, 750, 200, 40);

        // Ajouter les boutons au panel
        add(abandonButton);
        add(titleScreenButton);
        add(resetButton);
        add(saveButton);

        game = new Game(this);
        game.launchGame(gameMode);

        currentPlayer = (game.currentColor == Game.WHITE) ? WHITE : BLACK;

        gameThread = new Thread(this);
        gameThread.start();

        board = new Board();

        mouse = new Mouse();
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        abandonButton.addActionListener(_ -> game.abandonGame());
        titleScreenButton.addActionListener(_ -> panelManager.returnToTitle());
        resetButton.addActionListener(_ -> game.resetGame());
        saveButton.addActionListener(_ -> Save.saveGame(game));
    }

    @Override
    public void run() {
        // GAME LOOP upadate et repaint tout les 1/60 seconds
        double drawInterval = (double) 1000000000 / GamePanel.FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                game.update(mouse);
                repaint();
                delta--;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        //Dessine le plateau
        board.draw(g2);

        //Dessine les pieces
        for (Piece p : game.simPieces) {
            p.draw(g2);
        }
        // Dessiner les informations de jeu
        int tour = 1;
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        g2.drawString("Tour actuel : " + currentPlayer, 840, 50);
        g2.drawString("Statut : " + status, 840, 100);
        g2.drawString("Historique :", 840, 140);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("Blanc :", 860, 170);
        g2.drawString("Noir : ", 980, 170);

        for (int i = 0; i < game.Historizes.size(); i++) {
            if (tour % 46 == 0) {
                g2.setColor(Color.BLACK);
                g2.clearRect(820, 180, 200, 800);
            }
            int displayIndex = i % 70; // Retourner à la première ligne après 30 tours
            if (i % 2 == 0) {
                g2.drawString(String.valueOf(tour), 840, 200 + displayIndex * 7);
                g2.drawString(game.Historizes.get(i), 860, 200 + displayIndex * 7);
            } else {
                g2.drawString(game.Historizes.get(i), 980, 200 + displayIndex * 7 - 7);
                tour++;
            }
        }

        //Timer
        if (game.gameMode == Game.PLAYERVSPLAYER) {
            game.timers.paintTimers(g2);
        }

        Score.paintScore(g2, game);
        Score.paintScoreSystem(g2);


        // Dessiner les mouvements
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        if (game.activeP != null) {
            if (game.canMove) {
                g2.setColor(Color.green.brighter());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.fillRect(game.activeP.col * Board.SQUARE_SIZE, game.activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                //Dessine la piece active
            }
            game.activeP.draw(g2);
        }

        if (game.gameMode == Game.PLAYERVSPLAYER && repaintTeam) {
            for (Piece piece : game.pieces) {
                piece.draw(g2);
            }
            repaintTeam = false;
        }


        //status message
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        g2.setColor(Color.white);


        if (game.promotion)//affichage promotion
        {
            status = "Promotion de " + currentPlayer;
            for (Piece piece : game.promotionPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);

            }
        } else {
            if (game.currentColor == Game.WHITE) {
                if (game.checkingP != null && game.checkingP.color == Game.BLACK) { // si echec l'afficher
                    status = currentPlayer + " est en echec";
                }
            } else {
                if (game.checkingP != null && game.checkingP.color == Game.WHITE) { // si echec l'afficher
                    status = currentPlayer + " est en echec";
                }
            }
        }
        if (game.checkmate || game.stalemate || game.ff || game.timeout) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            if (game.checkmate) {
                if (game.currentColor == Game.WHITE) {
                    status = "VICTOIRE BLANC";
                } else {
                    status = "VICTOIRE NOIR";
                }
                g2.drawString("Echec et mat", 300, 400);
            } else if (game.stalemate) {
                status = "Match nul";
                g2.drawString("Pat", 300, 400);
            } else if (game.ff || game.timeout) {
                if (game.currentColor == Game.WHITE) {
                    status = "VICTOIRE NOIR";
                } else {
                    status = "VICTOIRE BLANC";
                }
                if (game.ff) {
                    g2.drawString("Abandon", 300, 400);
                } else {
                    g2.drawString("Temps écoulé", 300, 400);
                }

            }
            titleScreenButton.setVisible(true);
        }
    }
}
