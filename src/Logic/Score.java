package Logic;

import Piece.Piece;
import Panel.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class Score {
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

    public static void paintScore(Graphics2D g2, Game game) {
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        int[] scores = evaluateBoard(game.pieces);
        g2.drawString("Score Blanc : " + scores[0], 1200, 180);
        g2.drawString("Score Noir : " + scores[1], 1200, 240);
    }

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
