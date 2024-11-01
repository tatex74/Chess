package Piece;
import Main.Board;
import Main.GamePanel;
import Main.Type;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(int col, int row, int color) {
        super(col, row, color);

        type = Type.QUEEN;

        if (color == GamePanel.WHITE) {
            image = getImage("/Piece/w-queen");
        } else {
            image = getImage("/Piece/b-queen");
        }
    }
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if (Board.isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(pieces, targetCol, targetRow) && !pieceIsOnStraightLine(pieces, targetCol, targetRow)) {
                    return isLegalMove(pieces, targetCol, targetRow);
                }
            }
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(pieces, targetCol, targetRow) && !pieceIsOnDiagonalLine(pieces, targetCol, targetRow)) {
                    return isLegalMove(pieces, targetCol, targetRow);
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