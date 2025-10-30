package com.example.demo.model.utils.var;
import static com.example.demo.model.utils.var.GlobalVar.HEIGHT;
import static com.example.demo.model.utils.var.GlobalVar.WIDTH;

public final class GameVar {
    public static final int START_LEVEL = 1;
    public static final int MAXPOWER    = 5;
    public static final int MINPOWER    = 1;

    private GameVar() {}

    public static final int INIT_PADDLE_X = WIDTH / 2 - 50;
    public static final int INIT_PADDLE_Y = HEIGHT - 30;

    public static final int INIT_BALL_X                    = WIDTH / 2 - 10;
    public static final int INIT_BALL_Y                    = HEIGHT - 60;
    public static final float SPEED                        = 0.4F;
    public static final float ACCELERATED_SPEED_MULTIPLIER = 2.0F;

    public static final int WIDTH_OF_WALLS        = 12;
    public static final int HEIGHT_OF_WALLS       = 200;
    public static final int N_OF_WALLS_LEFT_RIGHT = HEIGHT / HEIGHT_OF_WALLS;
    public static final int N_OF_WALLS_TOP        = WIDTH / HEIGHT_OF_WALLS;

    public static final int BRICKS_PER_ROW = 20;
    public static final int PADDING_X      = 0;
    public static final int PADDING_Y      = 0;

    public static final int MAXHEALTH_OF_BRICKS = 5;
    public static final int WIDTH_OF_BRICKS     = 30;
    public static final int HEIGHT_OF_BRICKS    = 16;

    public static final String ACCELERATE   = "ACCELERATE";
    public static final String STRONGER     = "STRONGER";
    public static final String BIGGERPADDLE = "BIGGERPADDLE";
    public static final String STOPTIME     = "STOPTIME";
    public static final String[] powerUps   = {ACCELERATE, STRONGER, STOPTIME, BIGGERPADDLE};

    public static final long PADDLE_SOUND_COOLDOWN = 150;  // ms

    public static final float BASE_SPEED_BALL   = 300F;
    public static final float BASE_SPEED_PADDLE = 400F;

    private final static int CELL_W = GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X;
    private final static int CELL_H = GameVar.HEIGHT_OF_BRICKS + GameVar.PADDING_Y;

    // Compute the usable width/height inside walls
    private final static int USABLE_WIDTH  = WIDTH - 2 * GameVar.WIDTH_OF_WALLS;
    private final static int USABLE_HEIGHT = HEIGHT - GameVar.WIDTH_OF_WALLS;

    // Number of columns and rows that actually fit inside
    public final static int MATRIX_COLS = USABLE_WIDTH / CELL_W;
    public final static int MATRIX_ROWS = (USABLE_HEIGHT / 2) / CELL_H;

    // Brick grid starts just *inside* top and left walls
    public final static int MATRIX_START_X = WIDTH_OF_WALLS + (USABLE_WIDTH - MATRIX_COLS * (WIDTH_OF_BRICKS + PADDING_X))/2 + WIDTH_OF_BRICKS/2;
    public final static int MATRIX_START_Y = MATRIX_START_X;

    // ANIMATION ASSETS
    public static final String EXPLOSION_SHEET_KEY   = "explosion_spritesheet";
    public static final int EXPLOSION1_FRAME_WIDTH   = 96;
    public static final int EXPLOSION1_FRAME_HEIGHT  = 96;
    public static final int EXPLOSION1_TOTAL_FRAMES  = 24;
    public static final int EXPLOSION1_ROW           = 0;
    public static final int EXPLOSION1_RENDER_WIDTH  = 64;
    public static final int EXPLOSION1_RENDER_HEIGHT = 64;
    public static final boolean EXPLOSION1_LOOP      = false;
    public static final double EXPLOSION1_DURATION   = 1.0;

    public static final int EXPLOSION2_FRAME_WIDTH   = 96;
    public static final int EXPLOSION2_FRAME_HEIGHT  = 96;
    public static final int EXPLOSION2_TOTAL_FRAMES  = 32;
    public static final int EXPLOSION2_ROW           = 1;
    public static final int EXPLOSION2_RENDER_WIDTH  = 32;
    public static final int EXPLOSION2_RENDER_HEIGHT = 32;
    public static final boolean EXPLOSION2_LOOP      = false;
    public static final double EXPLOSION2_DURATION   = 1.0;

    public static final String POWERUP_SHEET_KEY = "powerup_spritesheet";
    public static final int POWERUP_FRAME_WIDTH  = 32;
    public static final int POWERUP_FRAME_HEIGHT = 16;
    public static final int POWERUP_TOTAL_FRAMES = 8;

    public static final int POWERUP_ACCELERATE_ROW   = 0;
    public static final int POWERUP_STRONGER_ROW     = 1;
    public static final int POWERUP_STOPTIME_ROW     = 2;
    public static final int POWERUP_BIGGERPADDLE_ROW = 3;

    public static final int POWERUP_RENDER_WIDTH  = 32;
    public static final int POWERUP_RENDER_HEIGHT = 16;

    public static final boolean POWERUP_LOOP = true;

    public static final double POWERUP_ACCELERATE_DURATION   = 5.0;
    public static final double POWERUP_STRONGER_DURATION     = 2.0;
    public static final double POWERUP_STOPTIME_DURATION     = 10.0;
    public static final double POWERUP_BIGGERPADDLE_DURATION = 1.0;


}