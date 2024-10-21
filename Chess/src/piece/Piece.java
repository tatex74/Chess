package piece;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import Main.Board;
import Main.GamePanel;
import Main.Move;
import Main.Type;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public abstract class Piece {

    public Type  type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved, twoStepped;


    public Piece(int col, int row, int color) {
        this.col = col;
        this.row = row;
        this.color = color;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;

    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    } //

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    // pour que la point qui definie la piece soit au centre de la case
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getIndex(){
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
                return index;
            }
        }
        return 0;
    }

    public void updatePosition() {
        // verifie si on peux faire un En passant ( si le piece avance de  2)
        if (type == Type.PAWN) {
            if (Math.abs(row - preRow) == 2) {
                twoStepped = true;
            }

        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int TargetRow) {
        return false;
    }
    public boolean isWithinBoard(int targetCol,int targetRow)
    {
        if (targetCol >=0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) // si la piece est dans le plateau
        {
            return true;
        }
        return false;
    }

    public boolean isSameSquare (int targetCol, int targetRow) { // pour verifier si la piece est sur la meme case
        if (preCol == targetCol && preRow == targetRow) {
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }

        }

        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){
        hittingP = getHittingP(targetCol, targetRow);
        if (hittingP == null) {
            return true;
        }
        else
        {
            if (hittingP.color != this.color) {
                return true;
            }
            else {
                hittingP = null;
            }
        }
        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) { // pour verifier si une piece est sur la trajectoire de la piece
        // si la piece va à gauche
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va à droite
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en haut
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean  pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        // si la piece va en haut à gauche
        for (int c = preCol - 1, r = preRow - 1; c > targetCol && r > targetRow; c--, r--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en haut à droite
        for (int c = preCol + 1, r = preRow - 1; c < targetCol && r > targetRow; c++, r--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas à gauche
        for (int c = preCol - 1, r = preRow + 1; c > targetCol && r < targetRow; c--, r++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas à droite
        for (int c = preCol + 1, r = preRow + 1; c < targetCol && r < targetRow; c++, r++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }

    public abstract String getName();

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    public void move(int targetCol, int targetRow) {
        // Mettre à jour la position précédente
        preCol = col;
        preRow = row;

        // Mettre à jour la position actuelle
        col = targetCol;
        row = targetRow;

        // Mettre à jour les coordonnées x et y
        x = getX(col);
        y = getY(row);

        // Vérifier s'il y a une pièce à capturer
        Piece capturedPiece = getHittingP(targetCol, targetRow);
        if (capturedPiece != null) {
            GamePanel.simPieces.remove(capturedPiece);
        }

        // Marquer la pièce comme ayant bougé
        moved = true;
    }
    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        // Logique pour ajouter les mouvements possibles du pion
        return moves;
    }
}
