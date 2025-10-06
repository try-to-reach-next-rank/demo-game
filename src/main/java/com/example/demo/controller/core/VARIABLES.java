package com.example.demo.controller.core;

public final class VARIABLES {
    // Private constructor to prevent initialization
    private VARIABLES() {}


    public static final int N_OF_BRICKS = 150;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;
    public static final int BOTTOM_EDGE = HEIGHT;

    public static final int INIT_PADDLE_X = WIDTH / 2 - 50;
    public static final int INIT_PADDLE_Y = HEIGHT - 30;

    public static final int INIT_BALL_X = WIDTH / 2 - 10;
    public static final int INIT_BALL_Y = HEIGHT - 60;
    public static final float SPEED = 0.4F;
    public static final float ACCELERATED_SPEED_MULTIPLIER = 2.0F;

    public static final int WIDTH_OF_WALLS = 15;
    public static final int HEIGHT_OF_WALLS = 16;
    public static final int N_OF_WALLS_LEFT_RIGHT = HEIGHT / HEIGHT_OF_WALLS;
    public static final int N_OF_WALLS_TOP = WIDTH / WIDTH_OF_WALLS;

    public static final int BRICKS_PER_ROW = 15;
    public static final int PADDING_X = 6;
    public static final int PADDING_Y = 6;

    public static final int MAXHEALTH_OF_BRICKS = 5;
    public static final int WIDTH_OF_BRICKS = 32;
    public static final int HEIGHT_OF_BRICKS = 16;
    public static final int FIRST_X_OF_BRICKS = WIDTH_OF_WALLS + PADDING_X/2;
    public static final int FIRST_Y_OF_BRICKS = HEIGHT_OF_WALLS + PADDING_Y/2;

    public static final double FPS = 60.0;
}