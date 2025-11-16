package com.example.demo.model.core.entities.bricks;

import com.example.demo.view.graphics.BrickTextureProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalBrickTest {

    private NormalBrick brick;

    @BeforeEach
    void setUp() {
        brick = new NormalBrick(50, "brick_full.png", 0, 0, 40, 20);
    }

    @Test
    void testTakeDamage_NotDestroyed_UpdatesTexture() {
        // Giảm damage nhỏ → không destroyed
        int damage = 10;
        boolean destroyed = brick.takeDamage(damage);

        assertFalse(destroyed, "Brick should not be destroyed yet");
        assertEquals(40, brick.getHealth(), "Health should be reduced by damage");

        // Kiểm tra imageKey dựa trên BrickTextureProvider
        String expectedTexture = BrickTextureProvider.getTextureForHealth(40);
        assertEquals(expectedTexture, brick.getImageKey(), "ImageKey should match texture for current health");
    }

    @Test
    void testTakeDamage_Destroyed() {
        // Gây damage = toàn bộ health → destroyed
        boolean destroyed = brick.takeDamage(50);

        assertTrue(destroyed, "Brick should be destroyed");
        assertEquals(0, brick.getHealth(), "Health should be zero when destroyed");
        assertTrue(brick.isDestroyed(), "Brick destroyed flag should be true");
    }

    @Test
    void testGetScoreValue() {
        // initialHealth = 50 → score = 50 * 10 = 500
        assertEquals(500, brick.getScoreValue(), "Score should be 500");

        NormalBrick brick2 = new NormalBrick(0, "brick.png", 0,0,40,20);
        assertEquals(0, brick2.getScoreValue(), "Score should be 0 when initialHealth = 0");

        NormalBrick brick3 = new NormalBrick(Integer.MAX_VALUE, "brick.png", 0,0,40,20);
        assertEquals(0, brick3.getScoreValue(), "Score should be 0 when initialHealth = Integer.MAX_VALUE");
    }

    @Test
    void testIsVisible() {
        assertTrue(brick.isVisible(), "Brick should be visible initially");

        brick.takeDamage(50);
        assertFalse(brick.isVisible(), "Brick should not be visible when destroyed");
    }
}
