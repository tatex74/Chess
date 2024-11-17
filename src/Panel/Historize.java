package Panel;

import java.util.ArrayList;
import Piece.Move;
import Piece.Piece;

public class Historize extends ArrayList<String> {

    public void recordMove(Piece piece, int oldCol, int oldRow, int newCol, int newRow) {
        char oldChessCol = Move.colToChessCol(oldCol);
        char newChessCol = Move.colToChessCol(newCol);
        char oldChessRow = Move.rowToChessRow(oldRow);
        char newChessRow = Move.rowToChessRow(newRow);

        this.add(piece.getName() + " de (" + oldChessCol + ", " + oldChessRow + ") à (" + newChessCol + ", " + newChessRow + ")   ");

    }

    public String toString() {
        StringBuilder strHistorize = new StringBuilder();
        boolean lineBreak = false;

        for (String move : this) {
            if (lineBreak) {
                strHistorize.append(move).append("\n");
                lineBreak = false;
            } else {
                strHistorize.append(move);
                lineBreak = true;
            }
        }

        return strHistorize.toString();
    }
}
