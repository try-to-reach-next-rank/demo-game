package com.example.demo.model.core.entities;

import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.state.PortalData;
import com.example.demo.utils.Timer;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class Portal extends AnimatedObject<PortalData> {
    private boolean active;
    private final Timer timer;
    // TODO:DELETE
    private long nextTeleportTime = 0; // millisec
    private double lifeTime;
    private Vector2D direction;

    public Portal(String animKey) {
        super(0, 0);
        setAnimationKey(animKey);
        this.active = false;
        this.timer = new Timer();
        this.lifeTime = Double.MAX_VALUE;
        this.direction = new Vector2D(GameVar.PORTAL_INIT_DIR_X, GameVar.PORTAL_INIT_DIR_Y);
    }

    public void updateLifetime(double deltaTime) {
        if (!active) return;
        timer.update(deltaTime);
        if (timer.isFinished()) deactivate();
    }

    public void activate(double x, double y, double lifeTime) {
        timer.reset();
        this.x = x;
        this.y = y;
        this.lifeTime = lifeTime;
        this.timer.start(lifeTime);
        this.active = true;
    }

    public void deactivate() {
        if (!active) return;
        this.active = false;
    }

    @Override
    public boolean isVisible() { return super.isVisible() && isActive(); }

    @Override
    public double getRotation() { return Math.toDegrees(Math.atan2(direction.y, direction.x)); }

    public boolean isActive() { return this.active; }

    public double getLifetime() { return this.lifeTime; }

    public Vector2D getDirection() { return direction; }
    public void setDirection(Vector2D direction) { this.direction = direction.normalize(); }

    // TODO:DELETE
    public long getNextTeleportTime() { return nextTeleportTime; }
    public void setNextTeleportTime(long time) { this.nextTeleportTime = time; }

    public boolean canTeleport() {
        return System.currentTimeMillis() >= nextTeleportTime;
    }

    @Override
    public void applyState(PortalData data) {
        super.applyState(data);
        this.lifeTime = data.getLifetime();
        if (data.isActive()) activate(x, y, lifeTime);
        else deactivate();
    }
}
