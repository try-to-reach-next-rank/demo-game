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
    public GameObject(double startX, double startY) {
        setPosition(startX, startY);
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
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { if (this.x != x) this.x = x; }
    public void setY(double y) { if (this.y != y) this.y = y; }
    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }
    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    // For coreview
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public double getRotation() { return 0; } // Default no rotation

    public Bounds getBounds() {
        return new BoundingBox(x, y, width, height);
    }

    public void applyState(T data) {
        if (data == null) return;
        this.setPosition(data.getX(), data.getY());
        this.setSize(data.getWidth(), data.getHeight());
        this.setVisible(data.isVisible());
    }
}