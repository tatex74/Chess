package Computer;

import Main.GamePanel;
import Piece.Piece;
import Main.Type;

import java.util.ArrayList;

class Evaluator {
    private static final int PAWN_VALUE = 1;
    private static final int KNIGHT_VALUE = 3;
    private static final int BISHOP_VALUE = 3;
    private static final int ROOK_VALUE = 5;
    private static final int QUEEN_VALUE = 9;
    private static final int KING_VALUE = 1000;

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