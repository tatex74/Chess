package Computer;

import Main.GamePanel;
import Main.Type;
import Piece.Move;
import Piece.Piece;
import Piece.Queen;

import java.util.ArrayList;

public class Minimax {
    private static final int MAX_DEPTH = 3;

    public static Move findBestMove(ArrayList<Piece> pieces, int color) {
        int bestValue;
        if (color == GamePanel.WHITE) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        Move bestMove = null;

        ArrayList<Move> moves = Move.generateLegalMoves(pieces, color);

        for (Move move : moves) {
            ArrayList<Piece> piecesCopy = realPiecesCopy(pieces);
            makeMove(piecesCopy, move);

            int boardValue = minimax(piecesCopy, MAX_DEPTH, changeColor(color), Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (color == GamePanel.WHITE) {
                if (boardValue > bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    private static int minimax(ArrayList<Piece> pieces, int depth, int color, int alpha, int beta) {

        if (depth == 0 || isGameOver(pieces)) {
            return Evaluator.evaluate(pieces, color);
        }

        ArrayList<Move> moves = Move.generateLegalMoves(pieces, color);

        if (color == GamePanel.WHITE) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                ArrayList<Piece> piecesCopy = realPiecesCopy(pieces);
                makeMove(piecesCopy, move);
                int eval = minimax(piecesCopy, depth - 1, GamePanel.BLACK, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;  // Beta pruning
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                ArrayList<Piece> piecesCopy = realPiecesCopy(pieces);
                makeMove(piecesCopy, move);
                int eval = minimax(piecesCopy, depth - 1, GamePanel.WHITE, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;  // Alpha pruning
            }
            return minEval;
        }
    }

    public static void makeMove(ArrayList<Piece> pieces, Move move) {
        Piece piece = Piece.getPieceByCoord(pieces, move.col, move.row);
        Piece hittingP = Piece.getPieceByCoord(pieces, move.targetCol, move.targetRow);

        // eating piece
        if (hittingP != null) {
           pieces.remove(hittingP);
        }

        // normal move
        piece.preCol = move.targetCol;
        piece.preRow = move.targetRow;
        piece.moved = true;

        // castling
        if (piece.type == Type.KING) {
            Piece rook = null;
            if (move.targetCol == move.col + 2) {
                rook = Piece.getPieceByCoord(pieces, 7, move.targetRow);
                rook.preCol = 5;
                rook.moved = true;
            } else if (move.targetCol == move.col - 2) {
                rook = Piece.getPieceByCoord(pieces, 0, move.targetRow);
                rook.preCol = 3;
                rook.moved = true;
            }
        }

        // promoting
        if (piece.type == Type.PAWN) {
            if (piece.color == GamePanel.WHITE && piece.preRow == 0 || piece.color == GamePanel.BLACK && piece.preRow == 7) {
                // The Computer always choose a queen even if in some case (very thin) choosing a knight can be better
                pieces.add(new Queen(piece.preCol, piece.preRow, piece.color));
                pieces.remove(piece);
            }
        }
    }

    private static ArrayList<Piece> realPiecesCopy(ArrayList<Piece> pieces) {
        ArrayList<Piece> piecesCopy = new ArrayList<>();
        for (Piece piece : pieces) {
            piecesCopy.add(piece.copyPiece());
        }

        return piecesCopy;
    }

    private static boolean isGameOver(ArrayList<Piece> pieces) {
        return Move.isCheckmate(pieces, GamePanel.WHITE) && Move.isCheckmate(pieces, GamePanel.BLACK) &&
                Move.isStalemate(pieces, GamePanel.WHITE) && Move.isStalemate(pieces, GamePanel.BLACK);
    }

    private static int changeColor(int color) {
        if (color == GamePanel.WHITE) {
            return GamePanel.BLACK;
        } else {
            return GamePanel.WHITE;
        }
    }
}