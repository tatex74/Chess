package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;

public class King extends Piece {

    /**
     * Constructs a King piece with a specified position, color, and option to set an image.
     *
     * @param col      The column position of the king.
     * @param row      The row position of the king.
     * @param color    The color of the king (e.g., Game.WHITE or Game.BLACK).
     * @param isImage  A flag indicating whether to assign an image to the king.
     */
    public King(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.KING;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-king");
            } else {
                image = getImage("/Piece/b-king");
            }
        }
    }

    /**
     * Checks if the king can legally move to the specified target position, considering regular
     * movement (one square in any direction), castling rules, path validity, and legality.
     *
     * @param pieces       A list of all pieces currently on the board.
     * @param targetCol    The target column position for the king.
     * @param targetRow    The target row position for the king.
     * @param verifyLegal  A flag to check if the move places the player in check.
     * @return             True if the king can move to the target position; false otherwise.
     */
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow, boolean verifyLegal) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || //se deplace en carré
                    Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1)  //se deplace en diagonale
            {
                if (isValidSquare(pieces, targetCol, targetRow)) {
                    if (verifyLegal) {
                        return isLegalMove(pieces, targetCol, targetRow);
                    } else {
                        return true;
                    }
                }
            }

            // Castling
            if(!this.moved && targetRow == preRow &&
                    !isPieceOnStraightLine(pieces, targetCol, targetRow) &&
                    (targetCol == preCol + 2 || targetCol == preCol - 2)) {
                Piece rook;
                if (targetCol == preCol + 2) {
                    rook = getHittingP(pieces, 7, targetRow);
                }
                else  {
                    rook = getHittingP(pieces, 0, targetRow);
                }
                if (rook != null && !rook.moved) {
                    if (!Game.isKingInCheck(pieces, this.color, true)) {
                        if (verifyLegal) {
                            return isLegalMove(pieces, preCol + (targetCol-this.preCol)/2, targetRow) &&
                                    isLegalMove(pieces, targetCol, targetRow);
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
     * Returns the name of the king in French.
     *
     * @return The name "Roi" (French for King).
     */
    @Override
    public String getName() {
        return "Roi";
    }

}