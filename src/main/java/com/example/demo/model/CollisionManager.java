package com.example.demo.model;

import com.example.demo.controller.core.*;

import java.util.List;
import java.util.Random;

public class CollisionManager {
    private final Random        random = new Random();
    private static final long   paddleSoundCooldown = 200L; // TODO: make it in the VARIABLES
    private long                nextPaddleSoundTime = 0;

    public void update(Ball ball, Paddle paddle, Brick[] bricks, List<PowerUp> powerUps, List<Wall> walls) {
        // ball-floor check
        if (ball.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
            SoundManager.getInstance().playSound("game_over");
            ball.resetState();
        }

        // powerups vs paddle
        for (PowerUp p : powerUps) {
            if (!p.isVisible()) continue;
            if (p.getBounds().intersects(paddle.getBounds())) {
                SoundManager.getInstance().playSound("power_up");
                ball.activatePowerUp(p);
                p.setVisible(false);
            }
            if (p.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) p.setVisible(false);
        }

        // ball vs paddle
        Collision c = buildCollision(ball, paddle);
        if (c != null) handleBallPaddleCollision(ball, paddle);

        // ball vs walls
        for (Wall w : walls) {
            c = buildCollision(ball, w);
            if (c != null) handleBallWallCollision(ball, w);
        }

        // ball vs bricks
        for (Brick brick : bricks) {
            c = buildCollision(ball, brick);
            if (c != null && !brick.isDestroyed()) {
                String sound = brick.takeDamage();
                if (sound != null) {
                    if ("explosion_hit".equals(sound)) {
                        brick.handleExplosion(brick, bricks); // TODO: add different types of bricks
                    }
                    SoundManager.getInstance().playSound(sound);
                }
                // bounce
                Vector2D v = ball.getVelocity();
                boolean fromSide = c.getOverlapX() < c.getOverlapY();
                if (fromSide) ball.setVelocity(new Vector2D(-v.x, v.y));
                else ball.setVelocity(new Vector2D(v.x, -v.y));

                if (brick.isDestroyed() && random.nextInt(100) < 30) {
                    PowerUp newPU = new PowerUp("ACCELERATE");
                    newPU.dropFrom(brick);
                    powerUps.add(newPU);
                }
            }
        }
    }

    private Collision buildCollision(GameObject a, GameObject b) {
        if (a == b) return null;
        if (!a.getBounds().intersects(b.getBounds())) return null;
        double overlapX = Math.min(a.getBounds().getMaxX(), b.getBounds().getMaxX()) -
                Math.max(a.getBounds().getMinX(), b.getBounds().getMinX());
        double overlapY = Math.min(a.getBounds().getMaxY(), b.getBounds().getMaxY()) -
                Math.max(a.getBounds().getMinY(), b.getBounds().getMinY());
        return new Collision(a, b, System.nanoTime(), overlapX, overlapY);
    }

    private void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!ball.isStuck()) {
            long now = System.currentTimeMillis();
            if (now > nextPaddleSoundTime) {
                SoundManager.getInstance().playSound("paddle_hit");
                nextPaddleSoundTime = now + paddleSoundCooldown;
            }
        }
        ball.setPosition(ball.getX(), paddle.getBounds().getMinY() - ball.getHeight());
        double paddleLPos = paddle.getBounds().getMinX();
        double ballCenterX = ball.getBounds().getMinX() + ball.getWidth() / 2.0;
        double hitPos = (ballCenterX - paddleLPos) / paddle.getWidth();
        double angle = Math.toRadians(150 * (1 - hitPos) + 30 * hitPos);
        ball.setVelocity(new Vector2D(Math.cos(angle), -Math.sin(angle)));
    }

    private void handleBallWallCollision(Ball ball, Wall wall) {
        Vector2D v = ball.getVelocity();
        switch (wall.getSide()) {
            case LEFT:
                ball.setVelocity(new Vector2D(Math.abs(v.x), v.y));
                break;
            case RIGHT:
                ball.setVelocity(new Vector2D(-Math.abs(v.x), v.y));
                break;
            case TOP:
                ball.setVelocity(new Vector2D(v.x, Math.abs(v.y)));
                break;
        }
        SoundManager.getInstance().playSound("wall_hit");
    }
}
