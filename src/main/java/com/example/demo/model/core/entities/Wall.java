package com.example.demo.model.core.entities;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.WallData;

public class Wall extends ImageObject<WallData> {
    public enum Side { LEFT, RIGHT, TOP }
    private final Side side;

    public Wall(Side side, double x, double y, double width, double height) {
        super(x, y);
        imageKey = side == Side.TOP ? "wall_top" : "wall_side";
        setImageKey(imageKey);
        this.side = side;
        if (side == Side.TOP) {
            setSize(height, width);
        } else {
            setSize(width, height);
        }
    }

    public Side getSide() {
        return side;
    }

    @Override
    public void applyState(WallData data) {
        super.applyState(data);
    }
}
