package Main;


import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import piece.Piece;//importation de la classe Piece
import piece.Rook;  // importation de la classe Rook
import piece.Knight; // importation de la classe Knight
import piece.Bishop; // importation de la classe Bishop
import piece.Queen;  // importation de la classe Queen
import piece.King;   // importation de la classe King
import piece.Pawn;   // importation de la classe Pawn

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




    //piece
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
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
    boolean validSquare;
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



        // Specify the file path for output
        String filePath = "save.json";

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonObject.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame() {
        gameState = playState;
        gameover = false;
        ff = false;
        timeout = false;

        String filePath = "save.json";

        try (FileReader reader = new FileReader(filePath)) {
            StringBuilder jsonContent = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonContent.append((char) i);
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());

            currentPlayer = jsonObject.getString("currentPlayer");
            currentColor = jsonObject.getInt("currentColor");
            VsAI = jsonObject.getBoolean("VsAI");
            VsPlayer = jsonObject.getBoolean("VsPlayer");
            whiteTimeRemaining = jsonObject.getInt("whiteTimeRemaining");
            blackTimeRemaining = jsonObject.getInt("blackTimeRemaining");
            stalemate = jsonObject.getBoolean("stalemate");
            promotion = jsonObject.getBoolean("promotion");


            JSONArray jsonPieces = jsonObject.getJSONArray("pieces");
            pieces.clear();
            for (int j = 0; j < jsonPieces.length(); j++) {
                JSONObject jsonPiece = jsonPieces.getJSONObject(j);
                Piece piece = Piece.fromJson(jsonPiece);
                pieces.add(piece);
            }

            JSONArray jsonSimPieces = jsonObject.getJSONArray("simPieces");
            simPieces.clear();
            for (int j = 0; j < jsonSimPieces.length(); j++) {
                JSONObject jsonSimPiece = jsonSimPieces.getJSONObject(j);
                Piece simPiece = Piece.fromJson(jsonSimPiece);
                simPieces.add(simPiece);
            }

            JSONArray jsonHistorize = jsonObject.getJSONArray("historize");
            Historizes.clear();
            for (int j = 0; j < jsonHistorize.length(); j++) {
                this.Historizes.add(jsonHistorize.getString(j));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        evaluateBoard(pieces);
        removeButtons();

        startTimer();

        if (currentColor == WHITE) {
            blackTimer.stop();
            whiteTimer.start();
        }
        else {
            blackTimer.start();
            whiteTimer.stop();
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


    public void updateActivePiecePosition(int mouseX, int mouseY) { //mise à jour de la position de la pièce active
        if (activeP != null) {
            activeP.x = mouseX - Board.HALF_SQUARE_SIZE;
            activeP.y = mouseY - Board.HALF_SQUARE_SIZE;
            repaint();
        }
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

        pieces.add(new Rook(0, 0, BLACK));
        pieces.add(new Knight(1, 0, BLACK));
        pieces.add(new Bishop(2, 0, BLACK));


        pieces.add(new Queen(3, 0, BLACK));
        pieces.add(new King(4, 0, BLACK));
        pieces.add(new Bishop(5, 0, BLACK));
        pieces.add(new Knight(6, 0, BLACK));
        pieces.add(new Rook(7, 0, BLACK));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(i, 1, BLACK));
        }


        pieces.add(new Rook(0, 7, WHITE));
        pieces.add(new Knight(1, 7, WHITE));
        pieces.add(new Bishop(2, 7, WHITE));
        pieces.add(new Queen(3, 7, WHITE));


        pieces.add(new King(4, 7, WHITE));
        pieces.add(new Bishop(5, 7, WHITE));
        pieces.add(new Knight(6, 7, WHITE));
        pieces.add(new Rook(7, 7, WHITE));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(i, 6, WHITE));
        }
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }


    }

    @Override
    public void run() {

        // GAME LOOP upadate et repaint tout les 1/60 seconds
        double drawInterval = 1000000000 / FPS;
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

        if (promotion) {
            promoting();// on promouvoit le pion
        } else if (gameover == false && stalemate == false && ff == false && timeout == false) {
            //souris pressé
            if (mouse.pressed) {
                if (activeP == null)
                // regarde si on peux prendre une piece
                {
                    for (Piece piece : simPieces)
                    // si la souris est dans la case d'une piece allie lui donner activeP
                    {
                        if (piece.color == currentColor &&
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                piece.row == mouse.y / Board.SQUARE_SIZE) {
                            activeP = piece;

                        }
                    }
                } else {
                    //si le joueur tien la piece
                    simulate();
                }

            }

            //souris relaché

            if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {

                        //MOUVEMENT CONFIRME


                        //enregistre le mouvement
                        recordMove(activeP, activeP.preCol, activeP.preRow, activeP.col, activeP.row);
                        // enleve la piece de la liste
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }

                        //Verifie si le joueur est en echec

                        if (isKingInCheck() && isCheckmate()) {
                            gameover = true; // game over

                        } else if (isStalemate() && isKingInCheck() == false) {
                            stalemate = true; // pat match nul
                        } else // le jeu continue
                        {
                            if (canPromote()) // voir si on peut promouvoir sinon on passe le tour
                            {
                                promotion = true;
                            } else {
                                changePlayer();
                                if (currentColor == WHITE) {
                                    whiteTimer.start();
                                    blackTimer.stop();
                                } else {
                                    blackTimer.start();
                                    whiteTimer.stop();
                                }
                                if (VsAI && currentColor == BLACK) {
                                    //code de l'ia
                                }

                            }

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
        validSquare = false;
        //si la piece est tenu, uptade la position de la piece
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
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;
            // si il y a une piece dessus
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();


            if (isIllegal(activeP) == false && opponentCanCaptureKing() == false) // si le roi n'est pas en echec sinon on fais "isIllegal"
            {
                validSquare = true;
            }

        }
    }

    private boolean isIllegal(Piece king) {
        if (king.type == Type.KING) // si la piece est un roi
        {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) // si une piece peut prendre le roi
                {
                    return true; //donc is legal est true
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }

        return false;
    }


    private boolean isKingInCheck() // On regarde si le roi est en echec
    {

        Piece king = getKing(true);

        if (activeP.canMove(king.col, king.row)) // si la piece peut prendre le roi
        {
            checkingP = activeP;
            return true; // le roi est en echec
        } else {
            checkingP = null; // le roi n'est pas en echec
            status = "En cours";
        }
        return false;
    }


    private Piece getKing(boolean opponent) // on recupere le roi
    {
        Piece king = null;
        for (Piece pieces : simPieces) {
            if (opponent) //opponent = true
            {
                if (pieces.type == Type.KING && pieces.color != currentColor) //if qui montre que le roi est en danger
                {
                    king = pieces;
                }
            } else {
                if (pieces.type == Type.KING && pieces.color == currentColor) //if qui montre que le roi n'est pas en danger
                {
                    king = pieces;
                }
            }

        }
        return king; // on retourne si le roi est en echec ou pas
    }

    private boolean isCheckmate() //verification echec et mat
    {
        Piece king = getKing(true);
        if (kingCanMove(king)) // si le roi peut bouger
        {
            return false; // le roi n'est pas en echec et mat
        } else {
            //verifie si on peut blocker le roi avec notre piece
            // voir le chemin qui met le roi en echec
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            //par ou faut il bouger pour bloquer le roi
            if (colDiff == 0) {
                //attaque verticalement
                if (checkingP.row < king.row) // de en haut
                {
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) // de en bas
                {
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }

                }

            } else if (rowDiff == 0) {
                //attaque horinzotalement
                if (checkingP.col < king.col) // de gauche
                {
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) // de droite
                {
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }

                }


            } else if (colDiff == rowDiff) {
                //attaque diagonalement
                if (checkingP.col < king.col && checkingP.row < king.row) // en haut à gauche
                {
                    for (int col = checkingP.col, row = checkingP.row; col < king.col && row < king.row; col++, row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col < king.col && checkingP.row > king.row) // en bas à gauche
                {
                    for (int col = checkingP.col, row = checkingP.row; col < king.col && row > king.row; col++, row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col && checkingP.row < king.row) // en haut à droite
                {
                    for (int col = checkingP.col, row = checkingP.row; col > king.col && row < king.row; col--, row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col && checkingP.row > king.row) // en bas à droite
                {
                    for (int col = checkingP.col, row = checkingP.row; col > king.col && row > king.row; col--, row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                return false;
                            }
                        }
                    }
                }

            }
        }
/*
        // vérifier si avec une pièce ou peut manger la piece qui fait l'échec et mate
        ArrayList<Piece> pieceThatAreDoingCheck = new ArrayList<Piece>();
        ArrayList<Piece> simPieces2 = new ArrayList<Piece>(simPieces);
        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                pieceThatAreDoingCheck.add(piece);
            }
        }

        if (pieceThatAreDoingCheck.size() == 1) {

            for (Piece piece : simPieces) {
                if (piece.color == king.color && piece.canMove(pieceThatAreDoingCheck.getFirst().col, pieceThatAreDoingCheck.getFirst().row)) {
                    // verifier si quand la piece mange le piece qui mettait le roi en échec celle-ci n'offre pas une autre possibilité d'échec
                    simPieces2.remove(pieceThatAreDoingCheck.getFirst());
                    piece.col = pieceThatAreDoingCheck.getFirst().col;
                    piece.row = pieceThatAreDoingCheck.getFirst().row;
                    boolean check = false;
                    for (Piece piece2 : simPieces2) {
                        if (piece2.color != king.color && piece2.canMove(king.col, king.row)) {
                            check = true;
                        }
                    }
                    piece.resetPosition();
                    if (check == false) {
                        return false;
                    }
                }
            }

        }*/


        return true;
    }

    private boolean kingCanMove(Piece king) //verification si le roi peut bouger
    {
        // on regarde si le roi peut bouger dans une case
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }

        return false; // le roi ne peut plus bouger
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus) //verification si on peut bouger
    {
        boolean isValidMove = false;
        // on regarde si le roi peut bouger dans une case
        king.col += colPlus;
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) // si le roi peut bouger
        {
            if (king.hittingP != null) // si le roi peut prendre une piece
            {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (isIllegal(king) == false) // si c'est pas un move illegal
            {
                isValidMove = true;
            }
        }
        // on remet le roi à sa place
        king.resetPosition();
        copyPieces(pieces, simPieces);


        return isValidMove;
    }

    private boolean isStalemate() //verification si c'est pat
    {
        int count = 0;
        // compter le nombre de piece qui ne peuvent pas bouger
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }
        // si il reste que le roi
        if (count == 1) {
            if (kingCanMove(getKing(true)) == false) // le roi peut pas bouger
            {
                return true;
            }
        }
        return false;
    }


    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;

            }
            castlingP.x = castlingP.getX(castlingP.col);

        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            currentPlayer = "Noir";
            // on reste la variable twostep a false
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = WHITE;
            currentPlayer = "Blanc";
            // on reste la variable twostep a false

            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }


    private void updateStatus(String status) {
        this.status = status;
    }

    private void recordMove(Piece piece, int oldX, int oldY, int newX, int newY) {
        char oldXChar = ' ';
        char newXChar = ' ';
        char oldYChar = ' ';
        char newYChar = ' ';
        switch (oldX) {
            case 0:
                oldXChar = 'a';
                break;
            case 1:
                oldXChar = 'b';
                break;
            case 2:
                oldXChar = 'c';
                break;
            case 3:
                oldXChar = 'd';
                break;
            case 4:
                oldXChar = 'e';
                break;
            case 5:
                oldXChar = 'f';
                break;
            case 6:
                oldXChar = 'g';
                break;
            case 7:
                oldXChar = 'h';
                break;
        }
        switch (newX) {
            case 0:
                newXChar = 'a';
                break;
            case 1:
                newXChar = 'b';
                break;
            case 2:
                newXChar = 'c';
                break;
            case 3:
                newXChar = 'd';
                break;
            case 4:
                newXChar = 'e';
                break;
            case 5:
                newXChar = 'f';
                break;
            case 6:
                newXChar = 'g';
                break;
            case 7:
                newXChar = 'h';
                break;
        }
        oldYChar = switch (oldY) {
            case 0 -> '8';
            case 1 -> '7';
            case 2 -> '6';
            case 3 -> '5';
            case 4 -> '4';
            case 5 -> '3';
            case 6 -> '2';
            case 7 -> '1';
            default -> oldYChar;
        };
        newYChar = switch (newY) {
            case 0 -> '8';
            case 1 -> '7';
            case 2 -> '6';
            case 3 -> '5';
            case 4 -> '4';
            case 5 -> '3';
            case 6 -> '2';
            case 7 -> '1';
            default -> oldYChar;
        };
        this.Historizes.add(piece.getName() + " de (" + oldXChar + ", " + oldYChar + ") à (" + newXChar + ", " + newYChar + ")");
    }


    private boolean canPromote() { //la promotion du pion
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || activeP.color == BLACK && activeP.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Rook(2, 4, currentColor));
                promotionPieces.add(new Bishop(3, 4, currentColor));
                promotionPieces.add(new Knight(4, 4, currentColor));
                promotionPieces.add(new Queen(5, 4, currentColor));
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
                            simPieces.add(new Rook(activeP.col, activeP.row, currentColor));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(activeP.col, activeP.row, currentColor));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(activeP.col, activeP.row, currentColor));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(activeP.col, activeP.row, currentColor));
                            break;
                        default:
                            break;

                    }
                    simPieces.remove(activeP.getIndex()); // on enleve le pion
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
            int tour=1;
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            g2.drawString("Tour actuel : " + currentPlayer, 840, 50);
            g2.drawString("Statut : " + status, 840, 100);
            g2.drawString("Historique :", 840, 140);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString("Blanc :", 860, 170);
            g2.drawString("Noir : ", 980, 170);

            for (int i = 0; i < Historizes.size(); i++) {
                if(tour % 46 == 0)
                {
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
                    if (isIllegal(activeP) || opponentCanCaptureKing()) // si on fais un move illegal
                    {
                        g2.setColor(Color.red.brighter());
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                        g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    } else {
                        g2.setColor(Color.green.brighter());
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                        g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                        //Dessine la piece active
                    }


                    activeP.draw(g2);
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
    }

}




