package com.example.demo.model.utils;

public class Timer {
    private long    startTimeNano;    // In nanoseconds
    private long    pausedTimeNano;   // In nanoseconds
    private double  durationSeconds;  // In seconds
    private boolean running;          // Timer state
    private boolean paused;           // Pause state

    // Constructor
    public Timer() {
        reset();
    }

    // Start the timer with a sepecified duration in seconds
    public void start(double durationSeconds) {
        this.durationSeconds = durationSeconds;
        this.startTimeNano   = System.nanoTime();
        this.running         = true;
        this.paused          = false;
        this.pausedTimeNano  = 0;
    }

    // Stop the timer
    public void stop() {
        this.running = false;
        this.paused  = false;
    }

    // Pause and resume the timer
    public void pause() {
        if (!running || paused) return;

        this.paused         = true;
        this.pausedTimeNano = System.nanoTime();
    }

    public void resume() {
        if (!running || !paused) return;

        long pausedDurationNano  = System.nanoTime() - pausedTimeNano;

        // Add the paused duration to the start time
        this.startTimeNano      += pausedDurationNano;
        this.paused              = false;
    }

    // Reset the timer
    public void reset() {
        this.running         = false;
        this.paused          = false;
        this.startTimeNano   = 0;
        this.pausedTimeNano  = 0;
        this.durationSeconds = 0.0;
    }

    // Check if the timer is running
    public boolean isRunning() {
        return running;
    }

    // Check if the timer is paused
    public boolean isPaused() {
        return paused;
    }

    // Check if the timer has finished
    public boolean isFinished() {
        if (!running) return false;
        if (getElapsedTime() >= durationSeconds) {
            running = false;
            return true;
        }

        return false;
    }

    // Get the elapsed time in seconds
    public double getElapsedTime() {
        if (!running) return 0.0;
        if (paused) {
            return (pausedTimeNano - startTimeNano) / 1_000_000_000.0;
        }
        return (System.nanoTime() - startTimeNano) / 1_000_000_000.0;
    }

    // Get the total duration in seconds
    public double getDuration() {
        return durationSeconds;
    }
}
