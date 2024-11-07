package Piece;
import Logic.Game;
import Panel.Board;

import java.util.ArrayList;

public class Queen extends Piece {

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
    @Override
    public String getName() {
        return "Reine";
    }

}