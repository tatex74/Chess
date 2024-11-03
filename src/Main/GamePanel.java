package Main;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import Piece.Piece;//importation de la classe Piece
import Piece.Rook;  // importation de la classe Rook
import Piece.Knight; // importation de la classe Knight
import Piece.Bishop; // importation de la classe Bishop
import Piece.Queen;  // importation de la classe Queen
import Piece.King;   // importation de la classe King
import Piece.Pawn;   // importation de la classe Pawn
import Piece.Move;
import Computer.Minimax;

import Main.Type;

public class GamePanel extends JPanel implements Runnable { //La classe GamePanel hérite de JPanel (composant graphique Swing) et implémente l'interface Runnable, ce qui permet d'exécuter du code dans un thread séparé. La méthode run() sera appelée quand le thread démarre, mais ici elle est vide, en attente de l'ajout du code du jeu.
    public static final int WIDTH = 1500;
    public static final int HEIGHT = 850;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();
    Timer whiteTimer, blackTimer;
    private String currentPlayer = "Blanc";
    private String status = "En cours";
    public final int titleState = 0;
    public final int playState = 1;
    public final int loadState = 2;
    public int gameState = titleState;
    private int whiteTimeRemaining = 100;
    private int blackTimeRemaining = 100;

    private boolean repaintTeam = false;


    //piece
    private static ArrayList<Piece> pieces = new ArrayList<>();
    private static ArrayList<Piece> simPieces = new ArrayList<>();
    public ArrayList<String> Historizes = new ArrayList<>();
    ArrayList<Piece> promotionPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;


    //Couleur
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //bollean
    boolean canMove;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    boolean VsPlayer;
    boolean VsAI;
    boolean ff;
    boolean timeout;

