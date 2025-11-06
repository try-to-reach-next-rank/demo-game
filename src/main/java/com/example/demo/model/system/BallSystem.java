package com.example.demo.model.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.Wall;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

import java.util.ArrayList;
import java.util.List;

public class BallSystem implements Updatable {

    // ===================== Fields =====================
    private final Ball ball;
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();

    // ===================== Constructor =====================
    public BallSystem(Ball ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    // ===================== Game Loop =====================
    @Override
    public void update(double deltaTime) {
        handleStopTime(deltaTime);

        if (ball.isStuck()) {
            alignWithPaddle(
                    GameVar.BALL_ALIGN_WITH_PADDLE_OFFSET_Y,
                    GameVar.BALL_ALIGN_WITH_PADDLE_LERPFACTOR
            );
            return;
        }

        moveBall(deltaTime);
    }

    // ===================== Ball Movement Logic =====================
    private void handleStopTime(double deltaTime) {
        if (!ball.isStopTime()) return;

        ball.setElapsedTime(ball.getElapsedTime() + deltaTime);

        if (ball.getElapsedTime() >= GameVar.BALL_ELAPSED_TIME) {
            ball.setElapsedTime(0);

            // Random new direction
            double angle = GameRandom.nextDouble() * 2 * Math.PI;
            double vx = Math.cos(angle);
            double vy = Math.sin(angle);

            // Ensure ball doesn't go downward when near paddle
            if (ball.getY() >= paddle.getY() - GameVar.BALL_PADDLE_OFFSET_Y) {
                vy = -Math.abs(vy);
            }
            ball.setVelocity(vx, vy);
        }
    }

    private void moveBall(double deltaTime) {
        double speed = ball.getBaseSpeed();
        if (ball.isAccelerated()) {
            speed *= GameVar.BALL_ACCELERATION_FACTOR;
        }

        Vector2D step = ball.getVelocity().normalize().multiply(speed * deltaTime);
        double newX = ball.getX() + step.x;
        double newY = ball.getY() + step.y;

        // If ball falls below bottom screen -> reset
        if (newY >= GlobalVar.HEIGHT) {
            ball.resetState();
            return;
        }

        ball.setPosition(newX, newY);
    }

    // ===================== Public Control API =====================
    public void resetBall(Ball ball) {
        ball.resetState();
        alignWithPaddle(GameVar.BALL_OFFSET_Y, GameVar.BALL_ALIGN_WITH_PADDLE_LERPFACTOR);
    }

    // ===================== Collision Handlers =====================
    public void bounceFromPaddle(Paddle paddle) {
        Ball ball = this.ball;

        double paddleLeft = paddle.getBounds().getMinX();
        double ballCenter = ball.getBounds().getMinX() + ball.getWidth() / 2.0;

        double hitPos = (ballCenter - paddleLeft) / paddle.getWidth(); // 0 → 1
        double angle = Math.toRadians(
                GameVar.BALL_BOUNCE_ANGLE_LEFT * (1 - hitPos) +
                        GameVar.BALL_BOUNCE_ANGLE_RIGHT * hitPos
        );

        ball.setVelocity(Math.cos(angle), -Math.sin(angle));
    }

    public void bounceFromWall(Ball ball, Wall wall) {
        Vector2D v = ball.getVelocity();
        switch (wall.getSide()) {
            case LEFT -> ball.setVelocity(Math.abs(v.x), v.y);
            case RIGHT -> ball.setVelocity(-Math.abs(v.x), v.y);
            case TOP -> ball.setVelocity(v.x, Math.abs(v.y));
        }
    }
    // ===================== move =====================
    public void alignWithPaddle(double offsetY, double lerpFactor) {
        double targetX = paddle.getX() + paddle.getWidth() / 2.0 - ball.getWidth() / 2.0;
        double targetY = paddle.getY() - ball.getHeight() - offsetY;

        if (lerpFactor >= 1.0) {
            ball.setX(targetX);
            ball.setY(targetY);
        } else {
            double preX = ball.getX();
            double preY = ball.getY();
            ball.setX(preX + (targetX - preX) * lerpFactor);
            ball.setY(preY + (targetY - preY) * lerpFactor);
        }

        double minX = paddle.getX();
        double maxX = paddle.getX() + paddle.getWidth() - ball.getWidth();
        if (ball.getX() < minX) ball.setX(minX);
        if (ball.getX() > maxX) ball.setX(maxX);
        ball.setPosition(ball.getX(), ball.getY());
    }
}
