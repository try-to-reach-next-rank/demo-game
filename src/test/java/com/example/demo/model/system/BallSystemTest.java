package com.example.demo.model.system;

import com.example.demo.controller.system.BallSystem;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.Wall;
import com.example.demo.model.core.entities.MovedWall;
import com.example.demo.model.core.entities.bricks.NormalBrick;
import com.example.demo.utils.Vector2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BallSystemTest {

    private static final double EPS = 1e-6;

    @Test
    void update_whenBallIsStuck_alignsWithPaddleAndDoesNotApplyMovementStep() {
        Paddle paddle = new Paddle();
        paddle.setPosition(200.0, 400.0);

        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);
        ball.setPosition(0.0, 0.0);
        ball.setStuck(true);

        BallSystem system = new BallSystem(List.of(ball));
        system.update(0.016);

        assertTrue(ball.isStuck());
        assertTrue(ball.getX() > 0.0, "Ball.x should have moved towards paddle");
    }

    @Test
    void update_whenNotStuck_movesAccordingToVelocityAndSpeed_and_accountsForAccelerationFlag() {
        Paddle paddle = new Paddle();
        paddle.setPosition(100.0, 300.0);

        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);
        ball.release();
        assertFalse(ball.isStuck());

        ball.setVelocity(new Vector2D(1.0, 0.0));
        ball.setPosition(10.0, 20.0);

        BallSystem system = new BallSystem(List.of(ball));

        // Update without acceleration
        system.update(1.0);
        double expectedX = 10.0 + ball.getBaseSpeed() * 1.0;
        assertEquals(expectedX, ball.getX(), EPS);

        // Update with acceleration
        ball.setPosition(10.0, 20.0);
        ball.setVelocity(new Vector2D(1.0, 0.0));
        ball.setAccelerated(true);
        system.update(1.0);
        double expectedAcceleratedX = 10.0 + ball.getBaseSpeed() * 1.5 * 1.0;
        assertEquals(expectedAcceleratedX, ball.getX(), EPS);
    }

    // ==================== MỚI BỔ SUNG ====================

    @Test
    void resetBall_shouldResetState() {
        Ball ball = new Ball();
        BallSystem system = new BallSystem(List.of(ball));

        ball.setAccelerated(true);
        ball.setStronger(true);
        ball.setDrunk(true);
        ball.setVelocity(new Vector2D(5, 5));

        system.resetBall(ball);

        assertTrue(ball.isStuck());
        assertFalse(ball.isAccelerated());
        assertFalse(ball.isStronger());
        assertFalse(ball.isDrunk());
        assertEquals(0.0, ball.getVelocity().x, EPS);
        assertEquals(-1.0, ball.getVelocity().y, EPS);
    }

    @Test
    void toggleCheats_shouldToggleAllBalls() {
        Ball ball = new Ball();
        BallSystem system = new BallSystem(List.of(ball));

        system.toggleDrunk();
        system.toggleAccelerated();
        system.toggleStronger();

        assertTrue(ball.isDrunk());
        assertTrue(ball.isAccelerated());
        assertTrue(ball.isStronger());

        system.toggleDrunk();
        system.toggleAccelerated();
        system.toggleStronger();

        assertFalse(ball.isDrunk());
        assertFalse(ball.isAccelerated());
        assertFalse(ball.isStronger());
    }

    @Test
    void handleCollision_withBrick_shouldInvertVelocity() {
        Ball ball = new Ball();
        ball.setVelocity(new Vector2D(1, 1));
        BallSystem system = new BallSystem(List.of(ball));

        NormalBrick brick = new NormalBrick(10, "brick.png", 0, 0, 10, 10);
        brick.takeDamage(0); // ensure not destroyed

        ball.setPosition(5, 5);

        system.handleCollision(ball, brick);
        // After collision, either x or y inverted
        Vector2D v = ball.getVelocity();
        assertTrue(v.x < 0.0 || v.y < 0.0, "One of the velocity components should have inverted");
    }

    @Test
    void handleCollision_withPaddle_shouldStickBall() {
        Paddle paddle = new Paddle();
        Ball ball = new Ball();
        ball.release();
        BallSystem system = new BallSystem(List.of(ball));

        system.handleCollision(ball, paddle);
        assertEquals(paddle, ball.getStuckPaddle());
    }

    @Test
    void handleCollision_withMovedWall_shouldAdjustVelocity() {
        Ball ball = new Ball();
        ball.release();
        BallSystem system = new BallSystem(List.of(ball));

        MovedWall mw = new MovedWall("wall.png");
        mw.activate(0, 0, 1.0);

        ball.setPosition(0, -10); // above wall
        ball.setVelocity(new Vector2D(0, 1));

        system.handleCollision(ball, mw);
        assertTrue(ball.getVelocity().y < 0.0, "Ball.y should invert when above moved wall");
    }

}
