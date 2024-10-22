package Main;

public class Move {
    public int sourceCol;
    public int sourceRow;
    public int targetCol;
    public int targetRow;

    public Move(int sourceCol, int sourceRow, int targetCol, int targetRow) {
        this.sourceCol = sourceCol;
        this.sourceRow = sourceRow;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }
}
