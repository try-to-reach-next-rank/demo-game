package com.example.demo.view;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.Brick;
import com.example.demo.model.core.Wall;
import com.example.demo.model.map.MapData;
import com.example.demo.view.Renderer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class LoadLevelTest {

    @Test
    void load_replacesWorldWalls_setsBricks_and_callsWorldAndRendererReset() {
        // Arrange
        GameWorld world = new GameWorld();
        Renderer renderer = new Renderer(world);

        // tạo MapData giả lập
        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(Wall.Side.TOP, 0, 10, 10, 10));
        List<Brick> bricks = new ArrayList<>();
        bricks.add(new Brick(null, 50, 50, 5));

        MapData mapData = new MapData(bricks, walls);

        // ghi đè tạm loadMap() bằng cách tạo subclass cục bộ
        MapController customMapManager = new MapController() {
            @Override
            public MapData loadMap(int level) {
                return mapData;
            }
        };

        LoadLevel loader = new LoadLevel(customMapManager, world, renderer);

        // thêm dữ liệu cũ để xem có bị thay thế không
        world.getWalls().add(new Wall(Wall.Side.LEFT, 0, 10, 10, 10));
        world.setBricks(new Brick[] {
                new Brick(null, 999, 999, 5)
        });

        // Act
        MapData result = loader.load(1);
        // Assert
        assertSame(mapData, result);

        assertEquals(1, world.getWalls().size());
        assertEquals(walls.get(0), world.getWalls().get(0));

        assertEquals(1, world.getBricks().length);
        assertEquals(bricks.get(0), world.getBricks()[0]);
    }
}
