package com.example.demo.controller;

import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.states.MapData;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.view.graphics.BrickTextureProvider;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MapManager is responsible for constructing the initial game world (bricks + walls)
 * based on predefined map matrices. It now uses BrickTextureProvider and the new Brick data model.
 */
public class MapManager {

    private final int CELL_W = GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X;
    private final int CELL_H = GameVar.HEIGHT_OF_BRICKS + GameVar.PADDING_Y;

    // Compute the usable width/height inside walls
    private final int USABLE_WIDTH  = GlobalVar.WIDTH  - 2 * GameVar.WIDTH_OF_WALLS;
    private final int USABLE_HEIGHT = GlobalVar.HEIGHT - GameVar.HEIGHT_OF_WALLS * 2;

    // Number of columns and rows that actually fit inside
    private final int MATRIX_COLS = USABLE_WIDTH  / CELL_W;
    private final int MATRIX_ROWS = USABLE_HEIGHT / CELL_H;

    // Brick grid starts just *inside* top and left walls
    private final int MATRIX_START_X = GameVar.WIDTH_OF_WALLS;
    private final int MATRIX_START_Y = GameVar.HEIGHT_OF_WALLS;

    private final Random rand = new Random();

    // ---------------------------------------------------------------------
    //  Main Entry
    // ---------------------------------------------------------------------

    public MapData loadMap(int level) {
        int[][] matrix = new int[0][];
        if (level == 1) matrix = createMap1Matrix();
//        else if (level == 2) matrix = createMap2Matrix();
//        else if (level == 3) matrix = createMap3Matrix();
//        else matrix = createRandomMatrix();

        return loadMapFromMatrix(matrix);
    }

    public MapData loadMapFromMatrix(int[][] matrix) {
        List<Brick> bricks = new ArrayList<>();
        List<Wall> walls = createBoundaryWalls();

        final double brickW = GameVar.WIDTH_OF_BRICKS;
        final double brickH = GameVar.HEIGHT_OF_BRICKS;
        final double padX = GameVar.PADDING_X * 1.75;
        final double padY = GameVar.PADDING_Y;

        // --- Total width of one full row of bricks (for centering) ---
        double startX = GameVar.WIDTH_OF_WALLS;
        double startY = GameVar.HEIGHT_OF_WALLS;

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                int type = matrix[r][c];
                if (type <= 0) continue; // 0 = empty cell

                double x = startX + c * (brickW + padX);
                double y = startY + r * (brickH + padY);

                int health = resolveHealth(type);
                Image texture = BrickTextureProvider.getTextureForHealth(health);

                bricks.add(new Brick(texture, x, y, health));
            }
        }
        return new MapData(bricks, walls);
    }

    // ---------------------------------------------------------------------
    //  Wall Generation
    // ---------------------------------------------------------------------

    private List<Wall> createBoundaryWalls() {
        List<Wall> walls = new ArrayList<>();

        for (int i = 0; i < GameVar.N_OF_WALLS_LEFT_RIGHT; i++) {
            walls.add(new Wall(Wall.Side.LEFT, 0, i * GameVar.HEIGHT_OF_WALLS,
                    GameVar.WIDTH_OF_WALLS, GameVar.HEIGHT_OF_WALLS));
            walls.add(new Wall(Wall.Side.RIGHT, GlobalVar.WIDTH - GameVar.WIDTH_OF_WALLS,
                    i * GameVar.HEIGHT_OF_WALLS, GameVar.WIDTH_OF_WALLS, GameVar.HEIGHT_OF_WALLS));
        }

        for (int i = 0; i < GameVar.N_OF_WALLS_TOP; i++) {
            walls.add(new Wall(Wall.Side.TOP, i * GameVar.WIDTH_OF_WALLS, 0,
                    GameVar.WIDTH_OF_WALLS, GameVar.HEIGHT_OF_WALLS));
        }
        System.out.println("Walls created: " + walls.size());
        return walls;
    }

    // ---------------------------------------------------------------------
    //  Map Matrix Definitions
    // ---------------------------------------------------------------------

    // 1 = weak brick, 2 = medium, 3 = strong
    private int resolveHealth(int cellValue) {
        return switch (cellValue) {
            case 2 -> 3;  // stronger brick
            case 3 -> 5;  // even stronger
            default -> 1; // default weak
        };
    }

    public int[][] createMap1Matrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];
        final int HALF_SCREEN_ROWS = (GlobalVar.HEIGHT / 2 - MATRIX_START_Y) / CELL_H;

        for (int r = 0; r < HALF_SCREEN_ROWS; r++) {
            for (int c = 0; c < MATRIX_COLS; c++) {
                map[r][c] = 1;
            }
        }
        return map;
    }
}
