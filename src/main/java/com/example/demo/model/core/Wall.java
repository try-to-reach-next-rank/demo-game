package com.example.demo.model.core;

public class Wall extends GameObject {
    public enum Side { LEFT, RIGHT, TOP }
    private final Side side;

    public Wall(Side side, double x, double y, double width, double height) {
        super("wall", x, y);
        this.side = side;
        setSize(width, height);
    }

    private void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Side getSide() {
        return side;
    }
}
