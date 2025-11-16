package com.example.demo.model.system;

import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.utils.var.GameVar;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PowerUpSystemTest {

    @Test
    void activate_accelerate_addsToActiveAndSetsBallAccelerated() {
        // Arrange
        Paddle paddle = new Paddle();
        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);
        List<PowerUp> world = new ArrayList<>();
        PowerUpSystem system = new PowerUpSystem(ball, paddle, world);

        PowerUp p = new PowerUp(GameVar.ACCELERATE);

        // Pre-assert
        assertFalse(ball.isAccelerated(), "Ball should start not accelerated");

        // Act
        system.activate(p);

        // Assert
        assertTrue(ball.isAccelerated(), "Activating ACCELERATE should set ball.accelerated = true");
        assertEquals(1, system.getActivePowerUps().size(), "Active power-ups list should contain the activated power-up");
        assertSame(p, system.getActivePowerUps().get(0), "The activated power-up should be tracked in the active list");
    }

    @Test
    void activate_biggerPaddle_setsPaddleFlagAndAddsToActive() {
        // Arrange
        Paddle paddle = new Paddle();
        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);
        List<PowerUp> world = new ArrayList<>();
        PowerUpSystem system = new PowerUpSystem(ball, paddle, world);

        PowerUp p = new PowerUp(GameVar.BIGGERPADDLE);

        // Pre-assert
        assertFalse(paddle.getBiggerPaddle(), "Paddle should start not bigger");

        // Act
        system.activate(p);

        // Assert
        assertTrue(paddle.getBiggerPaddle(), "Activating BIGGERPADDLE should set paddle.biggerPaddle = true");
        assertEquals(1, system.getActivePowerUps().size(), "Active list should include the bigger-paddle power-up");
    }

    @Test
    void update_removesExpiredPowerUp_and_resetsFlags() throws InterruptedException{
        // Arrange
        Paddle paddle = new Paddle();
        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);
        List<PowerUp> world = new ArrayList<>();
        PowerUpSystem system = new PowerUpSystem(ball, paddle, world);

        // Create a power-up and mark it as expired using activateWithRemainingDuration(0)
        PowerUp expired = new PowerUp(GameVar.ACCELERATE);
        expired.activateWithRemainingDuration(1);

        // Manually ensure game object flag is true so update() has something to clear
        ball.setAccelerated(true);

        // Add the expired power-up into active list directly (simulate an already-active-but-expired power-up)
        system.getActivePowerUps().add(expired);

        // Pre-check
        assertTrue(ball.isAccelerated(), "Ball accelerated flag should be true before update");
        assertEquals(1, system.getActivePowerUps().size(), "Active list should contain the expired item before update");

        // Act
        Thread.sleep(2);
        system.update(0.016);

        // Assert: update should detect expiration, remove it and reset the flag
        assertFalse(ball.isAccelerated(), "Ball accelerated flag should be reset to false after expired power-up processed");
        assertTrue(system.getActivePowerUps().isEmpty(), "Expired power-up should be removed from active list after update");
    }

    @Test
    void reset_clearsAllFlags_and_activeList() {
        // Arrange
        Paddle paddle = new Paddle();
        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);
        List<PowerUp> world = new ArrayList<>();
        PowerUpSystem system = new PowerUpSystem(ball, paddle, world);

        // Simulate active states
        ball.setAccelerated(true);
        ball.setStronger(true);
        ball.setDrunk(true);
        paddle.setBiggerPaddle(true);

        // Put some dummy power-ups into active list
        PowerUp p1 = new PowerUp(GameVar.ACCELERATE);
        PowerUp p2 = new PowerUp(GameVar.BIGGERPADDLE);
        system.getActivePowerUps().add(p1);
        system.getActivePowerUps().add(p2);

        // Pre-check
        assertTrue(ball.isAccelerated());
        assertTrue(ball.isStronger());
        assertTrue(ball.isDrunk());
        assertTrue(paddle.getBiggerPaddle());
        assertEquals(2, system.getActivePowerUps().size());

        // Act
        system.reset();

        // Assert: flags cleared and list emptied
        assertFalse(ball.isAccelerated(), "Ball.accelerated should be false after reset");
        assertFalse(ball.isStronger(), "Ball.stronger should be false after reset");
        assertFalse(ball.isDrunk(), "Ball.drunk should be false after reset");
        assertFalse(paddle.getBiggerPaddle(), "Paddle.biggerPaddle should be false after reset");
        assertTrue(system.getActivePowerUps().isEmpty(), "Active power-ups list should be cleared by reset()");
    }
}