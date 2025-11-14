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
        //  if (!portal.canTeleport()) {
        //      System.out.println("HEHEHEHEHE");
        //      return;
        //  }

        Portal dest = portalFactory.getRandomDestination(portal);
        if (dest == null) {
            System.out.println("No available destination portal for teleportation!");
            return;
        } 

        handleBallPosition(portal, ball); 

        // portal.setNextTeleportTime(System.currentTimeMillis() + 1000);
    }

    private void handleBallPosition(Portal portal, Ball ball) {
        // TODO: Phuc is fully wrong, please help
        // This is a white flag from phuc

        Vector2D edge = getPortalEdgePosition(portal);
        Vector2D dir  = portal.getDirection().normalize();

        double r = ball.getWidth() / 2;
        double offset = 2.0;

        // Tâm ball thẳng hàng ngoài mép portal
        double cx = edge.x + dir.x * r;
        double cy = edge.y + dir.y * r;

        // Chuyển từ center → top-left
        ball.setPosition(cx - r, cy - r);

        // Hướng bay ra
        ball.setVelocity(dir.multiply(ball.getBaseSpeed()));
    }


    private Vector2D getPortalEdgePosition(Portal portal) {
        double cx = portal.getX() + portal.getWidth() / 2.0;
        double cy = portal.getY() + portal.getHeight() / 2.0;

        Vector2D dir = portal.getDirection().normalize();

        double halfW = portal.getWidth() / 2.0;
        double halfH = portal.getHeight() / 2.0;

        // Khoảng cách từ tâm portal đến mép theo hướng dir
        double edgeOffset = Math.abs(dir.x) * halfW + Math.abs(dir.y) * halfH;

        double ex = cx + dir.x * edgeOffset;
        double ey = cy + dir.y * edgeOffset;

        return new Vector2D(ex, ey);
    }
}
