package Piece;
import Main.Board;
import Main.GamePanel;
import Main.Type;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(int col, int row, int color) {
        super(col, row, color);

        type = Type.KNIGHT;

        if (color == GamePanel.WHITE) {
            image = getImage("/Piece/w-knight");
        } else {
            image = getImage("/Piece/b-knight");
        }
    }
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            //calcul de la distance entre la case de départ et la case d'arrivée du chevalier
            if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
                if (isValidSquare(pieces, targetCol, targetRow)) {
                    return isLegalMove(pieces, targetCol, targetRow);
                }
            }
        }
        return false;
    }
    @Override
    public String getName() {
        return "Cavalier";
    }
}