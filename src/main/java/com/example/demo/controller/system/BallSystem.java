package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.MovedWall;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.Wall;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Sound;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.EffectRenderer;

import java.util.ArrayList;
import java.util.List;

public class BallSystem implements Updatable {
    private final List<Ball> balls;
    private long nextPaddleSoundTime = 0L;

    public BallSystem(List<Ball> balls) {
        this.balls = balls;
    }

    @Override
    public void update(double deltaTime) {
        for (Ball ball : balls) {
            if (ball.isDrunk()) {
                handleBallDrunk(deltaTime, ball);
            }

            if (ball.isStuck() && ball.getStuckPaddle() != null) {
                alignWithPaddle(ball, 
                                GameVar.BALL_ALIGN_WITH_PADDLE_OFFSET_Y, 
                                GameVar.BALL_ALIGN_WITH_PADDLE_LERPFACTOR);
            } else {
                updatePosition(ball, deltaTime);
            }
        }
    }

    @Override
    public void clear() {
        // TODO: CLEAR
    }

    public void handleCollision(Ball ball, GameObject obj) {
        if (obj instanceof Paddle paddle) {
            handlePaddleCollision(ball, paddle);
        } 
        else if (obj instanceof Wall wall) {
            handleWallCollision(ball, wall);
        }
        else if (obj instanceof MovedWall mw) {
            handleMovedWallCollision(ball, mw);
        }
        else if (obj instanceof Brick brick) {
            handleBrickCollision(ball, brick);
        }
    }

    public void resetBall(Ball ball) {
        ball.resetState();
    }

    // === CHEAT HANDLERS ===
    public void toggleDrunk() {
        for (Ball ball : balls) {
            ball.toggleDrunk();
        }
    }

    public void toggleAccelerated() {
        for (Ball ball : balls) {
            ball.toggleAccelerated();
        }
    }

    public void toggleStronger() {
        for (Ball ball : balls) {
            ball.toggleStronger();
        }
    }

    // --- When ball is drunk ---
    private void handleBallDrunk(double deltaTime, Ball ball) {
        ball.setElapsedTime(ball.getElapsedTime() + deltaTime);
        if (ball.getElapsedTime() >= GameVar.BALL_ELAPSED_TIME) {
            ball.setElapsedTime(0);
            double angle = GameRandom.nextDouble() * 2 * Math.PI;
            double vx = Math.cos(angle);
            double vy = Math.sin(angle);
            if (ball.getY() >= ball.getStuckPaddle().getY() - GameVar.BALL_PADDLE_OFFSET_Y) {
                vy = -Math.abs(vy);
            }
            ball.setVelocity(vx, vy);
        }
    }

    // --- When ball stuck ---
    private void alignWithPaddle(Ball ball, double offsetY, double lerpFactor) {
        Paddle paddle = ball.getStuckPaddle();
        double targetX = paddle.getX() + paddle.getWidth() / 2.0 - ball.getWidth() / 2.0;
        double targetY = paddle.getY() - ball.getHeight() - offsetY;

        double x = ball.getX();
        double y = ball.getY();

        if (lerpFactor >= 1.0) {
            x = targetX;
            y = targetY;
        } else {
            x += (targetX - x) * lerpFactor;
            y += (targetY - y) * lerpFactor;
        }

        double minX = paddle.getX();
        double maxX = paddle.getX() + paddle.getWidth() - ball.getWidth();
        if (x < minX) x = minX;
        if (x > maxX) x = maxX;

        ball.setPosition(x, y);
    }

    // --- When ball is not stuck
    private void updatePosition(Ball ball, double deltaTime) {
        double currentSpeed = ball.getBaseSpeed();
        if (ball.isAccelerated()) currentSpeed *= GameVar.BALL_ACCELERATION_FACTOR;

        Vector2D step = ball.getVelocity().normalize().multiply(currentSpeed * deltaTime);
        double newX = ball.getX() + step.x;
        double newY = ball.getY() + step.y;

        // Reset if it falls below the screen
        if (newY >= GameVar.MAP_MAX_Y) {
            ball.resetState();
            return;
        }

        ball.setPosition(newX, newY);
    }

