package com.example.demo.model.core.gameobjects;

import com.example.demo.model.state.gameobjectdata.GameObjectData;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public abstract class GameObject<T extends GameObjectData> {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected boolean visible = true;

    protected double baseWidth;
    protected double baseHeight;

    protected double scaleX = 1.0;
    protected double scaleY = 1.0;
    private Bounds cachedBounds;
    public boolean boundsDirty = true;

    public GameObject(double startX, double startY) {
        setPosition(startX, startY);
    }

    public void setPosition(double x, double y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            boundsDirty = true;
        }
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
        double centerX = x + width / 2;
        double centerY = y + height / 2;

        this.width = baseWidth * scaleX;
        this.height = baseHeight * scaleY;

        this.x = centerX - width / 2;
        this.y = centerY - height / 2;
        boundsDirty = true;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) {
        if (this.x != x) {
            this.x = x;
            boundsDirty = true;
        }
    }

    public void setY(double y) {
        if (this.y != y) {
            this.y = y;
            boundsDirty = true;
        }
    }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public Bounds getBounds() {
        if (boundsDirty) {
            cachedBounds = new BoundingBox(x, y, width, height);
            boundsDirty = false;
        }
        return cachedBounds;
    }

    public abstract void applyState(T GameObjectData);
}