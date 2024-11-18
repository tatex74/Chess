package Piece;

import Logic.Game;
import Panel.GamePanel;

import java.util.ArrayList;

public class Move {
    public int col, row;
    public int targetCol, targetRow;

    /**
     * Constructs a move with a starting position and a target position.
     *
     * @param col        The starting column of the piece.
     * @param startY     The starting row of the piece.
     * @param targetCol  The target column of the piece.
     * @param targetRow  The target row of the piece.
     */
    public Move(int col, int startY, int targetCol, int targetRow) {
        this.col = col;
        this.row = startY;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }


    /**
     * Generates a list of all legal moves for a player based on the pieces on the board.
     * Only pieces of the given color are considered.
     *
     * @param pieces A list of all pieces on the board.
     * @param color  The color of the player for whom to generate legal moves (e.g., Game.WHITE or Game.BLACK).
     * @return A list of all legal moves for the given player.
     */
    public static ArrayList<Move> generateLegalMoves(ArrayList<Piece> pieces, int color) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece.color == color) {
                moves.addAll(listAllPieceMove(pieces, piece));
            }
        }

        return moves;
    }

    /**
     * Lists all possible moves for a given piece.
     *
     * @param pieces A list of all pieces on the board.
     * @param piece  The piece for which to list possible moves.
     * @return A list of all possible moves for the given piece.
     */
    public static ArrayList<Move> listAllPieceMove(ArrayList<Piece> pieces, Piece piece) {
        ArrayList<Move> possibleMove = new ArrayList<>();

        for (int c = 0 ; c < 8 ; c++) {
            for (int r = 0 ; r < 8 ; r++) {
                if (piece.canMove(pieces, r, c, true)) {
                    possibleMove.add(new Move(piece.preCol, piece.preRow, r, c));
                }
            }
        }

        return possibleMove;
    }

    /**
     * Executes a move by updating the board state.
     * This method moves a piece from its starting position to the target position.
     *
     * @param pieces A list of all pieces on the board.
     * @param move   The move to be executed.
     * @return True if the move was successful; false otherwise.
     */
    public static boolean makeMove(ArrayList<Piece> pieces, Move move) {
        Piece piece = Piece.getPieceByCoord(pieces, move.col, move.row);
        if (piece == null) return false;

        Piece hittingP = Piece.getPieceByCoord(pieces, move.targetCol, move.targetRow);
        // eating piece
        if (hittingP != null) {
            pieces.remove(hittingP.getIndex(pieces));
        }

        // normal move
        piece.col = move.targetCol;
        piece.row = move.targetRow;

        if (piece.type == Type.PAWN) {
            if (Math.abs(piece.row - piece.preRow) == 2) {
                piece.twoStepped = true;
            }
        }

        piece.x = piece.getX(piece.col);
        piece.y = piece.getY(piece.row);
        piece.preCol = piece.col;
        piece.preRow = piece.row;
        piece.moved = true;

        // castling
        if (piece.type == Type.KING) {
            Piece rook = null;
            if (move.targetCol == move.col + 2) {
                rook = Piece.getPieceByCoord(pieces, 7, move.targetRow);
                rook.col = 5;
                rook.preCol = 5;
                rook.moved = true;
            } else if (move.targetCol == move.col - 2) {
                rook = Piece.getPieceByCoord(pieces, 0, move.targetRow);
                rook.col = 3;
                rook.preCol = 3;
                rook.moved = true;
            }
        }

        // promoting
        if (piece.type == Type.PAWN) {
            if (piece.color == Game.WHITE && piece.preRow == 0 || piece.color == Game.BLACK && piece.preRow == 7) {
                // The Computer always choose a queen even if in some case (very thin) choosing a knight can be better
                pieces.add(new Queen(piece.preCol, piece.preRow, piece.color, true));
                pieces.remove(piece.getIndex(pieces));
            }
        }

        return true;
    }


    /**
     * Converts a column index to its corresponding chess notation character (a-h).
     *
     * @param col The column index (0-7).
     * @return The corresponding chess column notation ('a'-'h').
     */
    public static char colToChessCol(int col) {
        return switch (col) {
            case 0 -> 'a';
            case 1 -> 'b';
            case 2 -> 'c';
            case 3 -> 'd';
            case 4 -> 'e';
            case 5 -> 'f';
            case 6 -> 'g';
            case 7 -> 'h';
            default -> ' ';
        };
    }

    /**
     * Converts a row index to its corresponding chess notation character (1-8).
     *
     * @param row The row index (0-7).
     * @return The corresponding chess row notation ('1'-'8').
     */
    public static char rowToChessRow(int row) {
        return switch (row) {
            case 0 -> '8';
            case 1 -> '7';
            case 2 -> '6';
            case 3 -> '5';
            case 4 -> '4';
            case 5 -> '3';
            case 6 -> '2';
            case 7 -> '1';
            default -> ' ';
        };
    }

    /**
     * Returns a string representation of the move in chess notation (e.g., "e2 e4").
     *
     * @return The string representation of the move in chess notation.
     */
    public String toString() {
        return ("" + colToChessCol(col) + rowToChessRow(row) + " " + colToChessCol(targetCol) + rowToChessRow(targetRow));
    }
}
