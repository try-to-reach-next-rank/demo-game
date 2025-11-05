package com.example.demo.model.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.Wall;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

import java.util.ArrayList;
import java.util.List;

public class BallSystem implements Updatable {
    private final Ball ball;
    private final List<Ball> balls = new ArrayList<>();
    private final Paddle paddle;

    public BallSystem(Ball ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    @Override
    public void update(double deltaTime) {
        if (ball.isStopTime()) {
            ball.setElapsedTime(ball.getElapsedTime() + deltaTime);
            if (ball.getElapsedTime() >= GameVar.BALL_ELAPSED_TIME) {
                ball.setElapsedTime(0);
                double angle = GameRandom.nextDouble() * 2 * Math.PI;
                double vx = Math.cos(angle);
                double vy = Math.sin(angle);
                if (ball.getY() >= paddle.getY() - GameVar.BALL_PADDLE_OFFSET_Y) {
                    vy = -Math.abs(vy);
                }
                ball.setVelocity(vx, vy);
            }
        }


        if (ball.isStuck()) {
            // Keep the ball above the paddle
            ball.alignWithPaddle(GameVar.BALL_ALIGN_WITH_PADDLE_OFFSET_Y, GameVar.BALL_ALIGN_WITH_PADDLE_LERPFACTOR);
            return;
        }

        double currentSpeed = ball.getBaseSpeed();
        if (ball.isAccelerated()) currentSpeed *= GameVar.BALL_ACCELERATION_FACTOR;

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
        double angle = Math.toRadians(GameVar.BALL_BOUNCE_ANGLE_LEFT * (1 - hitPos) + GameVar.BALL_BOUNCE_ANGLE_RIGHT * hitPos);
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
}
