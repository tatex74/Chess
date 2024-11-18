package Logic;

import Computer.Minimax;
import Panel.Board;
import Panel.GamePanel;
import Piece.*;

import java.util.ArrayList;

public class Game {
    public ArrayList<Piece> pieces = new ArrayList<>();
    public ArrayList<Piece> simPieces = new ArrayList<>();
    public Historize historize = new Historize();
    public ArrayList<Piece> promotionPieces = new ArrayList<>();
    public Piece activeP, checkingP;
    public static Piece castlingP;

    public GameTimers timers;
    public int gameDuration = 10 * 60;

    GamePanel gamePanel;
    Mouse mouse;

    public boolean canMove;
    public boolean promotion;
    public boolean checkmate;
    public boolean stalemate;
    public boolean ff;
    public boolean timeout;

    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public int currentColor = WHITE;

    public static final int PLAYERVSPLAYER = 0;
    public static final int PLAYERVSCOMPUTER = 1;
    public static final int LOADGAME = 2;
    public int gameMode;


    public Game(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        timers = new GameTimers(gameDuration);
    }

    /**
     * Initializes a new game with a player vs player mode.
     */
    public void startPlayerVsPlayer() {
        gameMode = PLAYERVSPLAYER;
        checkmate = false;
        stalemate = false;
        promotion = false;
        ff = false;
        timeout = false;
        if (pieces.isEmpty()) {
            setPieces();
        }
        copyPieces(pieces, simPieces);
        timers.invertTimer(currentColor);
    }

    /**
     * Initializes a new game with a player vs computer mode.
     */
    private void startPlayerVsComputer() {
        gameMode = PLAYERVSCOMPUTER;
        checkmate = false;
        stalemate = false;
        promotion = false;
        ff = false;
        timeout = false;
        if (pieces.isEmpty()) {
            setPieces();
        }
        copyPieces(pieces, simPieces);
    }

    /**
     * Loads the game from a saved state.
     */
    private void loadGame() {
        Save.loadSave(this);

        switch (gameMode) {
            case PLAYERVSPLAYER -> startPlayerVsPlayer();
            case PLAYERVSCOMPUTER -> startPlayerVsComputer();
        }
    }

    /**
     * Launches the game with the specified mode (player vs player, player vs computer, or load game).
     *
     * @param gameMode The game mode to start (0 = player vs player, 1 = player vs computer, 2 = load game).
     */
    public void launchGame(int gameMode) {
        switch (gameMode) {
            case PLAYERVSPLAYER -> startPlayerVsPlayer();
            case PLAYERVSCOMPUTER -> startPlayerVsComputer();
            case LOADGAME -> loadGame();
        }
    }

