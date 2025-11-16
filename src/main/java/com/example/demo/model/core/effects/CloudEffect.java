package com.example.demo.model.core.effects;

import com.example.demo.model.assets.AssetManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class CloudEffect extends VisualEffect {

    private enum Phase {
        ENTER,
        HOLD,
        EXIT
    }

    private Phase currentPhase = Phase.ENTER;
    private double phaseTimer = 0.0;

    private static final double HOLD_DURATION = 2.0;

    private static final double CLOUD_SPEED = 200.0;
    private static final double CLOUD_SCALE = 2.5; // <<< Increase cloud size multiplier

    private Image cloudLeft;
    private Image cloudRight;

    private double leftX;
    private double rightX;
    private double y;

    private double screenWidth;

    public CloudEffect() {
        super("cloud_cover");
        loadImages();
    }

    private void loadImages() {
        AssetManager assets = AssetManager.getInstance();
        cloudLeft = assets.getImage("cloud_left");
        cloudRight = assets.getImage("cloud_right");
    }

    @Override
    public void activate(double screenWidth, double screenHeight, double durationSeconds) {
        this.screenWidth = screenWidth;

        // Scale height so it sits around bottom 20% of screen
        this.y = screenHeight * 0.8;

        double scaledWidth = cloudLeft.getWidth() * CLOUD_SCALE;
        this.leftX = -scaledWidth;
        this.rightX = screenWidth;

        this.phaseTimer = 0.0;
        this.currentPhase = Phase.ENTER;
        this.active = true;
    }

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        phaseTimer += deltaTime;

        switch (currentPhase) {
            case ENTER -> updateEnter(deltaTime);
            case HOLD -> updateHold();
            case EXIT -> updateExit(deltaTime);
        }
    }

    private void updateEnter(double dt) {
        leftX += CLOUD_SPEED * dt;
        rightX -= CLOUD_SPEED * dt;

        double overlap = cloudLeft.getWidth() * CLOUD_SCALE * 0.15;
        double targetLeft = screenWidth / 2.0 - cloudLeft.getWidth() * CLOUD_SCALE + overlap;
        double targetRight = screenWidth / 2.0 - overlap;

        if (leftX >= targetLeft && rightX <= targetRight) {
            currentPhase = Phase.HOLD;
            phaseTimer = 0.0;
        }
    }

    private void updateHold() {
        if (phaseTimer >= HOLD_DURATION) {
            currentPhase = Phase.EXIT;
            phaseTimer = 0.0;
        }
    }

    private void updateExit(double dt) {
        leftX += CLOUD_SPEED * dt;
        rightX -= CLOUD_SPEED * dt;

        if (leftX > screenWidth || rightX + cloudRight.getWidth() * CLOUD_SCALE < 0) {
            deactivate();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;

        double widthL = cloudLeft.getWidth() * CLOUD_SCALE;
        double heightL = cloudLeft.getHeight() * CLOUD_SCALE;
        double widthR = cloudRight.getWidth() * CLOUD_SCALE;
        double heightR = cloudRight.getHeight() * CLOUD_SCALE;

        gc.save();

        gc.drawImage(cloudLeft, leftX, y, widthL, heightL);
        gc.drawImage(cloudRight, rightX, y, widthR, heightR);

        gc.restore();
    }

    @Override
    public CloudEffect clone() {
        return new CloudEffect();
    }
}
