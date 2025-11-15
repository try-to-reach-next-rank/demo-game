package com.example.demo.model.system;

import com.example.demo.controller.system.BrickSystem;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.bricks.NormalBrick;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class BrickSystemTest {

    @Test
    void applyDamage_whenHealthBecomesZero_setsHealthZeroAndDestroyed() {
        // Arrange: create a real Brick and set it to 1 health
        Brick brick = new NormalBrick(1, null, 50, 50, 100, 0);
        brick.setHealth(1);
        brick.setDestroyed(false);

        List<Brick> bricks = List.of(brick);
        List<PowerUp> powerUps = new ArrayList<>();
        BrickSystem system = new BrickSystem(bricks, powerUps);

        // Act
        system.applyDamage(brick, false);

        // Assert: health decreased to 0 and brick marked destroyed
        assertEquals(0, brick.getHealth(), "Health should be reduced to 0 when damaged from 1");
        assertTrue(brick.isDestroyed(), "Brick should be marked destroyed when health reaches 0");
    }

    @Test
    void applyDamage_whenHealthGreaterThanOne_decrementsHealthAndDoesNotDestroy() {
        // Arrange: create a real Brick with health > 1
        Brick brick = new Brick(null, 50, 50, 100);

        brick.setHealth(3);
        brick.setDestroyed(false);

        Brick[] bricks = new Brick[]{brick};
        List<PowerUp> powerUps = new ArrayList<>();
        BrickSystem system = new BrickSystem(bricks, powerUps);

        // Act
        system.applyDamage(brick, false);

        // Assert: health decremented by 1, not destroyed
        assertEquals(2, brick.getHealth(), "Health should be decremented by 1 for non-lethal damage");
        assertFalse(brick.isDestroyed(), "Brick should not be destroyed when health remains positive");
    }

    @Test
    void onBallHitBrick_whenBrickAlreadyDestroyed_doesNothing() {
        // Arrange: create a brick and mark destroyed already
        Brick brick = new Brick(null, 50, 50, 100);
        brick.setHealth(2);
        brick.setDestroyed(true);

        Brick[] bricks = new Brick[]{brick};
        List<PowerUp> powerUps = new ArrayList<>();
        BrickSystem system = new BrickSystem(bricks, powerUps);

        // Act: try to hit an already destroyed brick
        system.onBallHitBrick(null, null);

        // Assert: state unchanged
        assertEquals(2, brick.getHealth(), "Health should remain unchanged for already destroyed brick");
        assertTrue(brick.isDestroyed(), "Destroyed flag should remain true");
    }


    //chưa refector nổ lan
    //void handleExplosion_appliesDamageOnlyToBricksWithinRadius() {

    //}
}