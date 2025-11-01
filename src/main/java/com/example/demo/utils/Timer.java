package com.example.demo.utils;

public class Timer {
    private double duration;
    private double elapsed;
    private boolean running;

    public Timer() {
        reset();
    }

    public void start(double durationSeconds) {
        this.duration = durationSeconds;
        this.elapsed = 0;
        this.running = true;
    }

    public void update(double deltaTime) {
        if (!running) return;
        elapsed += deltaTime;
        if (elapsed >= duration) {
            running = false;
        }
    }

    public boolean isFinished() {
        return !running;
    }

    public void reset() {
        running = false;
        elapsed = 0;
        duration = 0;
    }
}
