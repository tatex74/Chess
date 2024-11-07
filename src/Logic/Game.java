package Logic;

import Computer.Minimax;
import Panel.Board;
import Panel.GamePanel;
import Piece.*;

import java.util.ArrayList;

public class Game {
    public ArrayList<Piece> pieces = new ArrayList<>();
    public ArrayList<Piece> simPieces = new ArrayList<>();
    public ArrayList<String> Historizes = new ArrayList<>();
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

    private void loadGame() {
        Save.loadSave(this);

        switch (gameMode) {
            case PLAYERVSPLAYER -> startPlayerVsPlayer();
            case PLAYERVSCOMPUTER -> startPlayerVsComputer();
        }
    }

    public void launchGame(int gameMode) {
        switch (gameMode) {
            case PLAYERVSPLAYER -> startPlayerVsPlayer();
            case PLAYERVSCOMPUTER -> startPlayerVsComputer();
            case LOADGAME -> loadGame();
        }
    }

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


    public void update(Mouse mouse) {
        this.mouse = mouse;

        if (gameMode == PLAYERVSCOMPUTER) {
            if (currentColor == WHITE) {
                mouseEventOnPiece();
            } else {
                Move AIMove = Minimax.findBestMove(pieces, BLACK);
                Move.makeMove(pieces, AIMove);
                Move.recordMove(Piece.getPieceByCoord(pieces, AIMove.targetCol, AIMove.targetRow), Historizes,
                        AIMove.col, AIMove.row, AIMove.targetCol, AIMove.targetRow);
                changePlayer();
            }
        } else {
            mouseEventOnPiece();
        }
    }

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
                        Move.recordMove(activeP, Historizes, activeP.preCol, activeP.preRow, activeP.col, activeP.row);
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
                            changePlayer();
                        }

                        activeP = null;
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
        Historizes.clear();
        timers.resetTimers(gameDuration);
        timers.invertTimer(currentColor);
    }

    public void abandonGame() {
        ff = true;
    }

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

    public static boolean isGameOver(ArrayList<Piece> pieces) {
        return isCheckmate(pieces, WHITE) && isCheckmate(pieces, BLACK) &&
                isStalemate(pieces, WHITE) && isStalemate(pieces, BLACK);
    }

    public static Piece getKing(ArrayList<Piece> listPieces, int color) // on recupere le roi
    {
        for (Piece piece : listPieces) {
            if (piece.type == Type.KING && piece.color == color) {
                return piece;
            }
        }
        return null;
    }

    public static int changeColor(int color) {
        if (color == WHITE) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }
}
