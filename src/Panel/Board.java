package Panel;

import java.awt.*;

public class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE=100; // taille du carré du plateau
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    /**
     * Draws the chessboard on the panel. This method creates an 8x8 grid with alternating colors
     * for the squares and displays coordinates (letters a-h and numbers 1-8) around the board.
     *
     * @param g2 The Graphics2D object used to render the board.
     */
    public void draw(Graphics2D g2)
    {
        int c = 0; //changer couleur
        for (int row = 0;row< MAX_ROW; row++)
        {
            for (int col=0;col<MAX_COL;col++)
            {
                if (c == 0)
                {
                    g2.setColor(new Color(204, 183, 174, 255));
                    c=1;
                }
                else {
                    g2.setColor(new Color(112, 102, 119));
                    c=0;
                }
                g2.fillRect(col*SQUARE_SIZE,row*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);//defini les carré
            }
            if (c==0){
                c=1;
            }
            else
            {
                c=0;
            }
        }

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.WHITE);

        // Dessiner les lettres (a-h) en haut et en bas du plateau
        for (int col = 0; col < 8; col++) {
            char letter = (char) ('a' + col);
            g2.drawString(String.valueOf(letter), col * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE, 815);

        }

        // Dessiner les numéros (1-8) à gauche et à droite du plateau
        for (int row = 0; row < 8; row++) {
            int number = 8 - row;
            g2.drawString(String.valueOf(number), 810, row * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE);
        }
    }

    /**
     * Checks if the specified board position is within the bounds of the 8x8 chessboard.
     *
     * @param targetCol The column index to check.
     * @param targetRow The row index to check.
     * @return true if the position is within the board limits, false otherwise.
     */
    public static boolean isWithinBoard(int targetCol,int targetRow)
    {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

}
