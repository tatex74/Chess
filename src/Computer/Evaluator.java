package Computer;

import Piece.Piece;
import Piece.Type;

import java.util.ArrayList;

class Evaluator {
    private static final int PAWN_VALUE = 1;
    private static final int KNIGHT_VALUE = 3;
    private static final int BISHOP_VALUE = 3;
    private static final int ROOK_VALUE = 5;
    private static final int QUEEN_VALUE = 9;
    private static final int KING_VALUE = 1000;

    /**
     * Evaluates the board and returns a score based on the types and colors of the pieces on the board.
     * The score is calculated by summing the value of each piece on the board, with positive values for pieces
     * belonging to the specified color, and negative values for pieces belonging to the opponent.
     *
     * @param pieces The list of pieces on the board.
     * @param color  The color of the player whose perspective is being evaluated (e.g., WHITE or BLACK).
     * @return The evaluated score for the given color. Positive values favor the specified color, negative values favor the opponent.
     */
    public static int evaluate(ArrayList<Piece> pieces, int color) {
        int score = 0;
        int sign;

        for (Piece piece : pieces) {
            if (piece.color == color) {
                sign = 1;
            } else {
                sign = -1;
            }
            score += sign * switch (piece.type) {
                case Type.PAWN -> PAWN_VALUE;
                case Type.KNIGHT -> KNIGHT_VALUE;
                case Type.BISHOP -> BISHOP_VALUE;
                case Type.ROOK -> ROOK_VALUE;
                case Type.QUEEN -> QUEEN_VALUE;
                case Type.KING -> KING_VALUE;
            };

        }

        return score;
    }
}