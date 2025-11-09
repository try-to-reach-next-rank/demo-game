package com.example.demo.model.core.factory;

import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.Wall;

import java.util.ArrayList;
import java.util.List;

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
