package com.example.demo.controller.map;

import com.example.demo.model.core.builder.MapBuilder;
import com.example.demo.model.map.MapData;
import com.example.demo.utils.var.GameVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapController decide which map to load
 */
public class MapController {
    private static final Logger log = LoggerFactory.getLogger(MapController.class);

    public MapData loadMap(int level) {
        int[][] matrix = switch (level) {
            case 1 -> MapData.createMap1Matrix();
            case 2 -> MapData.createMap2Matrix();
            case 3 -> MapData.createMap3Matrix();
            default -> new int[0][0];
        };

        return new MapBuilder()
                .addBoundaryWalls()
                .addBricksFromMatrix(matrix)
                .build();
    }

    public int getNextLevel(int currentLevel) {
        int nextLevel = currentLevel + 1;
        if (nextLevel > GameVar.MAX_LEVEL) {
            nextLevel = GameVar.MIN_LEVEL;
        }
        log.info("Next level: {} -> {}", currentLevel, nextLevel);
        return nextLevel;
    }
    public int getPreviousLevel(int currentLevel) {
        int prevLevel = currentLevel - 1;
        if (prevLevel < GameVar.MIN_LEVEL) {
            prevLevel = GameVar.MAX_LEVEL;
        }
        log.info("Previous level: {} -> {}", currentLevel, prevLevel);
        return prevLevel;
    }
}
