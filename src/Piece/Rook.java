package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;

public class Rook extends Piece {

    /**
     * Constructs a Rook piece with a specified position, color, and option to set an image.
     *
     * @param col      The column position of the rook.
     * @param row      The row position of the rook.
     * @param color    The color of the rook (e.g., Game.WHITE or Game.BLACK).
     * @param isImage  A flag indicating whether to assign an image to the rook.
     */
    public Rook(int col, int row, int color, boolean isImage) {

        super(col, row, color);

        type = Type.ROOK;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-rook");
            } else {
                image = getImage("/Piece/b-rook");
            }
        }
    }

    /**
     * Checks if the rook can legally move to the target position.
     * The rook can move horizontally or vertically any number of squares, but not diagonally.
     *
     * @param pieces       A list of all pieces currently on the board.
     * @param targetCol    The target column position for the rook.
     * @param targetRow    The target row position for the rook.
     * @param verifyLegal  A flag to check if the move places the player in check.
     * @return             True if the rook can move to the target position; false otherwise.
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
        }
        return false;
    }

    /**
     * Returns the name of the rook in French.
     *
     * @return The name "Tour" (French for Rook).
     */
    @Override
    public String getName() {
        return "Tour";
    }


}