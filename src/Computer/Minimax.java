package Computer;

import Logic.Game;
import Panel.GamePanel;
import Piece.Move;
import Piece.Piece;

import java.util.ArrayList;

public class Minimax {
    private static final int MAX_DEPTH = 4;

    public static Move findBestMove(ArrayList<Piece> pieces, int color) {
        int bestValue;
        if (color == Game.WHITE) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        Move bestMove = null;

        ArrayList<Move> moves = Move.generateLegalMoves(pieces, color);

        for (Move move : moves) {
            ArrayList<Piece> piecesCopy = Piece.copyPieces(pieces);
            Move.makeMove(piecesCopy, move);

            int boardValue = minimax(piecesCopy, MAX_DEPTH, Game.changeColor(color), Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (color == Game.WHITE) {
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

        if (depth == 0 || Game.isGameOver(pieces)) {
            return Evaluator.evaluate(pieces, color);
        }

        ArrayList<Move> moves = Move.generateLegalMoves(pieces, color);

        if (color == Game.WHITE) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                ArrayList<Piece> piecesCopy = Piece.copyPieces(pieces);
                if(Move.makeMove(piecesCopy, move)) {
                    int eval = minimax(piecesCopy, depth - 1, Game.BLACK, alpha, beta);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;  // Beta pruning
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                ArrayList<Piece> piecesCopy = Piece.copyPieces(pieces);
                if(Move.makeMove(piecesCopy, move)) {
                    int eval = minimax(piecesCopy, depth - 1, Game.WHITE, alpha, beta);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;  // Alpha pruning
                }
            }
            return minEval;
        }
    }
}