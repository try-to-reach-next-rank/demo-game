package com.example.demo.model.core;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public abstract class GameObject {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Image image;
    protected ImageView imageView;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private boolean visible = true;

    public GameObject(String imagePath, double startX, double startY) {
        setImagePath(imagePath);
        setPosition(startX, startY);
    }

    public GameObject(Image image, double startX, double startY) {
        setImage(image);
        setPosition(startX, startY);
    }

    public void setImagePath(String imagePath) {
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        this.imageView = new ImageView(image);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void setImage(Image image) {
        this.image = image;
        if (this.imageView == null) {
            this.imageView = new ImageView(image);
        } else {
            this.imageView.setImage(image);
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
        applyScale();
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        if (imageView != null) {
            imageView.setX(x);
            imageView.setY(y);
        }
    }

    public void setScale(double sx, double sy) {
        this.scaleX = sx;
        this.scaleY = sy;
        applyScale();
    }

    private void applyScale() {
        if (imageView != null) {
            imageView.setFitWidth(width * scaleX);
            imageView.setFitHeight(height * scaleY);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public Image getImage() { return image; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (imageView != null) imageView.setVisible(visible);
    }

    public Bounds getBounds() {
        return new BoundingBox(x, y, width, height);
    }
}
