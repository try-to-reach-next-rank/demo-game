package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.core.factory.PortalFactory;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.Vector2D;

public class PortalSystem implements Updatable {
    private final PortalFactory portalFactory;

    public PortalSystem(PortalFactory portalFactory) {
        this.portalFactory = portalFactory;
        // portalFactory.createRandom("portal");
        // portalFactory.createRandom("portal");
        // portalFactory.createRandom("portal");
        // portalFactory.createRandom("portal");
        portalFactory.create("portal", 100, 200, 100.0);
        portalFactory.create("portal", 500, 200, 100.0);
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
        // if (!portal.canTeleport()) {
        //     System.out.println("HEHEHEHEHE");
        //     return;
        // }

        System.out.println("BALL: x = " + ball.getX() +  " y = " + ball.getY());

        Portal dest = portalFactory.getRandomDestination(portal);
        if (dest == null) {
            System.out.println("No available destination portal for teleportation!");
            return;
        } 

        // Vector hướng của portal
        Vector2D dir = dest.getDirection().normalize();

        // Tính center portal (bounding box) trước
        double centerX = dest.getX() + dest.getWidth() / 2.0;
        double centerY = dest.getY() + dest.getHeight() / 2.0;

        // Khoảng cách offset để ra ngoài portal
        double offsetDistance = Math.max(dest.getWidth(), dest.getHeight()) / 2.0 + 0.1; // +5 pixels tránh collision

        // Vị trí ball xuất hiện
        double spawnX = centerX + dir.x * offsetDistance;
        double spawnY = centerY + dir.y * offsetDistance;

        ball.setPosition(spawnX, spawnY);

        // Set ball velocity along portal direction
        ball.setVelocity(dest.getDirection().multiply(ball.getBaseSpeed()));

        portal.setNextTeleportTime(System.currentTimeMillis() + 1000);
    }
}
