package com.example.demo.utils;

import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.core.bricks.NormalBrick;
import com.example.demo.model.core.bricks.SteelBrick;
import com.example.demo.model.state.BrickData;

public class BrickFactoryUtil {
    public static Brick createBrickFromData(BrickData data) {
        Brick brick;

        switch (data.getType()) {
            case "STEEL":
                brick = new SteelBrick(data.getX(), data.getY(), data.getWidth(), data.getHeight());
                break;

            case "NORMAL":
            default:
                // Use health and imageKey from data
                brick = new NormalBrick(
                        data.getInitialHealth(),
                        data.getImageKey(),
                        data.getX(),
                        data.getY(),
                        data.getWidth(),
                        data.getHeight()
                );
                break;
        }

        brick.applyState(data); // restore current health, destroyed flag
        return brick;
    }
}
