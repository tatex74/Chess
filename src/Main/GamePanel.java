package Main;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import piece.Piece;//importation de la classe Piece
import piece.Rook;  // importation de la classe Rook
import piece.Knight; // importation de la classe Knight
import piece.Bishop; // importation de la classe Bishop
import piece.Queen;  // importation de la classe Queen
import piece.King;   // importation de la classe King
import piece.Pawn;   // importation de la classe Pawn

public class GamePanel extends JPanel implements Runnable { //La classe GamePanel hérite de JPanel (composant graphique Swing) et implémente l'interface Runnable, ce qui permet d'exécuter du code dans un thread séparé. La méthode run() sera appelée quand le thread démarre, mais ici elle est vide, en attente de l'ajout du code du jeu.
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 8000;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //etat
    public int gameState = 0;
    public final int titleState = 0;

    //piece
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promotionPieces = new ArrayList<>();
    Piece activeP,checkingP;
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
    boolean restart;
    boolean VsPlayer;
    boolean VsAI;


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); //la dimension
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }


    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
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

        if (promotion)
        {
            promoting();// on promouvoit le pion
        }
        else if (gameover == false && stalemate == false)
        {
            //souris pressé
            if (mouse.pressed)
            {
                if (activeP == null)
                // regarde si on peux prendre une piece
                {
                    for (Piece piece : simPieces)
                    // si la souris est dans la case d'une piece allie lui donner activeP
                    {
                        if (piece.color == currentColor &&
                                piece.col == mouse.x/Board.SQUARE_SIZE &&
                                piece.row == mouse.y/Board.SQUARE_SIZE)
                        {
                            activeP = piece;
                        }
                    }
                }
                else
                {
                    //si le joueur tien la piece
                    simulate();
                }

            }

            //souris relaché

            if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {

                        //MOUVEMENT CONFIRME



                        // enleve la piece de la liste
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null)
                        {
                            castlingP.updatePosition();
                        }

                        //Verifie si le joueur est en echec

                        if(isKingInCheck() && isCheckmate())
                        {
                            gameover = true; // game over

                        } else if (isStalemate() && isKingInCheck() ==false) {
                            stalemate = true; // pat match nul
                        } else // le jeu continue
                        {
                            if (canPromote()) // voir si on peut promouvoir sinon on passe le tour
                            {
                                promotion = true;
                            }
                            else {
                                changePlayer();
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
        if(castlingP != null)
        {
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

    private boolean isIllegal(Piece king)
    {
        if(king.type == Type.KING) // si la piece est un roi
        {
            for (Piece piece: simPieces)
            {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) // si une piece peut prendre le roi
                {
                    return true; //donc is legal est true
                }
            }
        }
    return false;
    }

    private boolean opponentCanCaptureKing()
    {
        Piece king = getKing(false);

        for (Piece piece: simPieces)
        {
            if (piece.color != king.color && piece.canMove(king.col, king.row))
            {
                return true;
            }
        }

        return false;
    }



    private boolean isKingInCheck() // On regarde si le roi est en echec
    {

        Piece king = getKing(true);

        if(activeP.canMove(king.col, king.row)) // si la piece peut prendre le roi
        {
            checkingP = activeP;
            return true; // le roi est en echec
        }
        else {
            checkingP = null; // le roi n'est pas en echec
        }
        return false;
    }




    private Piece getKing(boolean opponent) // on recupere le roi
    {
        Piece king = null;
        for (Piece pieces: simPieces) {
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
        if(kingCanMove(king)) // si le roi peut bouger
        {
            return false; // le roi n'est pas en echec et mat
        }
        else{
            //verifie si on peut blocker le roi avec notre piece
            // voir le chemin qui met le roi en echec
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);
            
            //par ou faut il bouger pour bloquer le roi
            if (colDiff==0)
            {
                //attaque verticalement
                if (checkingP.row < king.row) // de en haut
                {
                    for (int row = checkingP.row; row < king.row; row++)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col,row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) // de en bas
                {
                    for (int row = checkingP.row; row > king.row; row--)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col,row))
                            {
                                return false;
                            }
                        }
                    }

                }
                
            } else if (rowDiff == 0) {
                //attaque horinzotalement
                if (checkingP.col < king.col) // de gauche
                {
                    for (int col = checkingP.col; col < king.col; col++)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(col,checkingP.row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) // de droite
                {
                    for (int col = checkingP.col; col > king.col; col--)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(col,checkingP.row))
                            {
                                return false;
                            }
                        }
                    }

                }


            } else if (colDiff == rowDiff) {
                //attaque diagonalement
                if (checkingP.col < king.col && checkingP.row < king.row) // en haut à gauche
                {
                    for (int col = checkingP.col, row = checkingP.row; col < king.col && row < king.row; col++, row++)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col < king.col && checkingP.row > king.row) // en bas à gauche
                {
                    for (int col = checkingP.col, row = checkingP.row; col < king.col && row > king.row; col++, row--)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col && checkingP.row < king.row) // en haut à droite
                {
                    for (int col = checkingP.col, row = checkingP.row; col > king.col && row < king.row; col--, row++)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col && checkingP.row > king.row) // en bas à droite
                {
                    for (int col = checkingP.col, row = checkingP.row; col > king.col && row > king.row; col--, row--)
                    {
                        for (Piece piece: simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                            {
                                return false;
                            }
                        }
                    }
                }

            }

        }

        return true;
    }
    private boolean kingCanMove(Piece king) //verification si le roi peut bouger
    {
        // on regarde si le roi peut bouger dans une case
        if (isValidMove(king,-1,-1)){return true;}
        if (isValidMove(king,-1,0)){return true;}
        if (isValidMove(king,-1,1)){return true;}
        if (isValidMove(king,0,-1)){return true;}
        if (isValidMove(king,0,1)){return true;}
        if (isValidMove(king,1,-1)){return true;}
        if (isValidMove(king,1,0)){return true;}
        if (isValidMove(king,1,1)){return true;}

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
            if(king.hittingP != null) // si le roi peut prendre une piece
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
        for (Piece piece: simPieces)
        {
            if (piece.color != currentColor)
            {
                    count++;
            }
        }
        // si il reste que le roi
        if (count == 1)
        {
            if (kingCanMove(getKing(true))== false) // le roi peut pas bouger
            {
                return true;
            }
        }
        return false;
    }


    private void checkCastling(){
        if (castlingP != null)
        {
            if (castlingP.col == 0)
            {
                castlingP.col += 3;
            } else if (castlingP.col == 7)
            {
                castlingP.col -= 2;

            }
            castlingP.x = castlingP.getX(castlingP.col);

        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            // on reste la variable twostep a false
            for (Piece piece: pieces)
            {
                if (piece.color == BLACK)
                {
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = WHITE;
            // on reste la variable twostep a false
            for (Piece piece: pieces)
            {
                if (piece.color == WHITE)
                {
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote() { //la promotion du pion
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || activeP.color == BLACK && activeP.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Rook(9,2, currentColor));
                promotionPieces.add(new Bishop(9,3, currentColor));
                promotionPieces.add(new Knight(9,4, currentColor));
                promotionPieces.add(new Queen(9,5, currentColor));
                return true;
            }
        }
        return false;
    }

    private void promoting() { //la promotion
        if(mouse.pressed)
        {
            for (Piece piece: promotionPieces)
            {
                if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) // si la souris est sur une piece de promotion
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
                    //on applique la promotion et donner le tour au joueur adverse
                }

            }
        }
    }


        public void paintComponent (Graphics g) {
            //paintComponent est une méthode dans JComponent que Jpanel intere et utiliser pour dessiner des objets sur le panel
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

         /*   if (gameState == titleState) {
                //affichage du titre
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Book Antiqua", Font.PLAIN, 50));
                g2.setColor(Color.white);
                g2.drawString("Echec", 450, 200);
                g2.setFont(new Font("Book Antiqua", Font.PLAIN, 30));
                g2.drawString("Appuyez sur une touche pour commencer", 350, 300);

            }
             else {
          */

                //Dessine le plateau
                board.draw(g2);
                //Dessine les pieces
                for (Piece p : simPieces) {
                    p.draw(g2);
                }

                if (activeP != null) {
                    if (canMove) {
                        if (isIllegal(activeP) || opponentCanCaptureKing()) // si on fais un move illegal
                        {
                            g2.setColor(Color.red.brighter());
                            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                            g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                        }
                        else {
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
                    g2.drawString("Promotion", 840, 150);
                    for (Piece piece : promotionPieces) {
                        g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                                Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);

                    }
                }
                else {
                    if (currentColor == WHITE) {
                        g2.drawString("Votre tour", 840, 550);
                        if (checkingP != null && checkingP.color == BLACK) { // si echec l'afficher
                            g2.setColor(Color.red);
                            g2.drawString("Echec", 840, 600);
                        }
                    } else {
                        g2.drawString("C'est au tour de l'adversaire", 800, 250);
                        if (checkingP != null && checkingP.color == WHITE) { // si echec l'afficher
                            g2.setColor(Color.red);
                            g2.drawString("Echec", 840, 300);
                        }
                    }
                }
                if (gameover) // si le jeu est fini
                {
                    String winner;
                    if (currentColor == WHITE) {
                        winner = "BLANC";
                    } else {
                        winner = "NOIR";
                    }
                    g2.setColor(Color.red);
                    g2.drawString("Echec et mat", 840, 350);
                    g2.setColor(Color.white);
                    g2.drawString("Le gagnant est " + winner, 840, 400);
                }
                if (stalemate) // si le jeu est fini
                {
                    g2.setColor(Color.white);
                    g2.drawString("Match nul ", 840, 400);
                }
            //}
            }

}




