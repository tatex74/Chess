package piece;
import Main.GamePanel;
import Main.Type;


public class Pawn extends Piece {

    public Pawn(int col, int row, int color) {
        super(col, row, color);

        type = Type.PAWN;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-pawn");
        } else {
            image = getImage("/piece/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol,targetRow) == false) {
            //definnir le mouvement selon la couleur
            int moveValue;
            if (color == GamePanel.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }
            //on regarde où il peut attaque
            hittingP = getHittingP(targetCol, targetRow);

            //mouvement d'un carré
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                    return true;
            }
            //mouvement de deux carrés
            if(targetCol == preCol && targetRow == preRow + 2*moveValue && hittingP == null && moved == false
            && pieceIsOnStraightLine(targetCol,targetRow)==false){
                return true;
            }
            // capture diagonale
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
            hittingP.color != color) {
                return true;
            }

            //en passant
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue)
            {
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.col == targetCol && piece.row == preRow && piece.twoStepped == true)
                    {
                        hittingP = piece;
                        return true;
                    }
                }
            }



        }
        return false;
    }
    @Override
    public String getName() {
        return "Pion";
    }
}

