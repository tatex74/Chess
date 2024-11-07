package Piece;
import Logic.Game;
import Panel.Board;
import Panel.GamePanel;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(int col, int row, int color, boolean isImage) {
        super(col, row, color);

        type = Type.KNIGHT;

        if (isImage) {
            if (color == Game.WHITE) {
                image = getImage("/Piece/w-knight");
            } else {
                image = getImage("/Piece/b-knight");
            }
        }
    }
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int targetRow, boolean verifyLegal) {
        if (Board.isWithinBoard(targetCol, targetRow) ) {
            //calcul de la distance entre la case de départ et la case d'arrivée du chevalier
            if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
                if (isValidSquare(pieces, targetCol, targetRow)) {
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
        return "Cavalier";
    }
}