package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;

public class Queen extends Piece {

    /**
     * Constructs a Queen piece with a specified position, color, and option to set an image.
     *
     * @param col      The column position of the queen.
     * @param row      The row position of the queen.
     * @param color    The color of the queen (e.g., Game.WHITE or Game.BLACK).
     * @param isImage  A flag indicating whether to assign an image to the queen.
     */
    public Queen(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.QUEEN;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-queen");
            } else {
                image = getImage("/Piece/b-queen");
            }
        }
    }

    /**
     * Checks if the queen can legally move to the target position, considering its combined movement rules.
     * The queen can move horizontally, vertically, and diagonally, similar to both a rook and a bishop.
     *
     * @param pieces       A list of all pieces currently on the board.
     * @param targetCol    The target column position for the queen.
     * @param targetRow    The target row position for the queen.
     * @param verifyLegal  A flag to check if the move places the player in check.
     * @return             True if the queen can move to the target position; false otherwise.
     */
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow, boolean verifyLegal) {
        if (Board.isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(pieces, targetCol, targetRow) && !isPieceOnStraightLine(pieces, targetCol, targetRow)) {
                    if (verifyLegal) {
                        return isLegalMove(pieces, targetCol, targetRow);
                    } else {
                        return true;
                    }
                }
            }
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(pieces, targetCol, targetRow) && !isPieceOnDiagonalLine(pieces, targetCol, targetRow)) {
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
     * Returns the name of the queen in French.
     *
     * @return The name "Reine" (French for Queen).
     */
    @Override
    public String getName() {
        return "Reine";
    }

}