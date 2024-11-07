package Logic;

import Panel.GamePanel;

import javax.swing.Timer;
import java.awt.*;


public class GameTimers {
    private int whiteTimeRemaining;
    private int blackTimeRemaining;
    private Timer whiteTimer;
    private Timer blackTimer;

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

    public void stopTimers() {
        stopWhiteTimer();
        stopBlackTimer();
    }

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

    public void startWhiteTimer() {
        if (whiteTimeRemaining > 0 ) {
            whiteTimer.start();
        }
    }

    public void stopWhiteTimer() {
        whiteTimer.stop();
    }


    public void startBlackTimer() {
        if (blackTimeRemaining > 0) {
            blackTimer.start();
        }
    }

    public void stopBlackTimer() {
        blackTimer.stop();
    }

    public void resetTimers(int duration) {
        stopWhiteTimer();
        stopBlackTimer();
        whiteTimeRemaining = duration;
        blackTimeRemaining = duration;
    }

    public int getWhiteTimeRemaining() {
        return whiteTimeRemaining;
    }

    public int getBlackTimeRemaining() {
        return blackTimeRemaining;
    }

    public void setWhiteTimeRemaining(int timeRemaining) {
        if (timeRemaining >= 0) {
            whiteTimeRemaining = timeRemaining;
        }
    }

    public void setBlackTimeRemaining(int timeRemaining) {
        if (timeRemaining >= 0) {
            blackTimeRemaining = timeRemaining;
        }
    }

    public String whiteTimerToString() {
        int minutes = whiteTimeRemaining / 60;
        int seconds = whiteTimeRemaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String blackTimerToString() {
        int minutes = blackTimeRemaining / 60;
        int seconds = blackTimeRemaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void paintTimers(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        g2.drawString("Temps restant Blanc : " + whiteTimerToString(), 1200, 60);
        g2.drawString("Temps restant Noir : " + blackTimerToString(), 1200, 120);
    }
}
