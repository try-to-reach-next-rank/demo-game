package com.example.demo.model.utils.var;
import javafx.scene.paint.Color;

import static com.example.demo.model.utils.var.GlobalVar.HEIGHT;
import static com.example.demo.model.utils.var.GlobalVar.WIDTH;

public final class GameVar {

    private GameVar() {}
    // GAME
    public static final int START_LEVEL = 1;
    public static final int MAX_LEVEL = 3;
    public static final int MIN_LEVEL = 1;
    public static final double MAX_DELTA_TIME = 0.05;
    public static final double TRANSITION_DURATION = 2.0;

    // THEME
    public static final int DEFAULT_BG_FRAMES = 6;

    // PARALLAX
    public static final double PARALLAX_BASE_SPEED = 0.15;
    public static final double PARALLAX_DEPTH = 8.0;
    public static final double[] PARALLAX_SPEED_LAYERS = {1.0, 0.6, 0.35, 0.2};

    // PADDLE
    public static final int INIT_PADDLE_X = WIDTH / 2 - 50;
    public static final int INIT_PADDLE_Y = HEIGHT - 30;

    // WALL
    public static final int WIDTH_OF_WALLS        = 12;
    public static final int HEIGHT_OF_WALLS       = 200;
    public static final int N_OF_WALLS_LEFT_RIGHT = HEIGHT / HEIGHT_OF_WALLS;
    public static final int N_OF_WALLS_TOP        = WIDTH / HEIGHT_OF_WALLS;

    // BRICK
    public static final int MAXHEALTH_OF_BRICKS = 5;
    public static final int WIDTH_OF_BRICKS     = 30;
    public static final int HEIGHT_OF_BRICKS    = 16;
    public static final int MAXPOWER    = 5;
    public static final int MINPOWER    = 1;

    // POWER UP
    public static final String ACCELERATE   = "ACCELERATE";
    public static final String STRONGER     = "STRONGER";
    public static final String BIGGERPADDLE = "BIGGERPADDLE";
    public static final String STOPTIME     = "STOPTIME";
    public static final String[] powerUps   = {ACCELERATE, STRONGER, STOPTIME, BIGGERPADDLE};

    // BALL
    public static final int INIT_BALL_X = WIDTH / 2 - 10;
    public static final int INIT_BALL_Y = HEIGHT - 60;
    public static final float BASE_SPEED_BALL   = 300F;
    public static final float BASE_SPEED_PADDLE = 400F;
    public static final double BALL_OFFSET_Y          = 10.0;   // Distance above paddle when stuck
    public static final double BALL_ALIGN_LERP_FACTOR = 1.0;    // Instant align when resetting
    public static final double BALL_INIT_DIR_X        = 0.0;    // Default X velocity direction
    public static final double BALL_INIT_DIR_Y        = -1.0;   // Default Y velocity direction

    // EFFECT
    public static final double EFFECT_DURATION       = 0.5;           // seconds
    public static final String EXPLOSION1_EFFECT_KEY = "explosion1";
    public static final String EXPLOSION2_EFFECT_KEY = "explosion2";

    // BALL SYSTEM
    public static final double BALL_ELAPSED_TIME = 0.5; // seconds
    public static final double BALL_PADDLE_OFFSET_Y = 100.0;
    public static final double BALL_ALIGN_WITH_PADDLE_OFFSET_Y = 0.0;
    public static final double BALL_ALIGN_WITH_PADDLE_LERPFACTOR = 0.1;
    public static final double BALL_ACCELERATION_FACTOR = 1.5;
    public static final int    BALL_BOUNCE_ANGLE_LEFT = 150;
    public static final int    BALL_BOUNCE_ANGLE_RIGHT = 30;

    // BRICK SYSTEM
    public static final int POWERUP_SPAWN_CHANCE = 100;   // TODO: Make this to 30%

    // PADDLE SYSTEM
    public static final double PADDLE_BIGGER_SCALE_X = 2.0;
    public static final double PADDLE_BIGGER_SCALE_Y = 1.0;

    // PARALLAX SYSTEM
    public static final double PARALLAX_CAMERA_TARGET_MIN   = 0.0;
    public static final double PARALLAX_CAMERA_TARGET_MAX   = 1.0;
    public static final double PARALLAX_CAMERA_DELTA_X      = 0.0005;
    public static final double PARALLAX_NORMALIZE_DENOM_MIN = 1.0;

    // POWERUP SYSTEM
    public static final long POWERUP_ACTIVATE_DURATION = 5000;

    // MAP
    public static final int PADDING_X = 0;
    public static final int PADDING_Y = 0;
    private final static int CELL_W = GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X;
    private final static int USABLE_WIDTH = WIDTH - 2 * GameVar.WIDTH_OF_WALLS;
    public final static int MATRIX_COLS = USABLE_WIDTH / CELL_W;
    public final static int MATRIX_START_X = WIDTH_OF_WALLS + (USABLE_WIDTH - MATRIX_COLS * (WIDTH_OF_BRICKS + PADDING_X))/2 + WIDTH_OF_BRICKS/2;
    public final static int MATRIX_START_Y = MATRIX_START_X;

    // SOUND
    public static final long PADDLE_SOUND_COOLDOWN = 150;  // ms

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

    // GLOW TEXT
    public static final double GLOW_FONT_SIZE = 48.0;

    public static final double GLOW_ANIMATION_DURATION = 2.5;  // seconds
    public static final double GLOW_OFFSET_START       = 0.0;
    public static final double GLOW_OFFSET_END         = 1.0;

    public static final double[] GLOW_GRADIENT_STOPS = {
            0.0, 0.30, 0.40, 0.50, 0.60, 0.70, 1.0
    };

    public static final Color GLOW_COLOR_BASE      = Color.web("#555555");
    public static final Color GLOW_COLOR_CYAN      = Color.web("#00b8ff");
    public static final Color GLOW_COLOR_HIGHLIGHT = Color.web("#ffffff");

    // TRANSITION EFFECT
    public static final double TRANSITION_MIN_DURATION = 0.01;  // seconds
    public static final double TRANSITION_HALF_FACTOR   = 2.0;
    public static final double TRANSITION_OPACITY_FULL  = 1.0;
    public static final Color  TRANSITION_DEFAULT_COLOR = Color.BLACK;

    // PARALLAX
    public static final double PARALLAX_FRAME_DURATION = 0.2; // seconds per frame (5 FPS)
    public static final boolean PARALLAX_PRESERVE_RATIO = false;
    public static final boolean PARALLAX_SMOOTH_SCALING = true;
    public static final double PARALLAX_Y_OFFSET = 0.0;
}