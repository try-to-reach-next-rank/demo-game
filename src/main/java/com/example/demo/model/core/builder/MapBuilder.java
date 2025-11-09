package com.example.demo.model.core.builder;

import com.example.demo.model.core.Brick;
import com.example.demo.model.core.Wall;
import com.example.demo.model.core.factory.BrickFactory;
import com.example.demo.model.map.MapData;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.var.GameVar;

import java.util.ArrayList;
import java.util.List;

/**
 * Map Builder handle layout & collection building
 */
public class MapBuilder {
    private final List<Brick> bricks = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();

    public MapBuilder addBoundaryWalls() {
        walls.addAll(MapData.createBoundaryWalls());
        return this;
    }

    public MapBuilder addBricksFromMatrix(int[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                int type = matrix[r][c];
                if (type <= 0) continue;

                double x = GameVar.MATRIX_START_X + c * (GameVar.WIDTH_OF_BRICKS + GameVar.PADDING_X);
                double y = GameVar.MATRIX_START_Y + r * (GameVar.HEIGHT_OF_BRICKS + GameVar.PADDING_Y);

                int randomHealth = GameRandom.nextInt(5) + 1;
                bricks.add(BrickFactory.createFromType(randomHealth, x, y));
            }
        }
        return this;
    }

    public MapData build() {
        return new MapData(bricks, walls);
    }
}
