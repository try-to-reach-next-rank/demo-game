package com.example.demo.controller.core;

import java.util.List;

import com.example.demo.controller.system.BallSystem;
import com.example.demo.controller.system.BrickSystem;
import com.example.demo.controller.system.PortalSystem;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.controller.system.SystemManager;
import com.example.demo.model.core.entities.*;
import com.example.demo.model.core.gameobjects.GameObject;

public class CollisionControllerPhuc {
    private List<GameObject> objects;
    private SystemManager systemManager;

    public CollisionControllerPhuc(List<GameObject> objects, SystemManager systemManager) {
        this.objects = objects;
        this.systemManager = systemManager;
    }

    public void control() {
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                GameObject a = objects.get(i);
                GameObject b = objects.get(j);

                if (a.getBounds().intersects(b.getBounds())) {
                    handleCollision(a, b);
                }
            }
        }
    }

    private void handleCollision(GameObject a, GameObject b) {
        BallSystem ballSystem = systemManager.get(BallSystem.class);
        BrickSystem brickSystem = systemManager.get(BrickSystem.class);
        PowerUpSystem powerUpSystem = systemManager.get(PowerUpSystem.class);
        PortalSystem portalSystem = systemManager.get(PortalSystem.class);

        // BALL - PADDLE
        if (isPair(a, b, Ball.class, Paddle.class)) {
            Ball ball = (a instanceof Ball) ? (Ball) a : (Ball) b;
            Paddle paddle = (a instanceof Paddle) ? (Paddle) a : (Paddle) b;
            ballSystem.handleCollision(ball, paddle);
            return;
        }

        // BALL - WALL
        if (isPair(a, b, Ball.class, Wall.class)) {
            Ball ball = (a instanceof Ball) ? (Ball) a : (Ball) b;
            Wall wall = (a instanceof Wall) ? (Wall) a : (Wall) b;
            ballSystem.handleCollision(ball, wall);
            return;
        }

        // BALL - BRICK
        if (isPair(a, b, Ball.class, Brick.class)) {
            Ball ball = (a instanceof Ball) ? (Ball) a : (Ball) b;
            Brick brick = (a instanceof Brick) ? (Brick) a : (Brick) b;
            brickSystem.handleCollision(brick, ball);
            ballSystem.handleCollision(ball, brick);
            return;
        }

        // BALL - PORTAL
        if (isPair(a, b, Ball.class, Portal.class)) {
            Ball ball = (a instanceof Ball) ? (Ball) a : (Ball) b;
            Portal portal = (a instanceof Portal) ? (Portal) a : (Portal) b;
            portalSystem.handleCollision(portal, ball);
            return;
        }

        // PADDLE - POWERUP
        if (isPair(a, b, Paddle.class, PowerUp.class)) {
            Paddle paddle = (a instanceof Paddle) ? (Paddle) a : (Paddle) b;
            PowerUp powerUp = (a instanceof PowerUp) ? (PowerUp) a : (PowerUp) b;
            powerUpSystem.handleCollision(powerUp, paddle);
        }
    }

    private boolean isPair(GameObject a, GameObject b, Class<?> class1, Class<?> class2) {
        return (class1.isInstance(a) && class2.isInstance(b)) 
            || (class1.isInstance(b) && class2.isInstance(a));
    }
}
