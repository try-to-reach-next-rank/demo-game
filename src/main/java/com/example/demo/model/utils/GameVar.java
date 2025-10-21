package com.example.demo.model.utils;
import static com.example.demo.model.utils.GlobalVar.*;

public class GameVar {
    private GameVar() {}

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

    public static final int BRICKS_PER_ROW = 20;
    public static final int PADDING_X = 6;
    public static final int PADDING_Y = 6;

    public static final int MAXHEALTH_OF_BRICKS = 5;
    public static final int WIDTH_OF_BRICKS = 23;
    public static final int HEIGHT_OF_BRICKS = 12;

    public static final String ACCELERATE = "ACCELERATE";
    public static final String STRONGER = "STRONGER";
    public static final String BIGGERPADDLE = "BIGGERPADDLE";
    public static final String STOPTIME = "STOPTIME";
    public static final String[] powerUps = {ACCELERATE, STRONGER, STOPTIME, BIGGERPADDLE};

    public static final long PADDLE_SOUND_COOLDOWN = 150; // ms

    public static final float BASE_SPEED_BALL= 300F;
    public static final float BASE_SPEED_PADDLE = 400F;
}