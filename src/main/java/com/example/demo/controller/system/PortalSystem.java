package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.core.factory.PortalFactory;
import com.example.demo.model.core.gameobjects.GameObject;

public class PortalSystem implements Updatable {
    private final PortalFactory portalFactory;

    public PortalSystem(PortalFactory portalFactory) {
        this.portalFactory = portalFactory;
        portalFactory.createRandom("powerup_accelerate");
        portalFactory.createRandom("powerup_accelerate");
        portalFactory.createRandom("powerup_accelerate");
        portalFactory.createRandom("powerup_accelerate");
    }

    @Override
    public void update(double deltaTime) {
        for (Portal p : portalFactory.getPortals()) {
            p.updateLifetime(deltaTime);
            p.updateAnimation(deltaTime);
        }
    }

    public void handleCollision(Portal portal, GameObject obj) {
        if (obj instanceof Ball ball) {
            handleBallCollision(portal, ball);
        }
    }

    @Override
    public void clear() {}

    private void handleBallCollision(Portal portal, Ball ball) {
        Portal dest = portalFactory.getRandomDestination(portal);
        ball.setPosition(dest.getX(), dest.getY());
    }
}
