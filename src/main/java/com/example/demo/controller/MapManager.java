package com.example.demo.controller;

import com.example.demo.model.core.bricks.SteelBrick;
import com.example.demo.model.core.bricks.ExplosionBrick;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.core.Wall;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.states.MapData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {

    private final int CELL_W = GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X;
    private final int CELL_H = GameVar.HEIGHT_OF_BRICKS + GameVar.PADDING_Y;

    private final int MATRIX_COLS = GameVar.BRICKS_PER_ROW; // giờ là 20
    private final int MATRIX_ROWS = (GlobalVar.HEIGHT - GameVar.HEIGHT_OF_WALLS * 2) / CELL_H;

    private final int MATRIX_START_X = GameVar.WIDTH_OF_WALLS;
    private final int MATRIX_START_Y = GameVar.HEIGHT_OF_WALLS;

    private final Random rand = new Random();

    public MapData loadMap(int level) {
        int[][] matrix;
        if (level == 1) matrix = createMap1Matrix();
        else if (level == 2) matrix = createMap2Matrix();
        else if (level == 3) matrix = createMap3Matrix();
        else matrix = createRandomMatrix();

        return loadMapFromMatrix(matrix);
    }

    public MapData loadMapFromMatrix(int[][] matrix) {
        List<Brick> bricks = new ArrayList<>();
        List<Wall> walls = createBoundaryWalls();

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                double x = MATRIX_START_X + c * CELL_W;
                double y = MATRIX_START_Y + r * CELL_H;

                switch (matrix[r][c]) {
                    case 1 -> bricks.add(new Brick((int) x, (int) y));
                    case 2 -> bricks.add(new SteelBrick((int) x, (int) y));
                    case 3 -> bricks.add(new ExplosionBrick((int) x, (int) y));
                }
            }
        }
        return new MapData(bricks, walls);
    }

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

        return walls;
    }

    // Level 1: nửa trên toàn gạch
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

    // Level 2: trái tim giữa màn (chỉnh cho 20 cột)
    public int[][] createMap2Matrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];

        // Hình trái tim mở rộng 20 cột
        int[][] heartShape = {
                {0,0,0,2,1,1,1,2,0,0,0,0,2,1,1,1,2,0,0,0},
                {0,0,2,1,1,1,1,1,2,0,0,2,1,1,1,1,1,2,0,0},
                {0,2,1,1,1,1,1,1,1,2,2,1,1,1,1,1,1,1,2,0},
                {2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2},
                {0,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,0},
                {0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
                {0,0,0,2,1,1,1,1,1,1,1,1,1,1,1,1,2,0,0,0},
                {0,0,0,0,2,1,1,1,1,1,1,1,1,1,1,2,0,0,0,0},
                {0,0,0,0,0,2,1,1,1,1,1,1,1,1,2,0,0,0,0,0},
                {0,0,0,0,0,0,2,1,1,1,1,1,1,2,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,2,1,1,1,1,2,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,2,1,1,2,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };


        final int HEART_ROWS = heartShape.length;

        // Vẽ trái tim
        for (int r = 0; r < HEART_ROWS; r++) {
            for (int c = 0; c < MATRIX_COLS ; c++) {
                if(heartShape[r][c] == 1) map[r][c] = 1;
                if(heartShape[r][c] == 2) map[r][c] = 2;
            }
        }

        return map;
    }

    // Level 3: cột + gạch chéo (điều chỉnh theo 20 cột)
    public int[][] createMap3Matrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];
        int[][] towerShape = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 2, 2, 2, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 2, 2, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 0, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0}
        };

        final int TOWER_ROWS = towerShape.length;

        // Vẽ trái tim
        for (int r = 0; r < TOWER_ROWS; r++) {
            for (int c = 0; c < MATRIX_COLS ; c++) {
                if(towerShape[r][c] == 1) map[r][c] = 1;
                if(towerShape[r][c] == 2) map[r][c] = 2;
            }
        }
        return map;
    }

    // Level ngẫu nhiên
    public int[][] createRandomMatrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];
        for (int r = 0; r < MATRIX_ROWS / 2; r++) {
            for (int c = 0; c < MATRIX_COLS; c++) {
                int type = rand.nextInt(10);
                if (type < 4) map[r][c] = 1;
                else if (type < 5) map[r][c] = 2;
                else if (type < 8) map[r][c] = 3;
            }
        }
        return map;
    }
}
