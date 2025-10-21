package com.example.demo.model.map;

import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;

import java.util.*;

public class MapData {
    private final List<Brick> bricks;
    private final List<Wall> walls;

    public MapData(List<Brick> bricks, List<Wall> walls) {
        this.bricks = List.copyOf(bricks); // để tránh thay đổi sau này
        this.walls = List.copyOf(walls);
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    private final static int CELL_W = GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X;
    private final static int CELL_H = GameVar.HEIGHT_OF_BRICKS + GameVar.PADDING_Y;

    // Compute the usable width/height inside walls
    private final static int USABLE_WIDTH = GlobalVar.WIDTH - 2 * GameVar.WIDTH_OF_WALLS;
    private final static int USABLE_HEIGHT = GlobalVar.HEIGHT - GameVar.HEIGHT_OF_WALLS * 2;

    // Brick grid starts just *inside* top and left walls
    private final static int MATRIX_START_X = USABLE_WIDTH + GameVar.PADDING_X / 2;
    private final static int MATRIX_START_Y = USABLE_HEIGHT + GameVar.PADDING_X / 2;

    // Number of columns and rows that actually fit inside
    private final static int MATRIX_COLS = USABLE_WIDTH / CELL_W;
    private final static int MATRIX_ROWS = (GlobalVar.HEIGHT / 2 - MATRIX_START_Y) / CELL_H;

    private final Random rand = new Random();

    // ---------------------------------------------------------------------
    //  Main Entry
    // ---------------------------------------------------------------------


    // ---------------------------------------------------------------------
    //  Wall Generation
    // ---------------------------------------------------------------------

    public static List<Wall> createBoundaryWalls() {
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
    public static int resolveHealth(int cellValue) {
        return switch (cellValue) {
            case 2 -> 3;  // stronger brick
            case 3 -> 5;  // even stronger
            default -> 1; // default weak
        };
    }


    public static int[][] createMap1Matrix() {
        int[][] map = {
                {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0}
        };
        return map;
    }

    public static int[][] createMap2Matrix() {
        int[][] map = {
                {0, 0, 0, 2, 1, 1, 1, 2, 0, 0, 2, 1, 1, 1, 2, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 2, 0, 0, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0},
                {2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0},
                {0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0},
                {0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        return map;
    }

    public static int[][] createMap3Matrix() {
        int[][] map = {
                {0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        return map;
    }
}

