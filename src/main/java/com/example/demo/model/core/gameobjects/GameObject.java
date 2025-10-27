package com.example.demo.model.core.gameobjects;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected boolean visible = true;

    protected double baseWidth;
    protected double baseHeight;

    protected double scaleX = 1.0;
    protected double scaleY = 1.0;

    public GameObject(double startX, double startY) {
        setPosition(startX, startY);
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
    protected void applyScale() {
        this.width = baseWidth * scaleX;
        this.height = baseHeight * scaleY;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public Bounds getBounds() {
        return new BoundingBox(x, y, width, height);
    }

    public abstract boolean isStatic();
}