package com.example.demo.model.core.bricks;

import com.example.demo.model.utils.GameVar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickTest {

    @Test
    void constructorInitializesHealthAndNotDestroyed() {
        Brick b = new Brick(null, 6.0, 5.5, 5);

        assertEquals(5, b.getHealth());
        assertFalse(b.isDestroyed());
        assertEquals(6.0, b.getX(), 1e-9);
        assertEquals(5.5, b.getY(), 1e-9);
    }

    @Test
    void settersAndGettersWork() {
        Brick b = new Brick(null, 6.0, 5.5, 5);

        b.setHealth(5);
        assertEquals(5, b.getHealth());

        b.setDestroyed(true);
        assertTrue(b.isDestroyed());

        b.setDestroyed(false);
        assertFalse(b.isDestroyed());
    }

    @Test
    void applyStateWithNullDoesNothing() {
        Brick b = new Brick(null, 4.0, 1.0, 5);

        b.applyState(null); // should not throw and should leave state unchanged
        assertEquals(5, b.getHealth());
        assertFalse(b.isDestroyed());
    }
}