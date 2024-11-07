package Piece;

import Logic.Game;
import Panel.GamePanel;

import java.util.ArrayList;

public class Move {
    public int col, row;
    public int targetCol, targetRow;

    public Move(int col, int startY, int targetCol, int targetRow) {
        this.col = col;
        this.row = startY;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }

    public static ArrayList<Move> generateLegalMoves(ArrayList<Piece> pieces, int color) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece.color == color) {
                moves.addAll(listAllPieceMove(pieces, piece));
            }
        }

        return moves;
    }

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

    public static boolean makeMove(ArrayList<Piece> pieces, Move move) {
        Piece piece = Piece.getPieceByCoord(pieces, move.col, move.row);
        if (piece == null) return false;

        Piece hittingP = Piece.getPieceByCoord(pieces, move.targetCol, move.targetRow);
        // eating piece
        if (hittingP != null) {
            pieces.remove(hittingP);
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
                pieces.remove(piece);
            }
        }

        return true;
    }

    public static void recordMove(Piece piece, ArrayList<String> historizes, int oldCol, int oldRow, int newCol, int newRow) {
        char oldChessCol = colToChessCol(oldCol);
        char newChessCol = colToChessCol(newCol);
        char oldChessRow = rowToChessRow(oldRow);
        char newChessRow = rowToChessRow(newRow);

        historizes.add(piece.getName() + " de (" + oldChessCol + ", " + oldChessRow + ") à (" + newChessCol + ", " + newChessRow + ")");
    }

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

    public String toString() {
        return ("" + colToChessCol(col) + rowToChessRow(row) + " " + colToChessCol(targetCol) + rowToChessRow(targetRow));
    }
}
