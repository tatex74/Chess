package Piece;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import Main.Board;
import Main.Type;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.imageio.ImageIO;

public abstract class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public boolean moved, twoStepped;

    public Piece hittingP;


    public Piece(int col, int row, int color) {
        this.col = col;
        this.row = row;
        this.preCol = col;
        this.preRow = row;
        this.color = color;
        this.x = getX(col);
        this.y = getY(row);
    }

    public Piece copyPiece() {
        Piece newPiece = switch (this.type) {
            case BISHOP -> new Bishop(this.col, this.row, this.color);
            case KING -> new King(this.col, this.row, this.color);
            case KNIGHT -> new Knight(this.col, this.row, this.color);
            case PAWN -> new Pawn(this.col, this.row, this.color);
            case QUEEN -> new Queen(this.col, this.row, this.color);
            case ROOK -> new Rook(this.col, this.row, this.color);
        };

        newPiece.x = this.x;
        newPiece.y = this.y;
        newPiece.preCol = this.preCol;
        newPiece.preRow = this.preRow;
        newPiece.color = this.color;
        newPiece.moved = this.moved;
        newPiece.twoStepped = this.twoStepped;

        return newPiece;
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
    public int getIndex(ArrayList<Piece> pieces) {
        for (int index = 0; index < pieces.size(); index++) {
            if (pieces.get(index) == this) {
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

    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int TargetRow) {
        return false;
    }

    public boolean isSameSquare (int targetCol, int targetRow) { // pour verifier si la piece est sur la meme case
        return preCol == targetCol && preRow == targetRow;
    }

    public Piece getHittingP(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        for (Piece piece : pieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }

        }

        return null;
    }

    public static Piece getPieceByCoord(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        if(Board.isWithinBoard(targetCol, targetRow)) {
            for (Piece piece : pieces) {
                if (piece.col == targetCol && piece.row == targetRow) {
                    return piece;
                }
            }
        }

        return null;
    }

    public boolean isValidSquare(ArrayList<Piece> pieces, int targetCol, int targetRow){
        hittingP = getHittingP(pieces, targetCol, targetRow);
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

    public boolean isLegalMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        Piece hittingP = getHittingP(pieces, targetCol, targetRow);
        ArrayList<Piece> simPieces = new ArrayList<>(pieces);

        if (hittingP != null) {
            simPieces.remove(hittingP);
        }

        int preCol = this.preCol;
        int preRow = this.preRow;
        this.preCol = targetCol;
        this.preRow = targetRow;

        if (Move.isKingInCheck(simPieces, this.color)) {
            this.preCol = preCol; this.preRow = preRow;
            return false;
        }

        this.preCol = preCol; this.preRow = preRow;

        return true;
    }

    public boolean pieceIsOnStraightLine(ArrayList<Piece> pieces, int targetCol, int targetRow) { // pour verifier si une piece est sur la trajectoire de la piece
        // si la piece va à gauche
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece piece : pieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va à droite
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece piece : pieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en haut
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece piece : pieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece piece : pieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean pieceIsOnDiagonalLine(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        // si la piece va en haut à gauche
        for (int c = preCol - 1, r = preRow - 1; c > targetCol && r > targetRow; c--, r--) {
            for (Piece piece : pieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en haut à droite
        for (int c = preCol + 1, r = preRow - 1; c < targetCol && r > targetRow; c++, r--) {
            for (Piece piece : pieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas à gauche
        for (int c = preCol - 1, r = preRow + 1; c > targetCol && r < targetRow; c--, r++) {
            for (Piece piece : pieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas à droite
        for (int c = preCol + 1, r = preRow + 1; c < targetCol && r < targetRow; c++, r++) {
            for (Piece piece : pieces) {
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

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("col", col);
        jsonObject.put("row", row);
        jsonObject.put("preCol", preCol);
        jsonObject.put("preRow", preRow);
        jsonObject.put("color", color);
        return jsonObject;
    }

    public static Piece fromJson(JSONObject jsonObject) {

        String strType = jsonObject.getString("type");
        int col = jsonObject.getInt("col");
        int row = jsonObject.getInt("row");
        int preCol = jsonObject.getInt("preCol");
        int preRow = jsonObject.getInt("preRow");
        int color = jsonObject.getInt("color");

        Piece piece;

        switch (strType) {
            case "BISHOP":
                piece = new Bishop(col, row, color);
                break;
            case "KING":
                piece = new King(col, row, color);
                break;
            case "KNIGHT":
                piece = new Knight(col, row, color);
                break;
            case "PAWN":
                piece = new Pawn(col, row, color);
                break;
            case "QUEEN":
                piece = new Queen(col, row, color);
                break;
            case "ROOK":
                piece = new Rook(col, row, color);
                break;
            default:
                return null;
        }

        piece.preCol = preCol;
        piece.preRow = preRow;

        return piece;
    }
}
