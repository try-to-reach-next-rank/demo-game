package com.example.demo.model.core.factory;

import com.example.demo.model.core.entities.*;

/**
 * Not layout-based: which means no Brick here
 */
public class EntityFactory {
    public static Paddle createPaddle() {
        return new Paddle();
    }

    public static Ball createBall(Paddle paddle) {
        Ball b = new Ball();
        b.setStuckPaddle(paddle);
        return b;
    }

    public static PortalFactory createPortalFactory() {
        return new PortalFactory();
    }

    public static MovedWallFactory createMovedWallFactory() {
        return new MovedWallFactory();
    }
}
