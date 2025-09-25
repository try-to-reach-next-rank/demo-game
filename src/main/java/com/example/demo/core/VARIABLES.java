package com.example.demo.core;

public interface VARIABLES {
    public static final int N_OF_BRICKS = 30;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;
    public static final int BOTTOM_EDGE = HEIGHT;

    public static final int INIT_PADDLE_X = WIDTH / 2 - 50;
    public static final int INIT_PADDLE_Y = HEIGHT - 30;

    public static final int INIT_BALL_X = WIDTH / 2 - 10;
    public static final int INIT_BALL_Y = HEIGHT - 60;
    public static final float SPEED = 0.4F;
    public static final float ACCELERATED_SPEED_MULTIPLIER = 2.0F;
}