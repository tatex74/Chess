package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;

public class King extends Piece {

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

    @Override
    public String getName() {
        return "Roi";
    }

}