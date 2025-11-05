package com.example.demo.model.core;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.WallData;

public class Wall extends ImageObject<WallData> {

    @Override
    public void applyState(WallData ImageObjectData) {}

    public enum Side { LEFT, RIGHT, TOP }
    private final Side side;

    public Wall(Side side, double x, double y, double width, double height) {
        super(side == Side.TOP ? "wall_top" : "wall_side", x, y);
        this.side = side;
        if (side == Side.TOP) {
            setSize(height, width);
        } else {
            setSize(width, height);
        }
    }

    private void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Side getSide() {
        return side;
    }
}
