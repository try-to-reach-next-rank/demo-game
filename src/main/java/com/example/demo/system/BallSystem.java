package com.example.demo.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.Wall;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Vector2D;

public class BallSystem implements Updatable {
    private final Ball ball;
    private final Paddle paddle;

    public BallSystem(Ball ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    @Override
    public void update(double deltaTime) {
        if (ball.isStuck()) {
            // Keep the ball above the paddle
            ball.setPosition(
                    paddle.getX() + paddle.getWidth() / 2.0 - ball.getWidth() / 2.0,
                    paddle.getY() - ball.getHeight()
            );
            return;
        }

        double currentSpeed = ball.getBaseSpeed();
        if (ball.isAccelerated()) currentSpeed *= 1.5;

        Vector2D step = ball.getVelocity().normalize().multiply(currentSpeed * deltaTime);
        double newX = ball.getX() + step.x;
        double newY = ball.getY() + step.y;

        // Reset if it falls below the screen
        if (newY >= GlobalVar.HEIGHT) {
            ball.resetState();
            return;
        }

        ball.setPosition(newX, newY);
    }

    public void resetBall(Ball ball) {
        ball.resetState();
    }

    public void bounceFromPaddle(Paddle paddle) {
        Ball ball = this.ball;
        double paddleLPos = paddle.getBounds().getMinX();
        double ballCenterX = ball.getBounds().getMinX() + ball.getWidth() / 2.0;
        double hitPos = (ballCenterX - paddleLPos) / paddle.getWidth();
        double angle = Math.toRadians(150 * (1 - hitPos) + 30 * hitPos);
        ball.setVelocity(new Vector2D(Math.cos(angle), -Math.sin(angle)));
    }

    public void bounceFromWall(Ball ball, Wall wall) {
        Vector2D v = ball.getVelocity();
        switch (wall.getSide()) {
            case LEFT -> ball.setVelocity(new Vector2D(Math.abs(v.x), v.y));
            case RIGHT -> ball.setVelocity(new Vector2D(-Math.abs(v.x), v.y));
            case TOP -> ball.setVelocity(new Vector2D(v.x, Math.abs(v.y)));
        }
    }
}