    private void handlePaddleCollision(Ball ball, Paddle paddle) {
        if (ball.isStuck()) return;

        long now = System.currentTimeMillis();
        if (now > nextPaddleSoundTime) {
            Sound.getInstance().playSound("paddle_hit");
            nextPaddleSoundTime = now + GameVar.PADDLE_SOUND_COOLDOWN;
        }

        bounceFromPaddle(ball, paddle);
        ball.setStuckPaddle(paddle);
    }

    private void handleWallCollision(Ball ball, Wall wall) {
        bounceFromWall(ball, wall); // Delegate to BallSystem
        Sound.getInstance().playSound("wall_hit");

        // TODO: SRP
        // Simple effect (View layer responsibility)
        EffectRenderer.getInstance().spawn(
                GameVar.EXPLOSION2_EFFECT_KEY,
                ball.getX() + ball.getWidth() / 2,
                ball.getY() + ball.getHeight() / 2,
                GameVar.EFFECT_DURATION
        );
    }

    private void handleMovedWallCollision(Ball ball, MovedWall mw) {
        // TODO: play another sound
        Sound.getInstance().playSound("wall_hit");

        Vector2D v = ball.getVelocity();
        
        // Lấy tâm 2 object
        double ballCenterY = ball.getY() + ball.getHeight() / 2;
        double wallCenterY = mw.getY() + mw.getHeight() / 2;

        // Nếu ball ở trên wall → bounce xuống
        if (ballCenterY < wallCenterY) {
            ball.setVelocity(v.x, -Math.abs(v.y));
        }
        // ball ở dưới → bounce lên
        else {
            ball.setVelocity(v.x, Math.abs(v.y));
        }
    }

    private void handleBrickCollision(Ball ball, Brick brick) {
        if (brick.isDestroyed()) return;
        if (ball.getLastBrick() == brick) return;
        if (ball.isStronger()) return;

        Vector2D v = ball.getVelocity();
        double overlapX = overlapX(ball, brick);
        double overlapY = overlapY(ball, brick);
        boolean fromSide = overlapX <= overlapY;
        
        if (fromSide) ball.setVelocity(-v.x, v.y);
        else ball.setVelocity(v.x, -v.y);

        resolveBallBrickOverlap(ball, brick);
    }

    public void bounceFromPaddle(Ball ball, Paddle paddle) {
        double paddleLPos = paddle.getBounds().getMinX();
        double ballCenterX = ball.getBounds().getMinX() + ball.getWidth() / 2.0;
        double hitPos = (ballCenterX - paddleLPos) / paddle.getWidth();
        double angle = Math.toRadians(GameVar.BALL_BOUNCE_ANGLE_LEFT * (1 - hitPos) + GameVar.BALL_BOUNCE_ANGLE_RIGHT * hitPos);
        ball.setVelocity(Math.cos(angle), -Math.sin(angle));
    }

    private void bounceFromWall(Ball ball, Wall wall) {
        Vector2D v = ball.getVelocity();
        switch (wall.getSide()) {
            case LEFT -> ball.setVelocity(Math.abs(v.x), v.y);
            case RIGHT -> ball.setVelocity(-Math.abs(v.x), v.y);
            case TOP -> ball.setVelocity(v.x, Math.abs(v.y));
        }
    }

    private double overlapX(GameObject a, GameObject b) {
        return Math.min(a.getBounds().getMaxX(), b.getBounds().getMaxX()) -
                Math.max(a.getBounds().getMinX(), b.getBounds().getMinX());
    }

    private double overlapY(GameObject a, GameObject b) {
        return Math.min(a.getBounds().getMaxY(), b.getBounds().getMaxY()) -
                Math.max(a.getBounds().getMinY(), b.getBounds().getMinY());
    }

    private void resolveBallBrickOverlap(Ball ball, Brick brick) {
        var ballBounds = ball.getBounds();
        var brickBounds = brick.getBounds();
        double overlapX = overlapX(ball, brick);
        double overlapY = overlapY(ball, brick);

        // Push ball out along the smaller overlap axis
        if (overlapX < overlapY) {
            if (ballBounds.getCenterX() < brickBounds.getCenterX())
                ball.setX(ball.getX() - overlapX);
            else
                ball.setX(ball.getX() + overlapX);
        } else {
            if (ballBounds.getCenterY() < brickBounds.getCenterY())
                ball.setY(ball.getY() - overlapY);
            else
                ball.setY(ball.getY() + overlapY);
        }
    }
}
