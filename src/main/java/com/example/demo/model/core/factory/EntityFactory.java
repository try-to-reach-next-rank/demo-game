package com.example.demo.model.core.factory;

import com.example.demo.model.core.entities.*;

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
        return new Ball();
    }
}
