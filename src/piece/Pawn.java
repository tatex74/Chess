package Piece;
import Main.Board;
import Main.GamePanel;
import Main.Type;

import java.util.ArrayList;


public class Pawn extends Piece {

    public Pawn(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.PAWN;

        if (isImage) {
            if (color == GamePanel.WHITE) {
                image = getImage("/Piece/w-pawn");
            } else {
                image = getImage("/Piece/b-pawn");
            }
        }
    }

    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if (Board.isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            //definnir le mouvement selon la couleur
            int moveValue;
            if (color == GamePanel.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }
            //on regarde où il peut attaque
            hittingP = getHittingP(pieces, targetCol, targetRow);

            //mouvement d'un carré
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                    return isLegalMove(pieces, targetCol, targetRow);
            }
            //mouvement de deux carrés
            if(targetCol == preCol && targetRow == preRow + 2*moveValue && hittingP == null && !moved
            && !pieceIsOnStraightLine(pieces, targetCol, targetRow)){
                return isLegalMove(pieces, targetCol, targetRow);
            }
            // capture diagonale
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
            hittingP.color != color) {
                return isLegalMove(pieces, targetCol, targetRow);
            }

            //en passant
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue)
            {
                for (Piece piece : pieces)
                {
                    if (piece.col == targetCol && piece.row == preRow && piece.twoStepped)
                    {
                        hittingP = piece;
                        return isLegalMove(pieces, targetCol, targetRow);
                    }
                }
            }
        }
        return false;
    }
    @Override
    public String getName() {
        return "Pion";
    }
}

