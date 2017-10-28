package com.loanlassalle.enigma.client.models;

import javafx.scene.control.ProgressBar;

/**
 * DisplayTimer to display minutes and seconds in the form mm:ss
 *
 * @author Tano Iannetta
 */
public class DisplayTimer {

    private int seconds;
    private int secondsMax;
    private int realSecond;
    private int minutes;
    private double gap;

    /**
     * Create timer and initilize the gap of each second for the process bar
     *
     * @param secondsMax seconds maximum for the timer, used to know when the pgress bar
     *                   must be full
     */
    public DisplayTimer(int secondsMax) {
        this.secondsMax = secondsMax;
        gap = 1L / (double) secondsMax;
    }

    /**
     * Used each second to increment timer
     *
     * @param progressBar the pgress bar of the timer
     */
    public synchronized void incrementSecond(ProgressBar progressBar) {
        seconds++;
        realSecond++;
        progressBar.setProgress(progressBar.getProgress() + gap); // increment progress bar

        if (seconds % 60 == 0) { // new minute
            incrementMinute();
        } else if (realSecond == secondsMax + 1) { // time max
            reset(progressBar);
        }
    }

    /**
     * Used each minute to increment the minutes for the timer
     */
    private void incrementMinute() {
        minutes++;
        if (minutes % 60 == 0) { // loop minutes
            minutes = 0;
        }
    }

    /**
     * Reset timer and progress bar
     *
     * @param progressBar progress bar of the timer
     */
    public synchronized void reset(ProgressBar progressBar) {
        seconds = 0;
        realSecond = 0;
        minutes = 0;
        progressBar.setProgress(0L); // reset progress bar
    }

    /**
     * Display timer in format mm:ss
     *
     * @return format mm:ss
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d", minutes, seconds);
    }
}
