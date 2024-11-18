package Piece;
import Logic.Game;
import Panel.Board;
import Panel.GamePanel;

import java.util.ArrayList;

public class Knight extends Piece {

    /**
     * Constructs a Knight piece with a specified position, color, and option to set an image.
     *
     * @param col      The column position of the knight.
     * @param row      The row position of the knight.
     * @param color    The color of the knight (e.g., Game.WHITE or Game.BLACK).
     * @param isImage  A flag indicating whether to assign an image to the knight.
     */
    public Knight(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.KNIGHT;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-knight");
            } else {
                image = getImage("/Piece/b-knight");
            }
        }
    }

    /**
     * Checks if the knight can legally move to the specified target position, considering its
     * unique movement pattern (an "L" shape).
     *
     * @param pieces       A list of all pieces currently on the board.
     * @param targetCol    The target column position for the knight.
     * @param targetRow    The target row position for the knight.
     * @param verifyLegal  A flag to check if the move places the player in check.
     * @return             True if the knight can move to the target position; false otherwise.
     */
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow, boolean verifyLegal) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            //calcul de la distance entre la case de départ et la case d'arrivée du chevalier
            if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
                if (isValidSquare(pieces, targetCol, targetRow)) {
                    if (verifyLegal) {
                        return isLegalMove(pieces, targetCol, targetRow);
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the name of the knight in French.
     *
     * @return The name "Cavalier" (French for Knight).
     */
    @Override
    public String getName() {
        return "Cavalier";
    }
}