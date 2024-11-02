package Piece;
import Main.Board;
import Main.GamePanel;
import Main.Type;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(int col, int row, int color, boolean isImage) {

        super(col, row, color);

        type = Type.ROOK;

        if (isImage) {
            if (color == GamePanel.WHITE) {
                image = getImage("/Piece/w-rook");
            } else {
                image = getImage("/Piece/b-rook");
            }
        }
    }
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if (Board.isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(pieces, targetCol, targetRow) && !pieceIsOnStraightLine(pieces, targetCol, targetRow)) {
                    return isLegalMove(pieces, targetCol, targetRow);
                }
            }
        }
        return false;
    }
    @Override
    public String getName() {
        return "Tour";
    }


}