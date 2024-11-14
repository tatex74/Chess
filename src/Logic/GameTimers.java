package Logic;

import Panel.GamePanel;

import javax.swing.Timer;
import java.awt.*;


public class GameTimers {
    private int whiteTimeRemaining;
    private int blackTimeRemaining;
    private Timer whiteTimer;
    private Timer blackTimer;

    /**
     * Constructor that initializes the timers for both players with a given duration.
     *
     * @param duration The duration (in seconds) for each player's timer at the start of the game.
     */
    public GameTimers(int duration) {
        whiteTimeRemaining = duration;
        blackTimeRemaining = duration;

        whiteTimer = new Timer(1000, _ -> {
            if (whiteTimeRemaining > 0) {
                whiteTimeRemaining--;
            }
            else {
                whiteTimer.stop();
            }});

        blackTimer = new Timer(1000, _ -> {
            if (blackTimeRemaining > 0) {
                blackTimeRemaining--;
            }
            else {
                blackTimer.stop();
            }});
    }

    /**
     * Stops both the white and black timers.
     */
    public void stopTimers() {
        stopWhiteTimer();
        stopBlackTimer();
    }

    /**
     * Starts the timer for the current player and stops the timer for the opponent.
     *
     * @param currentColor The color of the current player (WHITE or BLACK).
     */
    public void invertTimer(int currentColor) {
        if (currentColor == Game.WHITE)  {
            startWhiteTimer();
            stopBlackTimer();
        }
        else if (currentColor == Game.BLACK){
            stopWhiteTimer();
            startBlackTimer();
        }

    }

    /**
     * Starts the white player's timer if there is remaining time.
     */
    public void startWhiteTimer() {
        if (whiteTimeRemaining > 0 ) {
            whiteTimer.start();
        }
    }

    /**
     * Stops the white player's timer.
     */
    public void stopWhiteTimer() {
        whiteTimer.stop();
    }

    /**
     * Starts the black player's timer if there is remaining time.
     */
    public void startBlackTimer() {
        if (blackTimeRemaining > 0) {
            blackTimer.start();
        }
    }

    /**
     * Stops the black player's timer.
     */
    public void stopBlackTimer() {
        blackTimer.stop();
    }

    /**
     * Resets both timers to the specified duration.
     *
     * @param duration The duration (in seconds) to reset both timers to.
     */
    public void resetTimers(int duration) {
        stopWhiteTimer();
        stopBlackTimer();
        whiteTimeRemaining = duration;
        blackTimeRemaining = duration;
    }

    /**
     * Gets the remaining time (in seconds) for the white player's timer.
     *
     * @return The remaining time for the white player, in seconds.
     */
    public int getWhiteTimeRemaining() {
        return whiteTimeRemaining;
    }

    /**
     * Gets the remaining time (in seconds) for the black player's timer.
     *
     * @return The remaining time for the black player, in seconds.
     */
    public int getBlackTimeRemaining() {
        return blackTimeRemaining;
    }

    /**
     * Sets the remaining time for the white player's timer.
     *
     * @param timeRemaining The new remaining time (in seconds) for the white player.
     */
    public void setWhiteTimeRemaining(int timeRemaining) {
        if (timeRemaining >= 0) {
            whiteTimeRemaining = timeRemaining;
        }
    }

    /**
     * Sets the remaining time for the black player's timer.
     *
     * @param timeRemaining The new remaining time (in seconds) for the black player.
     */
    public void setBlackTimeRemaining(int timeRemaining) {
        if (timeRemaining >= 0) {
            blackTimeRemaining = timeRemaining;
        }
    }

    /**
     * Converts the white player's remaining time to a formatted string (mm:ss).
     *
     * @return A string representation of the white player's remaining time in the format "mm:ss".
     */
    public String whiteTimerToString() {
        int minutes = whiteTimeRemaining / 60;
        int seconds = whiteTimeRemaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Converts the black player's remaining time to a formatted string (mm:ss).
     *
     * @return A string representation of the black player's remaining time in the format "mm:ss".
     */
    public String blackTimerToString() {
        int minutes = blackTimeRemaining / 60;
        int seconds = blackTimeRemaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Paints the remaining time for both players on the game panel.
     *
     * @param g2 The Graphics2D object used to draw the timers on the screen.
     */
    public void paintTimers(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        g2.drawString("Temps restant Blanc : " + whiteTimerToString(), 1200, 60);
        g2.drawString("Temps restant Noir : " + blackTimerToString(), 1200, 120);
    }
}
