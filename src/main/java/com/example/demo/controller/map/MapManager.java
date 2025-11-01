package com.example.demo.controller.map;

import com.example.demo.model.core.Brick;
import com.example.demo.model.core.Wall;
import com.example.demo.model.map.MapData;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * MapManager is responsible for constructing the initial game world (bricks + walls)
 * based on predefined map matrices. It now uses BrickTextureProvider and the new Brick data model.
 */
public class MapManager {
    private final List<Brick> bricks = new ArrayList<>();

    public MapData loadMap(int level) {
        int[][] matrix = new int[0][];
        if (level == 1) matrix = MapData.createMap1Matrix();
        else if (level == 2) matrix = MapData.createMap2Matrix();
        else if (level == 3) matrix = MapData.createMap3Matrix();
//       else matrix = MapData.createRandomMatrix();

        return loadMapFromMatrix(matrix);
    }

    public MapData loadMapFromMatrix(int[][] matrix) {
        bricks.clear();
        List<Wall> walls = MapData.createBoundaryWalls();

        // --- Total width of one full row of bricks (for centering) ---

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                int type = matrix[r][c];
                if (type <= 0) continue; // 0 = empty cell

                double x = GameVar.MATRIX_START_X + c * (GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X);
                double y = GameVar.MATRIX_START_Y + r * (GameVar.HEIGHT_OF_BRICKS + GameVar.PADDING_Y);

                int health = (matrix[r][c] == 2) ? Integer.MAX_VALUE : (GameRandom.nextInt(5) + 1);
                String imageKey = BrickTextureProvider.getTextureForHealth(health);

                bricks.add(new Brick(imageKey, x, y, health));
            }
        }
        return new MapData(bricks, walls);
    }

}
