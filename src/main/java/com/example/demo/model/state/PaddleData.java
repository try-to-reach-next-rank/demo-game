package com.example.demo.model.state;

import com.example.demo.model.core.Paddle;

public class PaddleData {
    private double x;
    private double y;

    public PaddleData() {}

    public PaddleData(Paddle paddle) {
        this.x = paddle.getX();
        this.y = paddle.getY();
    }

    public double getX() { return x; }
    public double getY() { return y; }
}