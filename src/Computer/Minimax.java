package Computer;

import Logic.Game;
import Panel.GamePanel;
import Piece.Move;
import Piece.Piece;

import java.util.ArrayList;

public class Minimax {
    private static final int MAX_DEPTH = 4;

    /**
     * Finds the best move for the given color (white or black) by evaluating all possible legal moves
     * using the minimax algorithm with alpha-beta pruning.
     *
     * @param pieces The list of pieces on the board.
     * @param color The color of the player whose best move is being evaluated (e.g., WHITE or BLACK).
     * @return The best move for the given color.
     */
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

    /**
     * Recursively evaluates the game tree using the minimax algorithm with alpha-beta pruning.
     * It returns the evaluation score for the current game state at the specified depth.
     *
     * @param pieces The list of pieces on the board.
     * @param depth The current depth of the search tree.
     * @param color The color of the player whose turn it is (e.g., WHITE or BLACK).
     * @param alpha The best value that the maximizer can guarantee so far (alpha value for pruning).
     * @param beta The best value that the minimizer can guarantee so far (beta value for pruning).
     * @return The evaluation score for the given board state.
     */
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