    //boutons
    private JButton playerVsPlayerButton;
    private JButton playerVsAIButton;
    private JButton loadGameButton;
    private JButton abandonButton;
    private JButton titleScreenButton;
    private JButton resetButton;
    private JButton saveButton;


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null); // Utiliser un layout null pour positionner les boutons manuellement

        // Initialiser les boutons
        playerVsPlayerButton = new JButton("Joueur contre Joueur");
        playerVsAIButton = new JButton("Joueur contre Ordinateur");
        loadGameButton = new JButton("Charger une Partie");
        abandonButton = new JButton("Abandonner");
        titleScreenButton = new JButton("Retour à l'écran titre");
        resetButton = new JButton("Réinitialiser la partie");
        saveButton = new JButton("Sauvegarder la partie");

        // Positionner les boutons
        playerVsPlayerButton.setBounds(600, 300, 300, 50);
        playerVsAIButton.setBounds(600, 400, 300, 50);
        loadGameButton.setBounds(600, 500, 300, 50);
        abandonButton.setBounds(1200, 600, 200, 40);
        titleScreenButton.setBounds(1200, 650, 200, 40);
        resetButton.setBounds(1200, 700, 200, 40);
        saveButton.setBounds(1200, 750, 200, 40);


        // Ajouter les boutons au panel
        add(playerVsPlayerButton);
        add(playerVsAIButton);
        add(loadGameButton);
        add(abandonButton);
        add(titleScreenButton);
        add(resetButton);
        add(saveButton);

        // Ajouter des listeners pour les boutons
        playerVsPlayerButton.addActionListener(e -> startPlayerVsPlayer());
        playerVsAIButton.addActionListener(e -> startPlayerVsAI());
        loadGameButton.addActionListener(e -> loadGame());
        abandonButton.addActionListener(e -> abandonGame());
        titleScreenButton.addActionListener(e -> returnToTitleScreen());
        resetButton.addActionListener(e -> resetGame());
        saveButton.addActionListener(e -> saveGame());

        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    private void startPlayerVsPlayer() {
        gameState = playState;
        VsPlayer = true;
        VsAI = false;
        blackTimeRemaining = 10 * 60;
        whiteTimeRemaining = 10 * 60;
        gameover = false;
        stalemate = false;
        promotion = false;
        ff = false;
        timeout = false;
        setPieces();
        copyPieces(pieces, simPieces);
        evaluateBoard(pieces);
        removeButtons();
        startTimer();
    }

    private void startPlayerVsAI() {
        gameState = playState;
        VsPlayer = false;
        VsAI = true;
        blackTimeRemaining = 10 * 60;
        whiteTimeRemaining = 10 * 60;
        gameover = false;
        stalemate = false;
        promotion = false;
        ff = false;
        timeout = false;
        setPieces();
        copyPieces(pieces, simPieces);
        evaluateBoard(pieces);
        removeButtons();
        startTimer();
    }

    private void abandonGame() {
        ff = true;
        status = "Abandon";
        repaint();
    }

    private void returnToTitleScreen() {
        gameState = titleState;
        removeButtons();
        add(playerVsPlayerButton);
        add(playerVsAIButton);
        add(loadGameButton);
        revalidate();
        repaint();
    }

    private void resetGame() {
        pieces.clear();
        simPieces.clear();
        setPieces();
        copyPieces(pieces, simPieces);
        currentColor = WHITE;
        currentPlayer = "Blanc";
        status = "En cours";
        gameover = false;
        stalemate = false;
        promotion = false;
        ff = false;
        timeout = false;
        activeP = null;
        checkingP = null;
        castlingP = null;
        Historizes.clear();
        blackTimeRemaining = 10 * 60;
        whiteTimeRemaining = 10 * 60;
        blackTimer.stop();
        whiteTimer.start();
        repaint();
        evaluateBoard(pieces);
    }
    private void saveGame() {
        // Création du fichier JSON avec les données du jeu
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentPlayer", currentPlayer);
        jsonObject.put("currentColor", currentColor);
        jsonObject.put("VsAI", VsAI);
        jsonObject.put("VsPlayer", VsPlayer);
        jsonObject.put("whiteTimeRemaining", whiteTimeRemaining);
        jsonObject.put("blackTimeRemaining", blackTimeRemaining);
        jsonObject.put("stalemate", stalemate);
        jsonObject.put("promotion", promotion);

        JSONArray jsonPieces = new JSONArray();
        for (Piece piece : pieces) {
            jsonPieces.put(piece.toJson());
        }
        jsonObject.put("pieces", jsonPieces);

        JSONArray jsonSimPieces = new JSONArray();
        for (Piece piece : simPieces) {
            jsonSimPieces.put(piece.toJson());
        }
        jsonObject.put("simPieces", jsonSimPieces);

        JSONArray jsonHistorize = new JSONArray();
        for (String move : Historizes) {
            jsonHistorize.put(move);
        }
        jsonObject.put("historize", jsonHistorize);

        // Ouvrir un explorateur de fichiers pour permettre à l'utilisateur de choisir l'emplacement et le nom du fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la sauvegarde");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers JSON", "json"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ajouter l'extension .json si elle est manquante
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".json")) {
                filePath += ".json";
            }

            // Écrire les données JSON dans le fichier choisi par l'utilisateur
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonObject.toString(4));
                System.out.println("Sauvegarde réussie : " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadGame() {

        // Ouvrir un explorateur de fichiers pour sélectionner le fichier de sauvegarde
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger une sauvegarde");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers JSON", "json"));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String filePath = fileToLoad.getAbsolutePath();

            try (FileReader reader = new FileReader(filePath)) {
                StringBuilder jsonContent = new StringBuilder();
                int i;
                while ((i = reader.read()) != -1) {
                    jsonContent.append((char) i);
                }

                JSONObject jsonObject = new JSONObject(jsonContent.toString());

                // Vérification des clés essentielles pour s'assurer que c'est une sauvegarde valide de jeu d'échecs
                if (isValidChessSave(jsonObject)) {
                    // Charger les données du fichier JSON
                    currentPlayer = jsonObject.getString("currentPlayer");
                    currentColor = jsonObject.getInt("currentColor");
                    VsAI = jsonObject.getBoolean("VsAI");
                    VsPlayer = jsonObject.getBoolean("VsPlayer");
                    whiteTimeRemaining = jsonObject.getInt("whiteTimeRemaining");
                    blackTimeRemaining = jsonObject.getInt("blackTimeRemaining");
                    stalemate = jsonObject.getBoolean("stalemate");
                    promotion = jsonObject.getBoolean("promotion");

                    // Charger les pièces
                    JSONArray jsonPieces = jsonObject.getJSONArray("pieces");
                    pieces.clear();
                    for (int j = 0; j < jsonPieces.length(); j++) {
                        JSONObject jsonPiece = jsonPieces.getJSONObject(j);
                        Piece piece = Piece.fromJson(jsonPiece);
                        pieces.add(piece);

                    }

                    // Charger les pièces simulées
                    JSONArray jsonSimPieces = jsonObject.getJSONArray("simPieces");
                    simPieces.clear();
                    for (int j = 0; j < jsonSimPieces.length(); j++) {
                        JSONObject jsonSimPiece = jsonSimPieces.getJSONObject(j);
                        Piece simPiece = Piece.fromJson(jsonSimPiece);
                        simPieces.add(simPiece);
                    }

                    // Charger l'historique des mouvements
                    JSONArray jsonHistorize = jsonObject.getJSONArray("historize");
                    Historizes.clear();
                    for (int j = 0; j < jsonHistorize.length(); j++) {
                        Historizes.add(jsonHistorize.getString(j));
                    }

                    // Initialiser l'état du jeu
                    gameState = playState;
                    gameover = false;
                    ff = false;
                    timeout = false;
                    stalemate = false;
                    evaluateBoard(pieces);
                    removeButtons();
                    startTimer();

                    if (currentColor == WHITE) {
                        blackTimer.stop();
                        whiteTimer.start();
                    } else {
                        blackTimer.start();
                        whiteTimer.stop();
                    }
                } else {
                    System.out.println("Le fichier sélectionné n'est pas une sauvegarde de jeu d'échecs valide.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidChessSave(JSONObject jsonObject) {
        try {
            // Vérification des clés principales et de leur type attendu
            return jsonObject.has("currentPlayer") && jsonObject.get("currentPlayer") instanceof String
                    && jsonObject.has("currentColor") && jsonObject.get("currentColor") instanceof Integer
                    && jsonObject.has("VsAI") && jsonObject.get("VsAI") instanceof Boolean
                    && jsonObject.has("VsPlayer") && jsonObject.get("VsPlayer") instanceof Boolean
                    && jsonObject.has("whiteTimeRemaining") && jsonObject.get("whiteTimeRemaining") instanceof Integer
                    && jsonObject.has("blackTimeRemaining") && jsonObject.get("blackTimeRemaining") instanceof Integer
                    && jsonObject.has("stalemate") && jsonObject.get("stalemate") instanceof Boolean
                    && jsonObject.has("promotion") && jsonObject.get("promotion") instanceof Boolean
                    && jsonObject.has("pieces") && jsonObject.get("pieces") instanceof JSONArray
                    && jsonObject.has("simPieces") && jsonObject.get("simPieces") instanceof JSONArray
                    && jsonObject.has("historize") && jsonObject.get("historize") instanceof JSONArray;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    private void removeButtons() {
        remove(playerVsPlayerButton);
        remove(playerVsAIButton);
        remove(loadGameButton);
        revalidate();
        repaint();
    }

    private void startTimer() {
        // Initialiser les timers
        whiteTimer = new Timer(1000, e -> {
                whiteTimeRemaining--;
                if (whiteTimeRemaining <= 0) {
                    whiteTimer.stop();
                    timeout = true;
                    status = "Temps écoulé";
                    repaint();
            }
        });
        whiteTimer.start();

        blackTimer = new Timer(1000, e -> {
                blackTimeRemaining--;
                if (blackTimeRemaining <= 0) {
                    blackTimer.stop();
                    timeout = true;
                    status = "Temps écoulé";
                    repaint();
            }
        });
        blackTimer.stop();

    }
   //parametrage de l'ia


    // Le jeu est lancée
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();

    }

    //gestion des points
    public int[] evaluateBoard(ArrayList<Piece> pieces) {
        int whiteScore = 1039; //Score
        int blackScore = 1039;
        for (Piece piece : pieces) {
            if (piece.color == GamePanel.WHITE) {
                blackScore += getPieceValue(piece);
            } else {
                whiteScore += getPieceValue(piece);
            }
        }
        return new int[]{whiteScore, blackScore};
    }

    private int getPieceValue(Piece piece) {
        switch (piece.type) {
            case PAWN: return -1;
            case KNIGHT: return -3;
            case BISHOP: return -3;
            case ROOK: return -5;
            case QUEEN: return -9;
            case KING: return -1000;
            default: return 0;
        }
    }


    public void setPieces() //place les pieces sur le plateau
    {
        pieces.add(new Rook(0, 0, BLACK, true));
        pieces.add(new Knight(1, 0, BLACK, true));
        pieces.add(new Bishop(2, 0, BLACK, true));
        pieces.add(new Queen(3, 0, BLACK, true));
        pieces.add(new King(4, 0, BLACK, true));
        pieces.add(new Bishop(5, 0, BLACK, true));
        pieces.add(new Knight(6, 0, BLACK, true));
        pieces.add(new Rook(7, 0, BLACK, true));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(i, 1, BLACK, true));
        }

        pieces.add(new Rook(0, 7, WHITE, true));
        pieces.add(new Knight(1, 7, WHITE, true));
        pieces.add(new Bishop(2, 7, WHITE, true));
        pieces.add(new Queen(3, 7, WHITE, true));
        pieces.add(new King(4, 7, WHITE, true));
        pieces.add(new Bishop(5, 7, WHITE, true));
        pieces.add(new Knight(6, 7, WHITE, true));
        pieces.add(new Rook(7, 7, WHITE, true));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(i, 6, WHITE, true));
        }
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    @Override
    public void run() {
        // GAME LOOP upadate et repaint tout les 1/60 seconds
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (!Historizes.isEmpty()) {
            if (!gameover && Move.isCheckmate(pieces, currentColor)) {
                gameover = true; // game over
            } else if (!stalemate && Move.isStalemate(pieces, currentColor)) {
                stalemate = true; // pat match nul
            }
        }

        if (VsAI) {
            if (currentColor == WHITE) {
                mouseEventOnPiece();
            } else {
                Move AIMove = Minimax.findBestMove(pieces, BLACK);
                Minimax.makeMove(pieces, AIMove);
                repaintTeam = true;
                changePlayer();
            }
        } else {
            mouseEventOnPiece();
        }


    }

    public void mouseEventOnPiece() {
        if (promotion) {
            promoting();// on promouvoit le pion
        } else if (!gameover && !stalemate && !ff && !timeout) {
            if (mouse.pressed) {
                if (activeP == null)
                {
                    activeP = Piece.getPieceByCoord(simPieces, mouse.x/Board.SQUARE_SIZE, mouse.y/Board.SQUARE_SIZE);
                    if (activeP != null && activeP.color != currentColor) {
                        activeP = null;
                    }

                } else {
                    //si le joueur tien la piece
                    simulate();
                }

            }

            //souris relaché

            if (!mouse.pressed) {
                if (activeP != null) {
                    if (canMove) {
                        Move.recordMove(activeP, Historizes, activeP.preCol, activeP.preRow, activeP.col, activeP.row);
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }

                        if (canPromote()) {
                            promotion = true;
                        } else {
                            changePlayer();
                        }


                    } else {
                        //mouvement annulé alors on reset
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }
    }


    private void simulate() {
        canMove = false;
        //si la piece est tenue, update la position de la piece
        // empeche la piece de revenir aprés qu'on la prise
        copyPieces(pieces, simPieces);

        //reset les positions des pieces pour le roque
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }


        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // regarde si il y a une piece dessus
        if (activeP.canMove(simPieces, activeP.col, activeP.row)) {
            canMove = true;

            if (activeP.type == Type.KING && Math.abs(activeP.col-activeP.preCol) == 2) {
                if (activeP.col == activeP.preCol + 2) {
                    castlingP = activeP.getHittingP(simPieces, 7, activeP.row);
                    castlingP.col = 5;
                }
                else  {
                    castlingP = activeP.getHittingP(simPieces, 0, activeP.row);
                    castlingP.col = 3;
                }
            }
            // si il y a une piece dessus
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex(simPieces));
            }

        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            currentPlayer = "Noir";
            // on reset la variable twostep a false
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }

            whiteTimer.start();
            blackTimer.stop();
        } else {
            currentColor = WHITE;
            currentPlayer = "Blanc";
            // on reste la variable twostep a false

            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }

            blackTimer.start();
            whiteTimer.stop();
        }

        activeP = null;
    }

    private boolean canPromote() { //la promotion du pion
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || activeP.color == BLACK && activeP.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Rook(2, 4, currentColor, true));
                promotionPieces.add(new Bishop(3, 4, currentColor, true));
                promotionPieces.add(new Knight(4, 4, currentColor, true));
                promotionPieces.add(new Queen(5, 4, currentColor, true));
                return true;
            }
        }
        return false;
    }

    private void promoting() { //la promotion
        whiteTimer.stop();
        blackTimer.stop();
        if (mouse.pressed) {
            for (Piece piece : promotionPieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) // si la souris est sur une piece de promotion
                {
                    switch (piece.type) // on change le type de la piece
                    {
                        case ROOK:
                            simPieces.add(new Rook(activeP.col, activeP.row, currentColor, true));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(activeP.col, activeP.row, currentColor, true));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(activeP.col, activeP.row, currentColor, true));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(activeP.col, activeP.row, currentColor, true));
                            break;
                        default:
                            break;

                    }
                    simPieces.remove(activeP.getIndex(simPieces)); // on enleve le pion
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                    if (currentColor == WHITE) {
                        whiteTimer.start();
                        blackTimer.stop();
                    } else {
                        blackTimer.start();
                        whiteTimer.stop();
                    }
                    //on applique la promotion et donner le tour au joueur adverse
                }

            }
        }
    }


    public void paintComponent(Graphics g) {
        //paintComponent est une méthode dans JComponent que Jpanel intere et utiliser pour dessiner des objets sur le panel
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Book Antiqua", Font.PLAIN, 50));
            g2.setColor(Color.white);
            g2.drawString("Echec", 600, 200);
            ff = false;
            gameover = false;
            stalemate = false;
            status = "En cours";
            resetButton.setVisible(false);
            abandonButton.setVisible(false);
            titleScreenButton.setVisible(false);
            saveButton.setVisible(false);

        } else {
            //rendre les boutons visibles
            resetButton.setVisible(true);
            abandonButton.setVisible(true);
            saveButton.setVisible(true);


            //Dessine le plateau
            board.draw(g2);
            // Dessiner les coordonnées des cases
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(Color.WHITE);

            // Dessiner les lettres (a-h) en haut et en bas du plateau
            for (int col = 0; col < 8; col++) {
                char letter = (char) ('a' + col);
                g2.drawString(String.valueOf(letter), col * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE, 815);

            }

            // Dessiner les numéros (1-8) à gauche et à droite du plateau
            for (int row = 0; row < 8; row++) {
                int number = 8 - row;
                g2.drawString(String.valueOf(number), 810, row * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE);
            }

            //Dessine les pieces
            for (Piece p : simPieces) {
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

            for (int i = 0; i < Historizes.size(); i++) {
                if (tour % 46 == 0) {
                    g2.setColor(Color.BLACK);
                    g2.clearRect(820, 180, 200, 800);
                }
                int displayIndex = i % 70; // Retourner à la première ligne après 30 tours
                if (i % 2 == 0) {
                    g2.drawString(String.valueOf(tour), 840, 200 + displayIndex * 7);
                    g2.drawString(Historizes.get(i), 860, 200 + displayIndex * 7);
                } else {
                    g2.drawString(Historizes.get(i), 980, 200 + displayIndex * 7 - 7);
                    tour++;
                }
            }
            //Timer
            int Wminutes = whiteTimeRemaining / 60;
            int Wseconds = whiteTimeRemaining % 60;
            int Bminutes = blackTimeRemaining / 60;
            int Bseconds = blackTimeRemaining % 60;
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            g2.drawString(String.format("Temps restant Blanc : %02d:%02d", Wminutes, Wseconds), 1200, 60);
            g2.drawString(String.format("Temps restant Noir : %02d:%02d", Bminutes, Bseconds), 1200, 120);
            //score
            int[] scores = evaluateBoard(pieces);
            g2.drawString("Score Blanc : " + scores[0], 1200, 180);
            g2.drawString("Score Noir : " + scores[1], 1200, 240);

            //afficher le systeme de point
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("Système de points :", 1200, 300);
            g2.drawString("Pion : 1 point", 1200, 320);
            g2.drawString("Cavalier : 3 points", 1200, 340);
            g2.drawString("Fou : 3 points", 1200, 360);
            g2.drawString("Tour : 5 points", 1200, 380);
            g2.drawString("Dame : 9 points", 1200, 400);
            g2.drawString("Roi : Inestimable", 1200, 420);


            // Dessiner les mouvements
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            int y = 150;

            if (activeP != null) {
                if (canMove) {
                    g2.setColor(Color.green.brighter());
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    //Dessine la piece active
                }
                activeP.draw(g2);
            }

            if (VsAI && repaintTeam) {
                for (Piece piece : pieces) {
                    piece.draw(g2);
                }
                repaintTeam = false;
            }
        }

        //status message
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        g2.setColor(Color.white);


        if (promotion)//affichage promotion
        {
            status = "Promotion de " + currentPlayer;
            for (Piece piece : promotionPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);

            }
        } else {
            if (currentColor == WHITE) {
                if (checkingP != null && checkingP.color == BLACK) { // si echec l'afficher
                    status = currentPlayer + " est en echec";
                }
            } else {
                if (checkingP != null && checkingP.color == WHITE) { // si echec l'afficher
                    status = currentPlayer + " est en echec";
                }
            }
        }
        if (gameover || stalemate || ff || timeout) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            if (gameover) {
                if (currentColor == WHITE) {
                    status = "VICTOIRE BLANC";
                } else {
                    status = "VICTOIRE NOIR";
                }
                g2.drawString("Echec et mat", 300, 400);
            } else if (stalemate) {
                status = "Match nul";
                g2.drawString("Pat", 300, 400);
            } else if (ff || timeout) {
                if (currentColor == WHITE) {
                    status = "VICTOIRE NOIR";
                } else {
                    status = "VICTOIRE BLANC";
                }
                if (ff) {
                    g2.drawString("Abandon", 300, 400);
                } else {
                    g2.drawString("Temps écoulé", 300, 400);
                }

            }
            titleScreenButton.setVisible(true);
        }
    }

    public void repaint(Graphics g, int color) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (Piece piece : pieces) {
            if (color == piece.color) {
                piece.draw(g2);
            }
        }
    }
}
