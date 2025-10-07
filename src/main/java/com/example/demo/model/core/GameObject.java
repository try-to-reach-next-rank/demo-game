package com.example.demo.model.core;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public abstract class GameObject {

    protected double        x;
    protected double        y;
    protected double        width;
    protected double        height;
    protected Image         image;
    protected ImageView     imageView;
    private double          scaleX;
    private double          scaleY;

    public GameObject(String imagePath, double startX, double startY) { // this constructor uses imagepath to create new image
        setImagePath(imagePath);
        setPosition(startX, startY);
    }

    public GameObject(Image image, double startX, double startY) { // this constructor uses preloaded images
        setImage(image);
        setPosition(startX, startY);
    }

    public void setImagePath(String imagePath){
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        this.imageView = new ImageView(image);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    // Sets the image from a preloaded Image object
    public void setImage(Image image){ //set image
        this.image = image;

        // Creating imageview if not yet created
        if (this.imageView == null) {
            this.imageView = new ImageView(image);
        } else {
            this.imageView.setImage(image);
        }

        this.width = image.getWidth();
        this.height = image.getHeight();

        if (this.scaleX != 0 && this.scaleY != 0) {
            setScale(this.scaleX, this.scaleY);
        }
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        imageView.setX(x);
        imageView.setY(y);
    }

    public void setScale(double sx, double sy) {
        this.scaleX = sx;
        this.scaleY = sy;
        imageView.setFitWidth(width * sx);
        imageView.setFitHeight(height * sy);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public Image getImage() { return image; }

    public Bounds getBounds() {
        return imageView.getBoundsInParent();
    }

    public boolean checkCollision(GameObject other) {
        if(this == other) return false; // ignore self
        return this.getBounds().intersects(other.getBounds());
    }

    public void setY(double v) {
        y = v;
    }
}
