package com.example.demo.core;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class GameObject {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Image image;
    protected ImageView imageView;

    public GameObject(String imagePath, double startX, double startY) {
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        this.imageView = new ImageView(image);
        this.width = image.getWidth();
        this.height = image.getHeight();
        setPosition(startX, startY);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        imageView.setX(x);
        imageView.setY(y);
    }

    public void setX(double x) {
        this.x = x;
        imageView.setX(x);
    }

    public void setY(double y) {
        this.y = y;
        imageView.setY(y);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public Image getImage() { return image; }
    public ImageView getImageView() { return imageView; }

    public Bounds getBounds() {
        return imageView.getBoundsInParent();
    }
}
