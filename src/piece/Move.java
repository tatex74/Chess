package Piece;

import Main.Type;

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
                if (piece.canMove(pieces, r, c)) {
                    possibleMove.add(new Move(piece.preCol, piece.preRow, r, c));
                }
            }
        }

        return possibleMove;
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

    public static boolean isKingInCheck(ArrayList<Piece> listPieces, int color) // On regarde si le roi est en echec
    {

        Piece king = getKing(listPieces, color);
        if (king == null) {
            return false;
        }

        for (Piece piece : listPieces) {
            if (piece.color != color) {
                if (piece.canMove(listPieces, king.preCol, king.preRow)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isCheckmate(ArrayList<Piece> pieces, int color) {
        if (isKingInCheck(pieces, color)) {
            for (Piece piece : pieces) {
                if (piece.color == color && !listAllPieceMove(pieces, piece).isEmpty()) {
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
        if (!isKingInCheck(pieces, color)) {
            for (Piece piece : pieces) {
                if (piece.color == color && !listAllPieceMove(pieces, piece).isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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

}
