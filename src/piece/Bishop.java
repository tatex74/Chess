package Piece;
import Main.Board;
import Main.GamePanel;
import Main.Type;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.BISHOP;

        if (isImage) {
            if (color == GamePanel.WHITE) {
                image = getImage("/Piece/w-bishop");
            } else {
                image = getImage("/Piece/b-bishop");
            }
        }
    }
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            if (targetCol != preCol || targetRow != preRow) { // Vérifie que la case cible est différente de la case actuelle
                if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                    if (isValidSquare(pieces, targetCol, targetRow) && !pieceIsOnDiagonalLine(pieces, targetCol, targetRow)) {
                        return isLegalMove(pieces, targetCol, targetRow);
                    }
                }
            }
        }
        return false;
    }
    @Override
    public String getName() {
        return "Fou";
    }
}