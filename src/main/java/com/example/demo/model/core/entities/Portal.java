package com.example.demo.model.core.entities;

import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.state.PortalData;
import com.example.demo.utils.Timer;

public class Portal extends AnimatedObject<PortalData> {
    private boolean active;
    private final Timer timer;
    private double lifeTime;

    public Portal(String animKey) {
        super(animKey, 0, 0);
        this.active = false;
        this.timer = new Timer();
        this.lifeTime = Double.MAX_VALUE;
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
    public boolean isVisible() {
        return super.isVisible() && isActive();
    }

    public boolean isActive() {
        return this.active;
    }

    public double getLifetime() {
        return this.lifeTime;
    }

    @Override
    public void applyState(PortalData data) {
        super.applyState(data);
        this.lifeTime = data.getLifetime();
        if (data.isActive()) activate(x, y, lifeTime);
        else deactivate();
    }
}
