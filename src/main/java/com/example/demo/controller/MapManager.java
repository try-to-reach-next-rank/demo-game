package com.example.demo.controller;

import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.map.MapData;
import com.example.demo.model.utils.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * MapManager is responsible for constructing the initial game world (bricks + walls)
 * based on predefined map matrices. It now uses BrickTextureProvider and the new Brick data model.
 */
public class MapManager {


    public MapData loadMap(int level) {
        int[][] matrix = new int[0][];
        if (level == 1) matrix = MapData.createMap1Matrix();
//        else if (level == 2) matrix = createMap2Matrix();
//        else if (level == 3) matrix = createMap3Matrix();
//        else matrix = createRandomMatrix();

        return loadMapFromMatrix(matrix);
    }

    public MapData loadMapFromMatrix(int[][] matrix) {
        List<Brick> bricks = new ArrayList<>();
        List<Wall> walls = MapData.createBoundaryWalls();

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

                int health = MapData.resolveHealth(type);
                Image texture = BrickTextureProvider.getTextureForHealth(health);

                bricks.add(new Brick(texture, x, y, health));
            }
        }
        return new MapData(bricks, walls);
    }

}
