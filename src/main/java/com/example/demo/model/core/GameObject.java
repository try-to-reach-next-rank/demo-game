package com.example.demo.model.core;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

import com.example.demo.controller.AssetManager;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected String imageKey;
    protected Image  image;
    protected boolean visible = true;

    private double baseWidth;
    private double baseHeight;

    private double scaleX = 1.0;
    private double scaleY = 1.0;

    public GameObject(String imageKey, double startX, double startY) {
        setImageKey(imageKey);
        setPosition(startX, startY);
    }

    public void setImageKey(String imageKey) {
        this.image = AssetManager.getInstance().getImage(imageKey);
        if (this.image == null) {
            System.err.println("Image not found for key: " + imageKey);
            return;
        }

        this.imageKey = imageKey;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.baseWidth = width;
        this.baseHeight = height;
        applyScale();
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setScale(double sx, double sy) {
        this.scaleX = sx;
        this.scaleY = sy;
        applyScale();
    }

    /**
     * Reset về kích thước gốc.
     */
    public void resetScale() {
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        applyScale();
    }

    /**
     * Áp dụng scale: cập nhật kích thước vật lý và hiển thị.
     */
    private void applyScale() {
        this.width = baseWidth * scaleX;
        this.height = baseHeight * scaleY;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public Image getImage() { return image; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Bounds getBounds() {
        return new BoundingBox(x, y, width, height);
    }
}
