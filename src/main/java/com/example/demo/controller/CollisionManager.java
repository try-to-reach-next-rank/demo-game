package com.example.demo.controller;

import com.example.demo.model.core.*;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.Sound;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Vector2D;
import com.example.demo.view.EffectRenderer;

import java.util.List;
import java.util.Random;

public class CollisionManager {
    private final Random        random = new Random();
    private static final long   paddleSoundCooldown = 200L; // TODO: make it in the VARIABLES
    private long                nextPaddleSoundTime = 0;

    public void update(Ball ball, Paddle paddle, Brick[] bricks, List<PowerUp> powerUps, List<Wall> walls) {
        // ball-floor check
        handleBallFloorCollision(ball);

        // powerups vs paddle
        handlePaddlePowerUpCollisions(ball, paddle, powerUps);

        // ball vs paddle
        handleBallPaddleCollision(ball, paddle);

        // ball vs walls
        handleBallWallCollisions(ball, walls);

        // ball vs bricks
        handleBallBrickCollisions(ball, bricks, powerUps);

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

    private void handleBallFloorCollision(Ball ball) {
        if (ball.getBounds().getMaxY() > GlobalVar.BOTTOM_EDGE) {
            Sound.getInstance().playSound("game_over");
            ball.resetState();
        }
    }

    private void handlePaddlePowerUpCollisions(Ball ball, Paddle paddle, List<PowerUp> powerUps) {
        for (PowerUp p : powerUps) {
            if (!p.isVisible()) continue;
            if (p.getBounds().intersects(paddle.getBounds())) {
                Sound.getInstance().playSound("power_up");
                ball.activatePowerUp(p);
                p.setVisible(false);
            }
            if (p.getBounds().getMaxY() > GlobalVar.BOTTOM_EDGE) p.setVisible(false);
        }
    }

    private void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        Collision c = buildCollision(ball, paddle);
        if (c == null) return;
        
        if (!ball.isStuck()) {
            long now = System.currentTimeMillis();
            if (now > nextPaddleSoundTime) {
                Sound.getInstance().playSound("paddle_hit");
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

    private void handleBallWallCollisions(Ball ball, List<Wall> walls) {
        for (Wall wall : walls) {
            Collision c = buildCollision(ball, wall);
            if (c == null) continue;
        
            Vector2D v = ball.getVelocity();
            switch (wall.getSide()) {
                case LEFT:
                    ball.setVelocity(new Vector2D(Math.abs(v.x), v.y));
                    EffectRenderer.getInstance().spawnEffect("explosion2", ball.getX(), ball.getY() + ball.getHeight() / 2, 0.3);
                    break;
                case RIGHT:
                    ball.setVelocity(new Vector2D(-Math.abs(v.x), v.y));
                    EffectRenderer.getInstance().spawnEffect("explosion2", ball.getX() + ball.getWidth() / 2, ball.getY() + ball.getHeight() / 2, 0.3);
                    break;
                case TOP:
                    ball.setVelocity(new Vector2D(v.x, Math.abs(v.y)));
                    EffectRenderer.getInstance().spawnEffect("explosion2", ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight() / 2, 0.3);
                    break;
            }
            Sound.getInstance().playSound("wall_hit");
        }
    }

    private void handleBallBrickCollisions(Ball ball, Brick[] bricks, List<PowerUp> powerUps) {
        for (Brick brick : bricks) {
            Collision c = buildCollision(ball, brick);
            if (c != null && !brick.isDestroyed()) {
                String sound = brick.takeDamage();
                if (sound != null) {
                    if ("explosion_hit".equals(sound)) {
                        brick.handleExplosion(brick, bricks); // TODO: add different types of bricks
                    }
                    Sound.getInstance().playSound(sound);
                }
                // bounce
                Vector2D v = ball.getVelocity();
                boolean fromSide = c.getOverlapX() < c.getOverlapY();
                if (fromSide) ball.setVelocity(new Vector2D(-v.x, v.y));
                else ball.setVelocity(new Vector2D(v.x, -v.y));

                if (brick.isDestroyed()) {
                    double centerX = brick.getX() + brick.getWidth() / 2;
                    double centerY = brick.getY() + brick.getWidth() / 2;
                    EffectRenderer.getInstance().spawnEffect("explosion", centerX, centerY, 0.3);

                    if (random.nextInt(100) < 30) {
                        PowerUp newPU = new PowerUp("ACCELERATE");
                        newPU.dropFrom(brick);
                        powerUps.add(newPU);
                    }   
                }
            }
        }
    }
}
