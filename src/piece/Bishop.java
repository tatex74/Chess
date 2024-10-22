package piece;
import Main.GamePanel;
import Main.Type;
import Main.Move;
import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(int col, int row, int color) {
        super(col, row, color);

        type = Type.BISHOP;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-bishop");
        } else {
            image = getImage("/piece/b-bishop");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) ) {
            if (targetCol != preCol || targetRow != preRow) { // Vérifie que la case cible est différente de la case actuelle
                if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                    if (isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)) {
                        return true;
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