    /**
     * Sets up the pieces on the board at the start of the game.
     */
    public void setPieces() {
        pieces.add(new Rook(0, 0, Game.BLACK, true));
        pieces.add(new Knight(1, 0, Game.BLACK, true));
        pieces.add(new Bishop(2, 0, Game.BLACK, true));
        pieces.add(new Queen(3, 0, Game.BLACK, true));
        pieces.add(new King(4, 0, Game.BLACK, true));
        pieces.add(new Bishop(5, 0, Game.BLACK, true));
        pieces.add(new Knight(6, 0, Game.BLACK, true));
        pieces.add(new Rook(7, 0, Game.BLACK, true));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(i, 1, Game.BLACK, true));
        }

        pieces.add(new Rook(0, 7, Game.WHITE, true));
        pieces.add(new Knight(1, 7, Game.WHITE, true));
        pieces.add(new Bishop(2, 7, Game.WHITE, true));
        pieces.add(new Queen(3, 7, Game.WHITE, true));
        pieces.add(new King(4, 7, Game.WHITE, true));
        pieces.add(new Bishop(5, 7, Game.WHITE, true));
        pieces.add(new Knight(6, 7, Game.WHITE, true));
        pieces.add(new Rook(7, 7, Game.WHITE, true));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(i, 6, Game.WHITE, true));
        }

        gamePanel.repaint();
    }

    /**
     * Updates the game state based on the mouse input. Handles player and computer moves.
     *
     * @param mouse The mouse input object representing the current mouse position.
     */
    public void update(Mouse mouse) {
        this.mouse = mouse;

        if (gameMode == PLAYERVSCOMPUTER) {
            if (currentColor == WHITE) {
                mouseEventOnPiece();
            } else {
                Move AIMove = Minimax.findBestMove(simPieces, BLACK);
                Move.makeMove(simPieces, AIMove);
                historize.recordMove(Piece.getPieceByCoord(simPieces, AIMove.targetCol, AIMove.targetRow),
                        AIMove.col, AIMove.row, AIMove.targetCol, AIMove.targetRow);
                checkmate = isCheckmate(simPieces, changeColor(currentColor));
                stalemate = isStalemate(simPieces, changeColor(currentColor));
                copyPieces(simPieces, pieces);
                changePlayer();
            }
        } else {
            mouseEventOnPiece();
        }

    }

    /**
     * Handles mouse events when a piece is being dragged and moved.
     */
    public void mouseEventOnPiece() {
        if (promotion) {
            promoting();// on promouvoit le pion
        } else if (!checkmate && !stalemate && !ff && !timeout) {
            if (mouse.pressed) {
                if (activeP == null)
                {
                    activeP = Piece.getPieceByCoord(simPieces, mouse.x/ Board.SQUARE_SIZE, mouse.y/Board.SQUARE_SIZE);
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
                        historize.recordMove(activeP, activeP.preCol, activeP.preRow, activeP.col, activeP.row);
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }

                        checkmate = isCheckmate(pieces, changeColor(activeP.color));
                        stalemate = isStalemate(pieces, changeColor(activeP.color));

                        if (canPromote()) {
                            promotion = true;
                        } else {
                            activeP = null;
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

    /**
     * Simulates the movement of the currently selected piece based on the mouse position.
     */
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
        if (activeP.canMove(simPieces, activeP.col, activeP.row, true)) {
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

    /**
     * Switches the turn to the next player.
     */
    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;

            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = WHITE;

            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }

        if (gameMode == PLAYERVSPLAYER) {
            timers.invertTimer(currentColor);
        }

        activeP = null;
    }

    /**
     * Checks if a pawn has reached the opposite side of the board and can be promoted.
     *
     * @return True if the pawn can be promoted, false otherwise.
     */
    private boolean canPromote() { //la promotion du pion
        if (activeP.type == Type.PAWN) {
            if (currentColor == Game.WHITE && activeP.row == 0 || activeP.color == BLACK && activeP.row == 7) {
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

    /**
     * Handles the pawn promotion process, allowing the player to choose a new piece.
     */
    private void promoting() { //la promotion
        timers.stopTimers();

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
                    promotion = false;
                    changePlayer();
                    timers.invertTimer(currentColor);
                    //on applique la promotion et donner le tour au joueur adverse
                }

            }
        }
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        pieces.clear();
        simPieces.clear();
        setPieces();
        copyPieces(pieces, simPieces);
        currentColor = WHITE;
        checkmate = false;
        stalemate = false;
        promotion = false;
        ff = false;
        timeout = false;
        activeP = null;
        checkingP = null;
        castlingP = null;
        historize.clear();
        timers.resetTimers(gameDuration);
        timers.invertTimer(currentColor);
    }

    /**
     * Abandons the game and ends the current session.
     */
    public void abandonGame() {
        ff = true;
    }

    /**
     * Checks if the king of the specified color is in check.
     *
     * @param listPieces The list of all pieces on the board.
     * @param color The color of the player to check (WHITE or BLACK).
     * @param verifyLegal A flag indicating whether to verify if the move is legal.
     * @return True if the king is in check, false otherwise.
     */
    public static boolean isKingInCheck(ArrayList<Piece> listPieces, int color, boolean verifyLegal) // On regarde si le roi est en echec
    {


        Piece king = getKing(listPieces, color);
        if (king == null) {
            return false;


        }

        for (Piece piece : listPieces) {
            if (piece.color != color) {
                if (piece.canMove(listPieces, king.preCol, king.preRow, verifyLegal)) {
                    return true;

                }
            }
        }

        return false;
    }

    /**
     * Checks if the specified color's king is in checkmate.
     *
     * @param pieces The list of all pieces on the board.
     * @param color The color of the player to check (WHITE or BLACK).
     * @return True if the king is in checkmate, false otherwise.
     */
    public static boolean isCheckmate(ArrayList<Piece> pieces, int color) {
        if (isKingInCheck(pieces, color, true)) {
            for (Piece piece : pieces) {
                if (piece.color == color && !Move.listAllPieceMove(pieces, piece).isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the specified color's king is in stalemate.
     *
     * @param pieces The list of all pieces on the board.
     * @param color The color of the player to check (WHITE or BLACK).
     * @return True if the king is in stalemate, false otherwise.
     */
    public static boolean isStalemate(ArrayList<Piece> pieces, int color) //verification si c'est pat
    {
        if (!isKingInCheck(pieces, color, true)) {
            for (Piece piece : pieces) {
                if (piece.color == color && !Move.listAllPieceMove(pieces, piece).isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the game is over, either by checkmate or stalemate for both players.
     *
     * @param pieces The list of all pieces on the board.
     * @return True if the game is over, false otherwise.
     */
    public static boolean isGameOver(ArrayList<Piece> pieces) {
        return isCheckmate(pieces, WHITE) && isCheckmate(pieces, BLACK) &&
                isStalemate(pieces, WHITE) && isStalemate(pieces, BLACK);
    }

    /**
     * Retrieves the king of the specified color from the list of pieces.
     *
     * @param listPieces The list of all pieces on the board.
     * @param color The color of the king to retrieve (WHITE or BLACK).
     * @return The king piece, or null if no king is found.
     */
    public static Piece getKing(ArrayList<Piece> listPieces, int color) // on recupere le roi
    {
        for (Piece piece : listPieces) {
            if (piece.type == Type.KING && piece.color == color) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Changes the color.
     *
     * @param color color (WHITE or BLACK).
     * @return The opposite color (BLACK or WHITE).
     */
    public static int changeColor(int color) {
        if (color == WHITE) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    /**
     * Copies the list of pieces from the source to the target list.
     *
     * @param source The list of pieces to copy from.
     * @param target The list to copy the pieces to.
     */
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }
}
