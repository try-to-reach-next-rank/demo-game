package com.example.demo.model.core.effects;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.VisualEffect;
import com.example.demo.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class HandGrabEffect extends VisualEffect {

    private enum Phase {
        RISE,       // move up from bottom toward the ball
        PUNCH,      // hit the ball and release it
        RETREAT     // move back down / disappear
    }

    private Phase currentPhase = Phase.RISE;
    private double phaseTimer = 0.0;

    private double handX;
    private double handY;
    private double screenShake = 0.0;

    private static final double HAND_SIZE = 120.0;
    private static final double RISE_DURATION = 0.4;
    private static final double PUNCH_DURATION = 0.2;
    private static final double RETREAT_DURATION = 0.4;

    private Ball ball;
    private Image handImage;
    private boolean hasPunched = false;
    private double punchFlashAlpha = 0.0;

    public HandGrabEffect() {
        super("hand_punch");
        loadHandImage();
    }

    private void loadHandImage() {
        handImage = AssetManager.getInstance().getImage("hand_open");
    }

    public void activateOnBall(Ball ball) {
        this.ball = ball;
        this.handX = ball.getX() + ball.getWidth() / 2;
        this.handY = ball.getY() + 500;
        this.phaseTimer = 0.0;
        this.currentPhase = Phase.RISE;
        this.hasPunched = false;
        this.active = true;

        ball.setVelocity(0, 0);
        ball.setHeldByEffect(true);
    }

    @Override
    public void update(double deltaTime) {
        if (!active || ball == null) return;
        phaseTimer += deltaTime;

        switch (currentPhase) {
            case RISE -> updateRise();
            case PUNCH -> updatePunch();
            case RETREAT -> updateRetreat();
        }

        if (punchFlashAlpha > 0) {
            punchFlashAlpha = Math.max(0, punchFlashAlpha - deltaTime * 3.0);
        }

        screenShake *= 0.9;
    }

    private void updateRise() {
        double targetY = ball.getY() + ball.getHeight() / 2;
        double progress = Math.min(phaseTimer / RISE_DURATION, 1.0);
        double eased = easeOutCubic(progress);
        handY = (ball.getY() + 300) - 300 * eased;

        if (progress >= 1.0) {
            handY = targetY;
            currentPhase = Phase.PUNCH;
            phaseTimer = 0.0;
        }
    }

    private void updatePunch() {
        if (!hasPunched) {
            hasPunched = true;
            punchFlashAlpha = 0.6;
            screenShake = 15;

            // Launch the ball at a random direction
            int dir = (int) (Math.random() * 3);
            double angle = switch (dir) {
                case 0 -> Math.toRadians(60);   // up-right
                case 1 -> Math.toRadians(90);   // straight up
                case 2 -> Math.toRadians(120);  // up-left
                default -> Math.toRadians(90);
            };

            double speed = 700 + Math.random() * 400;
            Vector2D punchVelocity = new Vector2D(
                    Math.cos(angle) * speed,
                    -Math.abs(Math.sin(angle)) * speed
            );

            ball.setHeldByEffect(false);
            ball.setVelocity(punchVelocity);
        }

        if (phaseTimer >= PUNCH_DURATION) {
            currentPhase = Phase.RETREAT;
            phaseTimer = 0.0;
        }
    }

    private void updateRetreat() {
        double progress = Math.min(phaseTimer / RETREAT_DURATION, 1.0);
        double eased = easeInCubic(progress);
        handY += eased * 400 * 0.016; // move downward

        if (progress >= 1.0) {
            deactivate();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active || handImage == null) return;
        gc.save();

        double shakeX = (Math.random() - 0.5) * screenShake;
        double shakeY = (Math.random() - 0.5) * screenShake;
        gc.translate(handX + shakeX, handY + shakeY);
        gc.rotate(90);
        gc.drawImage(handImage, -HAND_SIZE / 2 + 40, -HAND_SIZE / 2, HAND_SIZE, HAND_SIZE);
        gc.restore();

        if (punchFlashAlpha > 0) {
            gc.save();
            gc.setGlobalAlpha(punchFlashAlpha);
            gc.setFill(Color.color(1, 1, 1, punchFlashAlpha * 0.5));
            gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
            gc.restore();
        }
    }

    @Override
    public void activate(double x, double y, double durationSeconds) {
        // Not used
    }

    @Override
    public HandGrabEffect clone() {
        return new HandGrabEffect();
    }

    private double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private double easeInCubic(double t) {
        return t * t * t;
    }
}