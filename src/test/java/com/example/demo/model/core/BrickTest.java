package com.example.demo.model.core;

import org.junit.jupiter.api.Test;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.bricks.NormalBrick;

import static org.junit.jupiter.api.Assertions.*;

class BrickTest {

    @Test
    void constructorInitializesHealthAndNotDestroyed() {
        Brick b = new NormalBrick(5, null, 6.0, 5.5, 5, 10);

        assertEquals(5, b.getHealth());
        assertFalse(b.isDestroyed());
        assertEquals(6.0, b.getX(), 1e-9);
        assertEquals(5.5, b.getY(), 1e-9);
    }

    @Test
    void applyStateWithNullDoesNothing() {
        Brick b = new NormalBrick(5, null, 6.0, 5.5, 5, 10);

        b.applyState(null); // should not throw and should leave state unchanged
        assertEquals(5, b.getHealth());
        assertFalse(b.isDestroyed());
    }
}