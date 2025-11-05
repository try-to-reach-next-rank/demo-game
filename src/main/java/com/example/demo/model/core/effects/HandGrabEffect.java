package com.example.demo.model.core.effects;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.VisualEffect;
import com.example.demo.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class HandGrabEffect extends VisualEffect {

    private enum Phase {
        APPROACH,
        GRAB,
        SHAKE,
        WINDUP,
        PUNCH,
        RETREAT
    }

    private Phase currentPhase = Phase.APPROACH;
    private double phaseTimer = 0.0;

    // Hand state
    private double handX;
    private double handY;
    private double handRotation = 0.0;
    private double handScale = 1.0;
    private boolean handClosed = false;

    private Ball ball;
    private double originalBallX;
    private double originalBallY;

    private int shakeCount = 0;
    private static final int MAX_SHAKES = 5;
    private double shakeOffsetX = 0.0;
    private double shakeOffsetY = 0.0;
    private double grabY;
    private double punchY;

    private static final double APPROACH_DURATION = 0.5;
    private static final double GRAB_DURATION = 0.3;
    private static final double PUNCH_DURATION = 0.15;
    private static final double RETREAT_DURATION = 0.4;
    private static final double WINDUP_DURATION = 0.08;
    private double windupOffset = 0.0;

    private static final double SHAKE_INTENSITY = 8.0;
    private static final double HAND_SIZE = 80.0;

    private Vector2D throwVelocity;

    private Image handOpenImage;
    private Image handClosedImage;

    public HandGrabEffect() {
        super("hand_grab");
        loadHandImages();
    }

    private void loadHandImages() {
        handOpenImage = AssetManager.getInstance().getImage("hand_open");
        handClosedImage = AssetManager.getInstance().getImage("hand_closed");
    }

    public void activateOnBall(Ball ball) {
        this.ball = ball;
        this.originalBallX = ball.getX();
        this.originalBallY = ball.getY();

        ball.setVelocity(0, 0);
        ball.setHeldByEffect(true);

        // Hand starts from top of screen, slightly offset
        this.handX = ball.getX() + ball.getWidth() / 2;
        this.handY = -HAND_SIZE;

        // Calculate random throw velocity
        double angle = Math.random() * Math.PI; // 0 to 180 degrees (upward)
        double speed = 300 + Math.random() * 200; // Random speed
        throwVelocity = new Vector2D(
                Math.cos(angle) * speed,
                -Math.abs(Math.sin(angle)) * speed // Always upward
        ).normalize();

        currentPhase = Phase.APPROACH;
        phaseTimer = 0.0;
        shakeCount = 0;
        handClosed = false;
        active = true;
    }

    @Override
    public void update(double deltaTime) {
        if (!active || ball == null) return;

        phaseTimer += deltaTime;

        switch (currentPhase) {
            case APPROACH -> updateApproach(deltaTime);
            case GRAB -> updateGrab(deltaTime);
            case SHAKE -> updateShake(deltaTime);
            case WINDUP -> updateWindup(deltaTime);
            case PUNCH -> updatePunch(deltaTime);
            case RETREAT -> updateRetreat(deltaTime);
        }
    }

    private void updateApproach(double deltaTime) {
        // Hand moves down toward ball
        double targetY = ball.getY() - HAND_SIZE * 0.3;
        double progress = phaseTimer / APPROACH_DURATION;

        if (progress >= 1.0) {
            handY = targetY;
            currentPhase = Phase.GRAB;
            phaseTimer = 0.0;
        } else {
            // Smooth easing
            double eased = easeOutCubic(progress);
            handY = -HAND_SIZE + (targetY + HAND_SIZE) * eased;
        }
    }

    private void updateGrab(double deltaTime) {
        // Hand closes around ball
        double progress = phaseTimer / GRAB_DURATION;

        if (progress >= 1.0) {
            handClosed = true;
            currentPhase = Phase.SHAKE;
            grabY = handY;
            punchY = grabY + 100;
            phaseTimer = 0.0;
        } else {
            handClosed = progress > 0.5;
        }
    }

    private void updateShake(double deltaTime) {
        double shakeFrequency = 30.0; // Hz
        double t = phaseTimer * shakeFrequency * Math.PI * 2;

        shakeOffsetX = Math.sin(t) * SHAKE_INTENSITY;
        shakeOffsetY = Math.cos(t * 1.3) * SHAKE_INTENSITY * 0.5;

        if (phaseTimer >= 0.08) { // every 0.08s = about 12Hz shake
            phaseTimer = 0;
            shakeCount++;
        }

        if (shakeCount >= MAX_SHAKES) {
            currentPhase = Phase.WINDUP;
            phaseTimer = 0.0;
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        // Update ball position
        if (ball != null) {
            ball.setPosition(
                    handX - ball.getWidth() / 2 + shakeOffsetX,
                    handY + HAND_SIZE * 0.4 + shakeOffsetY
            );
        }
    }

    private void updatePunch(double deltaTime) {
        double progress = Math.min(phaseTimer / PUNCH_DURATION, 1.0);
        double eased = easeOutCubic(progress);

        handY = grabY - windupOffset + (punchY - (grabY - windupOffset)) * eased;
        handRotation = eased * 20;

        // Punch timing
        if (progress >= 1.0) {
            if (ball != null && ball.isHeldByEffect()) {
                double angle = Math.toRadians(45 + Math.random() * 90);
                double speed = 450 + Math.random() * 300;
                Vector2D punchVelocity = new Vector2D(
                        Math.cos(angle) * speed,
                        -Math.abs(Math.sin(angle)) * speed
                );
                ball.setStuck(false);
                ball.setVelocity(punchVelocity);
            }

            // transition to retreat
            currentPhase = Phase.RETREAT;
            phaseTimer = 0.0;
            handClosed = false;
        }
    }

    private void updateWindup(double deltaTime) {
        double progress = Math.min(phaseTimer / WINDUP_DURATION, 1.0);
        double eased = easeOutCubic(progress);

        windupOffset = eased * 50;
        handY = grabY - windupOffset;   // windup goes slightly up
        handRotation = -eased * 20;

        if (phaseTimer >= WINDUP_DURATION) {
            currentPhase = Phase.PUNCH;
            phaseTimer = 0.0;
        }
    }

    private void updateRetreat(double deltaTime) {
        // Hand exits screen
        double progress = phaseTimer / RETREAT_DURATION;

        if (progress >= 1.0) {
            deactivate();
        } else {
            // Move up and fade
            double eased = easeInCubic(progress);
            handY = handY - eased * 300 * deltaTime;
            handRotation = 45.0 - eased * 45.0;
            handScale = 1.3 - eased * 0.3;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;

        gc.save();

        if (currentPhase == Phase.RETREAT) {
            double alpha = 1.0 - (phaseTimer / RETREAT_DURATION);
            gc.setGlobalAlpha(alpha);
        }

        gc.translate(handX, handY + HAND_SIZE * 0.7);
        gc.rotate(handRotation);
        gc.scale(handScale, handScale);

        // Draw hand
        Image handImage = handClosed ? handClosedImage : handOpenImage;

        if (handImage != null) {
            gc.drawImage(
                    handImage,
                    -HAND_SIZE / 2,
                    -HAND_SIZE / 2,
                    HAND_SIZE,
                    HAND_SIZE
            );
        }

        gc.restore();
    }

    @Override
    public void activate(double x, double y, double durationSeconds) {
        // Not used for this effect - use activateOnBall instead
        this.x = x;
        this.y = y;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public HandGrabEffect clone() {
        return new HandGrabEffect();
    }

    // Easing functions
    private double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private double easeInCubic(double t) {
        return t * t * t;
    }

    // Getters
    public Ball getBall() {
        return ball;
    }
}
