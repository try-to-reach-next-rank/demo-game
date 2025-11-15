package com.example.demo.model.state;

import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.state.gameobjectdata.AnimatedObjectData;
import com.example.demo.utils.Vector2D;

public class PortalData extends AnimatedObjectData {
    private boolean active;
    private double lifeTime;
    private double dirX;
    private double dirY;

    public PortalData(Portal portal) {
        super(portal);
        this.active = portal.isActive();
        this.lifeTime = portal.getLifetime();

        Vector2D direction = portal.getDirection();
        if (direction != null) {
            this.dirX = direction.x;
            this.dirY = direction.y;
        } else {
            this.dirX = 0;
            this.dirY = 0;
        }
    }

    public boolean isActive() { return active; }
    public double getLifetime() { return lifeTime; }
    public double getDirX() { return dirX; }
    public double getDirY() { return dirY; }

    public Vector2D getDirection() {
        return new Vector2D(dirX, dirY);
    }
}
