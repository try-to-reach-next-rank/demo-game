package com.example.demo.controller.system;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.*;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GameVar;

public class CollisionSystem implements Updatable {
    // TODO: MAKE A MAP
    private List<GameObject> objects;
    private BallSystem ballSystem;
    private BrickSystem brickSystem;
    private PowerUpSystem powerUpSystem;
    private PortalSystem portalSystem;
    private final GameWorld world;
    private final SystemManager systemManager;

    public CollisionSystem(GameWorld world, SystemManager systemManager) {
        this.objects = world.getObjects();
        this.world = world;
        this.systemManager = systemManager;
        this.ballSystem = systemManager.get(BallSystem.class);
        this.brickSystem = systemManager.get(BrickSystem.class);
        this.powerUpSystem = systemManager.get(PowerUpSystem.class);
        this.portalSystem = systemManager.get(PortalSystem.class);
    }

    @Override
    public void update(double deltaTime) {
        // TODO: REPLACE THIS
        this.objects = world.getObjects();

        // Group objects by type once
        List<Ball> balls = filterObjects(Ball.class);
        List<Paddle> paddles = filterObjects(Paddle.class);
        List<Brick> bricks = filterObjects(Brick.class);
        List<PowerUp> powerUps = filterObjects(PowerUp.class);
        List<Wall> walls = filterObjects(Wall.class);
        List<Portal> portals = filterObjects(Portal.class);

        // Handle ball collisions
        for (Ball ball : balls) {
            if (ball.isStuck()) {
                continue; // skip stuck balls
            }

            boolean belowFloor = ball.getBounds().getMaxY() > GameVar.MAP_MAX_Y;
            if (belowFloor) {
                Sound.getInstance().playSound("game_over");
                ballSystem.resetBall(ball);
                powerUpSystem.reset();
            }

            paddles.forEach(paddle -> handleCollision(ball, paddle));
            bricks.forEach(brick -> handleCollision(ball, brick));
            walls.forEach(wall -> handleCollision(ball, wall));
            portals.forEach(portal -> handleCollision(ball, portal));
        }

        // Handle power-up collisions
        powerUps.forEach(pu -> paddles.forEach(paddle -> handleCollision(pu, paddle)));
    }

    @Override
    public void clear() {
        // objects.clear();
    }
    
    /**
     * Utility method to filter objects by type
     */
    private <T extends GameObject> List<T> filterObjects(Class<T> clazz) {
        return objects.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    /**
     * Handles collisions between two objects using the appropriate system.
     */
    private void handleCollision(GameObject a, GameObject b) {
        // No collision
        if (!a.getBounds().intersects(b.getBounds())) return;

        // LOG
        // System.out.println("[COLLISIONPHUC] " + a.getClass() + " & " + b.getClass());

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
