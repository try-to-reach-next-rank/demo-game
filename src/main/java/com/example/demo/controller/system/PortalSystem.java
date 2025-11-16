package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.core.factory.PortalFactory;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class PortalSystem implements Updatable {
    private final PortalFactory portalFactory;

    public PortalSystem(PortalFactory portalFactory) {
        this.portalFactory = portalFactory;
    }

    @Override
    public void update(double deltaTime) {
        portalFactory.getPortals().removeIf(p -> {
            p.updateLifetime(deltaTime);
            p.updateAnimation(deltaTime);
            return !p.isActive();
        });
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
        if (dest == null) {
            System.out.println("No available destination portal for teleportation!");
            return;
        } 

        handleBallPosition(dest, ball); 
    }

    private void handleBallPosition(Portal portal, Ball ball) {
        Vector2D edge = getPortalEdgePosition(portal);

        Vector2D dir  = portal.getDirection().normalize();

        double r = ball.getWidth() / 2.0;

        // Tâm ball thẳng hàng ngoài mép portal
        double cx = edge.x + dir.x * r;
        double cy = edge.y + dir.y * r;

        // Chuyển từ center → top-left
        ball.setPosition(cx - r, cy - r);

        // Hướng bay ra
        ball.setVelocity(dir.rotateRandom(GameVar.SMALL_VELOCITY_OFFSET));
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
