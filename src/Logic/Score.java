package Logic;

import Piece.Piece;
import Panel.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class Score {

    /**
     * Evaluates the current score of the game by calculating the value of each piece on the board.
     * The score is calculated based on a standard point system for each type of piece.
     *
     * @param pieces The list of pieces currently on the board.
     * @return An array of two integers: the first is the score for the white player, the second is for the black player.
     */
    public static int[] evaluateBoard(ArrayList<Piece> pieces) {
        int whiteScore = 1039; //Score
        int blackScore = 1039;
        for (Piece piece : pieces) {
            if (piece.color == Game.WHITE) {
                blackScore += getPieceValue(piece);
            } else {
                whiteScore += getPieceValue(piece);
            }
        }
        return new int[]{whiteScore, blackScore};
    }

    /**
     * Gets the point value of a given piece based on its type.
     * Standard chess piece values are used:
     * - Pawn: 1 point
     * - Knight/Bishop: 3 points
     * - Rook: 5 points
     * - Queen: 9 points
     * - King: Invaluable, but represented by 1000 points for game logic purposes
     *
     * @param piece The chess piece for which to get the value.
     * @return The point value of the piece.
     */
    private static int getPieceValue(Piece piece) {
        return switch (piece.type) {
            case PAWN -> -1;
            case KNIGHT, BISHOP -> -3;
            case ROOK -> -5;
            case QUEEN -> -9;
            case KING -> -1000;
            default -> 0;
        };
    }

    /**
     * Paints the current score of both players on the game panel.
     * This method is used to display the current score based on the pieces on the board.
     *
     * @param g2 The Graphics2D object used to draw the score on the panel.
     * @param game The current game instance.
     */
    public static void paintScore(Graphics2D g2, Game game) {
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        int[] scores = evaluateBoard(game.pieces);
        g2.drawString("Score Blanc : " + scores[0], 1200, 180);
        g2.drawString("Score Noir : " + scores[1], 1200, 240);
    }

    /**
     * Paints a legend explaining the point system for chess pieces.
     * This is displayed on the game panel for reference.
     *
     * @param g2 The Graphics2D object used to draw the point system.
     */
    public static void paintScoreSystem(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);
        g2.drawString("Système de points :", 1200, 300);
        g2.drawString("Pion : 1 point", 1200, 320);
        g2.drawString("Cavalier : 3 points", 1200, 340);
        g2.drawString("Fou : 3 points", 1200, 360);
        g2.drawString("Tour : 5 points", 1200, 380);
        g2.drawString("Dame : 9 points", 1200, 400);
        g2.drawString("Roi : Inestimable", 1200, 420);
    }
}
