package com.example.demo.model.core;

public class Wall extends GameObject {
    public enum Side { LEFT, RIGHT, TOP }

    private final Side side;

    public Wall(Side side, double x, double y, double width, double height) {
        super("/images/Wall.png", x, y); // optional: could just use a colored rect
        this.width = width;
        this.height = height;
        this.side = side;
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    public Side getSide() {
        return side;
    }
}
