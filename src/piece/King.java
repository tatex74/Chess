package Piece;
import Main.Board;
import Main.GamePanel;
import Main.Type;

import java.util.ArrayList;

public class King extends Piece {

    public King(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.KING;

        if (isImage) {
            if (color == GamePanel.WHITE) {
                image = getImage("/Piece/w-king");
            } else {
                image = getImage("/Piece/b-king");
            }
        }
    }

    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || //se deplace en carré
                    Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1)  //se deplace en diagonale
            {
                if (isValidSquare(pieces, targetCol, targetRow)) {
                    return isLegalMove(pieces, targetCol, targetRow);
                }
            }

            // Castling
            if(!this.moved && targetRow == preRow &&
                    !pieceIsOnStraightLine(pieces, targetCol, targetRow) &&
                    (targetCol == preCol + 2 || targetCol == preCol - 2)) {
                Piece rook;
                if (targetCol == preCol + 2) {
                    rook = getHittingP(pieces, 7, targetRow);
                }
                else  {
                    rook = getHittingP(pieces, 0, targetRow);
                }
                if (rook != null && !rook.moved) {
                    if (!Move.isKingInCheck(pieces, this.color)) {
                        return isLegalMove(pieces, preCol + (targetCol-this.preCol)/2, targetRow) &&
                                isLegalMove(pieces, targetCol, targetRow);
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "Roi";
    }

}