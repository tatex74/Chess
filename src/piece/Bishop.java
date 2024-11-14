package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;

public class Bishop extends Piece {

    /**
     * Constructs a Bishop piece with a specified position, color, and option to set an image.
     *
     * @param col      The column position of the bishop.
     * @param row      The row position of the bishop.
     * @param color    The color of the bishop (e.g., Game.WHITE or Game.BLACK).
     * @param isImage  A flag indicating whether to assign an image to the bishop.
     */
    public Bishop(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.BISHOP;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-bishop");
            } else {
                image = getImage("/Piece/b-bishop");
            }
        }
    }

    /**
     * Checks if the bishop can legally move to the specified target position on the board,
     * verifying the path, legality, and absence of obstructions along its diagonal.
     *
     * @param pieces        A list of all pieces currently on the board.
     * @param targetCol     The target column position for the bishop.
     * @param targetRow     The target row position for the bishop.
     * @param verifyLegal   A flag to check if the move places the player in check.
     * @return              True if the bishop can move to the target position; false otherwise.
     */
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow, boolean verifyLegal) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            if (targetCol != preCol || targetRow != preRow) { // Vérifie que la case cible est différente de la case actuelle
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
        }
        return false;
    }

    /**
    * Returns the name of the bishop in French.
    *
    * @return The name "Fou" (French for Bishop).
    */
    @Override
    public String getName() {
        return "Fou";
    }
}