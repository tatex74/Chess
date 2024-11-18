package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;


public class Pawn extends Piece {

    /**
     * Constructs a Pawn piece with a specified position, color, and option to set an image.
     *
     * @param col      The column position of the pawn.
     * @param row      The row position of the pawn.
     * @param color    The color of the pawn (e.g., Game.WHITE or Game.BLACK).
     * @param isImage  A flag indicating whether to assign an image to the pawn.
     */
    public Pawn(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.PAWN;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-pawn");
            } else {
                image = getImage("/Piece/b-pawn");
            }
        }
    }

    /**
     * Checks if the pawn can legally move to the target position, considering its unique movement rules.
     * The pawn moves one square forward, captures diagonally, and can move two squares on its first move.
     * It also supports en passant capturing.
     *
     * @param pieces       A list of all pieces currently on the board.
     * @param targetCol    The target column position for the pawn.
     * @param targetRow    The target row position for the pawn.
     * @param verifyLegal  A flag to check if the move places the player in check.
     * @return             True if the pawn can move to the target position; false otherwise.
     */
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow, boolean verifyLegal) {
        if (Board.isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            //definnir le mouvement selon la couleur
            int moveValue;
            if (color == Game.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }
            //on regarde où il peut attaque
            hittingP = getHittingP(pieces, targetCol, targetRow);

            //mouvement d'un carré
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                if (verifyLegal) {
                    return isLegalMove(pieces, targetCol, targetRow);
                } else {
                    return true;
                }
            }
            //mouvement de deux carrés
            if(targetCol == preCol && targetRow == preRow + 2*moveValue && hittingP == null && !moved
            && !isPieceOnStraightLine(pieces, targetCol, targetRow)){
                if (verifyLegal) {
                    return isLegalMove(pieces, targetCol, targetRow);
                } else {
                    return true;
                }
            }
            // capture diagonale
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
            hittingP.color != color) {
                if (verifyLegal) {
                    return isLegalMove(pieces, targetCol, targetRow);
                } else {
                    return true;
                }
            }

            //en passant
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue)
            {
                for (Piece piece : pieces)
                {
                    if (piece.col == targetCol && piece.row == preRow && piece.twoStepped)
                    {
                        hittingP = piece;
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
     * Returns the name of the pawn in French.
     *
     * @return The name "Pion" (French for Pawn).
     */
    @Override
    public String getName() {
        return "Pion";
    }
}

