package piece;
import Main.GamePanel;
import Main.Type;

public class Queen extends Piece {

    public Queen(int col, int row, int color) {
        super(col, row, color);

        type = Type.QUEEN;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-queen");
        } else {
            image = getImage("/piece/b-queen");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol,targetRow) == false) {
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}