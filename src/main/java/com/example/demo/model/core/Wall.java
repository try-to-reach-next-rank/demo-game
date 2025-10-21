package com.example.demo.model.core;

public class Wall extends GameObject {
    public enum Side { LEFT, RIGHT, TOP }
    private final Side side;

    public Wall(Side side, double x, double y, double width, double height) {
        super("/images/Wall.png", x, y);
        this.side = side;
        setSize(width, height);
    }

    private void setSize(double width, double height) {
        this.width = width;
        this.height = height;

        if (imageView != null) {
            imageView.setX(x);
            imageView.setY(y);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setVisible(true);
            imageView.autosize(); // force update of bounds
        }
    }

    public Side getSide() {
        return side;
    }
}
