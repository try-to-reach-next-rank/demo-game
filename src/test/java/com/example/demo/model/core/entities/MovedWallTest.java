package com.example.demo.model.core.entities;

import com.example.demo.utils.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovedWallTest {

    private MovedWall wall;

    @BeforeEach
    void setUp() {
        wall = new MovedWall("wall.png");
    }

    @Test
    void testActivateAndDeactivate() {
        assertFalse(wall.isActive(), "Initially wall should be inactive");

        wall.activate(10, 20, 5.0);
        assertTrue(wall.isActive(), "Wall should be active after activate()");
        assertEquals(10, wall.getX(), 1e-9);
        assertEquals(20, wall.getY(), 1e-9);

        wall.deactivate();
        assertFalse(wall.isActive(), "Wall should be inactive after deactivate()");
    }

    @Test
    void testUpdateLifetime_DeactivatesAfterTime() {
        wall.activate(0, 0, 1.0); // lifeTime = 1 second
        assertTrue(wall.isActive());

        wall.updateLifetime(0.5); // half life
        assertTrue(wall.isActive(), "Wall should still be active at half life");

        wall.updateLifetime(0.5); // remaining life
        assertFalse(wall.isActive(), "Wall should be inactive after full lifeTime");
    }

    @Test
    void testIsVisible() {
        wall.activate(0, 0, 5.0);
        assertTrue(wall.isVisible(), "Wall should be visible when active");

        wall.deactivate();
        assertFalse(wall.isVisible(), "Wall should not be visible when inactive");
    }

    @Test
    void testSetAndGetDirection() {
        Vector2D dir = new Vector2D(3, 4);
        wall.setDirection(dir);
        Vector2D expected = dir.normalize();
        assertEquals(expected.x, wall.getDirection().x, 1e-9);
        assertEquals(expected.y, wall.getDirection().y, 1e-9);
    }

    @Test
    void testSetAndGetSpeed() {
        wall.setSpeed(10.0);
        assertEquals(10.0, wall.getSpeed(), 1e-9);
    }
}
