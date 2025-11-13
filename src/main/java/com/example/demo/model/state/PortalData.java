package com.example.demo.model.state;

import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.state.gameobjectdata.AnimatedObjectData;

public class PortalData extends AnimatedObjectData {
    private boolean active;
    private double lifeTime;

    public PortalData(Portal portal) {
        super(portal);
        this.active = portal.isActive();
        this.lifeTime = portal.getLifetime();
    }

    public boolean isActive() { return active; }
    public double getLifetime() { return lifeTime; }
}
