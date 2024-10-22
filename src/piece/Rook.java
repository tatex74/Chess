package piece;
import Main.GamePanel;
import Main.Type;
import Main.Move;
import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(int col, int row, int color) {

        super(col, row, color);

        type = Type.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-rook");
        } else {
            image = getImage("/piece/b-rook");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol,targetRow) == false) {
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    return true;
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