package com.example.demo.core;

public record Collision(GameObject a, GameObject b, double time, double OverlapX, double OverlapY) {
    public boolean involves(GameObject obj) {
        return a == obj || b == obj;
    }

    public double getOverlapX() {
        return OverlapX;
    }

    public double getOverlapY() {
        return OverlapY;
    }
}
