package com.example.demo.model.core.entities;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.gameobjectdata.ImageObjectData;
import com.example.demo.utils.Timer;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class MovedWall extends ImageObject<ImageObjectData> {
    private boolean active;
    private final Timer timer;
    private double lifeTime;
    private double speed = 150.0;
    private Vector2D direction = new Vector2D(1, 0); // mặc định đi ngang

    public MovedWall(String imageKey) {
        super(0, 0);
        this.imageKey = imageKey;
        setImageKey(imageKey);
        this.active = false;
        this.timer = new Timer();
        this.lifeTime = Double.MAX_VALUE;
        setSize(GameVar.HEIGHT_OF_WALLS, GameVar.WIDTH_OF_WALLS);
    }

    public void updateLifetime(double deltaTime) {
        if (!active) return;
        timer.update(deltaTime);
        if (timer.isFinished()) deactivate();
    }

    public void activate(double x, double y, double lifeTime) {
        timer.reset();
        setPosition(x, y);
        this.lifeTime = lifeTime;
        this.timer.start(lifeTime);
        this.active = true;
    }

    public void deactivate() {
        if (!active) return;
        this.active = false;
    }

    public boolean isActive() { return this.active; }

    public Vector2D getDirection() { return direction; }
    public void setDirection(Vector2D dir) { this.direction = dir.normalize(); }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    @Override
    public boolean isVisible() { return super.isVisible() && isActive(); }

    // Default do nothing
    @Override
    public void applyState(ImageObjectData data) {
        super.applyState(data);
    }
}
