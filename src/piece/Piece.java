package Piece;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import Logic.Game;
import Panel.Board;
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

    /**
     * Creates a copy of the current piece.
     *
     * @param isImage A boolean flag indicating whether the piece should have an image.
     * @return A new instance of the piece with the same properties.
     */
    public Piece copyPiece(boolean isImage) {
        Piece newPiece = switch (this.type) {
            case BISHOP -> new Bishop(this.col, this.row, this.color, isImage);
            case KING -> new King(this.col, this.row, this.color, isImage);
            case KNIGHT -> new Knight(this.col, this.row, this.color, isImage);
            case PAWN -> new Pawn(this.col, this.row, this.color, isImage);
            case QUEEN -> new Queen(this.col, this.row, this.color, isImage);
            case ROOK -> new Rook(this.col, this.row, this.color, isImage);
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

    /**
     * Creates a copy of all pieces in the given list.
     *
     * @param pieces The list of pieces to be copied.
     * @return A new list containing copies of all the pieces.
     */
    public static ArrayList<Piece> copyPieces(ArrayList<Piece> pieces) {
        ArrayList<Piece> piecesCopy = new ArrayList<>();
        for (Piece piece : pieces) {
            piecesCopy.add(piece.copyPiece(false));
        }

        return piecesCopy;
    }

    /**
     * Loads an image from the given path.
     *
     * @param imagePath The path of the image.
     * @return A BufferedImage object representing the image.
     */
    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Calculates the X pixel coordinate for the given column.
     *
     * @param col The column index (0 to 7).
     * @return The X pixel coordinate of the piece on the board.
     */
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    } //

    /**
     * Calculates the Y pixel coordinate for the given row.
     *
     * @param row The row index (0 to 7).
     * @return The Y pixel coordinate of the piece on the board.
     */
    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    /**
     * Gets the column index from the X pixel coordinate.
     *
     * @param x The X pixel coordinate.
     * @return The column index (0 to 7).
     */
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    /**
     * Gets the row index from the Y pixel coordinate.
     *
     * @param y The Y pixel coordinate.
     * @return The row index (0 to 7).
     */
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    /**
     * Gets the index of the current piece in the list of pieces.
     *
     * @param pieces The list of pieces.
     * @return The index of the piece in the list.
     */
    public int getIndex(ArrayList<Piece> pieces) {
        for (int index = 0; index < pieces.size(); index++) {
            if (pieces.get(index) == this) {
                return index;
            }
        }
        return 0;
    }

    /**
     * Updates the position of the piece based on its current and previous position.
     * This method checks for special pawn movement (two-square move).
     */
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

    /**
     * Resets the position of the piece to its previous position.
     */
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }


    /**
     * Checks if the piece can move to a specified target position.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param TargetRow The target row.
     * @param verifyLegal A flag indicating whether to verify if the move is legal.
     * @return True if the piece can move to the target position, false otherwise.
     */
    public boolean canMove(ArrayList<Piece> pieces, int targetCol, int TargetRow, boolean verifyLegal) {
        return false;
    }

    /**
     * Checks if the target position is the same as the current position of the piece.
     *
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return True if the target position is the same as the current position, false otherwise.
     */
    public boolean isSameSquare (int targetCol, int targetRow) { // pour verifier si la piece est sur la meme case
        return preCol == targetCol && preRow == targetRow;
    }

    /**
     * Retrieves the piece at the target position, if any.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return The piece at the target position, or null if there is no piece.
     */
    public Piece getHittingP(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        for (Piece piece : pieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }

        }

        return null;
    }

    /**
     * Retrieves a piece by its coordinates on the board.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return The piece at the specified coordinates, or null if no piece exists at those coordinates.
     */
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

    /**
     * Checks if the specified square is a valid square for movement, considering any pieces on the target square.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return True if the square is valid for movement, false otherwise.
     */
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

    /**
     * Checks if a move is legal by simulating the move and checking if the king is in check.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return True if the move is legal, false otherwise.
     */
    public boolean isLegalMove(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        Piece hittingP = getHittingP(pieces, targetCol, targetRow);
        ArrayList<Piece> simPieces = new ArrayList<>(pieces);

        if (hittingP != null) {
            simPieces.remove(hittingP);
        }

        int preCol = this.preCol;
        int preRow = this.preRow;
        this.preCol = targetCol; this.preRow = targetRow;

        if (Game.isKingInCheck(simPieces, this.color, false)) {
            this.preCol = preCol; this.preRow = preRow;
            return false;
        }

        this.preCol = preCol; this.preRow = preRow;

        return true;
    }

    /**
     * Checks if there is any piece on the same straight line between the current position and the target position.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return True if there is a piece on the straight line, false otherwise.
     */
    public boolean isPieceOnStraightLine(ArrayList<Piece> pieces, int targetCol, int targetRow) { // pour verifier si une piece est sur la trajectoire de la piece
        // si la piece va à gauche
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece piece : pieces) {
                if (piece.preCol == c && piece.preRow == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va à droite
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece piece : pieces) {
                if (piece.preCol == c && piece.preRow == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en haut
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece piece : pieces) {
                if (piece.preCol == targetCol && piece.preRow == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece piece : pieces) {
                if (piece.preCol == targetCol && piece.preRow == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if there is any piece on the same diagonal line between the current position and the target position.
     *
     * @param pieces The list of all pieces on the board.
     * @param targetCol The target column.
     * @param targetRow The target row.
     * @return True if there is a piece on the diagonal line, false otherwise.
     */
    public boolean isPieceOnDiagonalLine(ArrayList<Piece> pieces, int targetCol, int targetRow) {
        // si la piece va en haut à gauche
        for (int c = preCol - 1, r = preRow - 1; c > targetCol && r > targetRow; c--, r--) {
            for (Piece piece : pieces) {
                if (piece.preCol == c && piece.preRow == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en haut à droite
        for (int c = preCol + 1, r = preRow - 1; c < targetCol && r > targetRow; c++, r--) {
            for (Piece piece : pieces) {
                if (piece.preCol == c && piece.preRow == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas à gauche
        for (int c = preCol - 1, r = preRow + 1; c > targetCol && r < targetRow; c--, r++) {
            for (Piece piece : pieces) {
                if (piece.preCol == c && piece.preRow == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // si la piece va en bas à droite
        for (int c = preCol + 1, r = preRow + 1; c < targetCol && r < targetRow; c++, r++) {
            for (Piece piece : pieces) {
                if (piece.preCol == c && piece.preRow == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Abstract method to get the name of the piece.
     *
     * @return The name of the piece (e.g., "King", "Queen", "Pawn", etc.).
     */
    public abstract String getName();

    /**
     * Draws the piece on the board using the provided Graphics2D object.
     *
     * @param g2 The Graphics2D object used for drawing.
     */
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    /**
     * Converts the piece to a JSON object for serialization.
     *
     * @return A JSONObject representing the piece.
     */
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

    /**
     * Creates a piece object from a given JSON object.
     *
     * @param jsonObject The JSON object containing the piece's data.
     * @return The piece created from the JSON data.
     */
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
                piece = new Bishop(col, row, color, true);
                break;
            case "KING":
                piece = new King(col, row, color, true);
                break;
            case "KNIGHT":
                piece = new Knight(col, row, color, true);
                break;
            case "PAWN":
                piece = new Pawn(col, row, color, true);
                break;
            case "QUEEN":
                piece = new Queen(col, row, color, true);
                break;
            case "ROOK":
                piece = new Rook(col, row, color, true);
                break;
            default:
                return null;
        }

        piece.preCol = preCol;
        piece.preRow = preRow;

        return piece;
    }
}
