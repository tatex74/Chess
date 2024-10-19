package piece;
import Main.GamePanel;
import Main.Type;

public class Knight extends Piece {

    public Knight(int col, int row, int color) {
        super(col, row, color);

        type = Type.KNIGHT;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-knight");
        } else {
            image = getImage("/piece/b-knight");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) ) {
            //calcul de la distance entre la case de départ et la case d'arrivée du chevalier
            if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}