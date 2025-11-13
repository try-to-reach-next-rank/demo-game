package com.example.demo.model.core.factory;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.Wall;

/**
 * Not layout-based: which means no Brick here
 */
public class EntityFactory {
    public static Paddle createPaddle() {
        return new Paddle();
    }

    public static Ball createBall(Paddle paddle) {
        return new Ball(paddle);
    }
}
