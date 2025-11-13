package com.example.demo.controller.map;

import com.example.demo.model.map.MapData;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapControllerTest {

    private MapController mapManager;

    @BeforeEach
    void setUp() {
        mapManager = new MapController();
    }

    @Test
    void testLoadMapFromMatrix_emptyMatrix_shouldReturnOnlyWalls() {
        int[][] matrix = {
                {0, 0},
                {0, 0}
        };

        MapData data = mapManager.loadMapFromMatrix(matrix);

        assertNotNull(data);
        assertEquals(0, data.getBricks().size(), "Không nên tạo brick nào khi matrix toàn 0");
        assertTrue(data.getWalls().size() > 0, "Phải có tường biên mặc định");
    }

    @Test
    void testLoadMapFromMatrix_singleBrick_shouldHaveCorrectPositionAndHealth() {
        int[][] matrix = {{1}};

        MapData data = mapManager.loadMapFromMatrix(matrix);
        List<Brick> bricks = data.getBricks();

        assertEquals(1, bricks.size(), "Phải có đúng 1 brick");
        Brick b = bricks.get(0);

        double expectedX = GameVar.MATRIX_START_X;
        double expectedY = GameVar.MATRIX_START_Y;

        assertEquals(expectedX, b.getX(), 0.001);
        assertEquals(expectedY, b.getY(), 0.001);
        assertTrue(b.getHealth() >= 1 && b.getHealth() <= 5, "Máu của brick loại 1 nằm trong [1..5]");
    }
    // test chưa pass vì chưa merge steel bricks
     @Test
     void testLoadMapFromMatrix_indestructibleBrick_shouldHaveMaxHealth() {
     int[][] matrix = {{2}};

     MapData data = mapManager.loadMapFromMatrix(matrix);
     Brick brick = data.getBricks().get(0);

     assertEquals(Integer.MAX_VALUE, brick.getHealth());
     String expectedTexture = BrickTextureProvider.getTextureForHealth(brick.getHealth());
     assertEquals(expectedTexture, brick.getImageKey());
     }

    @Test
    void testLoadMap_validLevel_shouldReturnNonEmptyData() {
        MapData data = mapManager.loadMap(1);

        assertNotNull(data);
        assertFalse(data.getBricks().isEmpty(), "Map level 1 phải có ít nhất 1 brick");
        assertFalse(data.getWalls().isEmpty(), "Phải có tường biên");
    }
}
