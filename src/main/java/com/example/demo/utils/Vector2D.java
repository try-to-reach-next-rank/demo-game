package com.example.demo.utils;

public class Vector2D {
    public double x,y;
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double len = length();
        return len == 0 ? new Vector2D(0,0) : new Vector2D(x / len, y / len);
    }

    @Override
    public String toString() {
        return String.format(
            "The vector is: %f %f", x, y
        );
    }

    // Quay vector theo góc (degree)
    public Vector2D rotate(double degree) {
        double rad = Math.toRadians(degree);
        return new Vector2D(
            x * Math.cos(rad) - y * Math.sin(rad),
            x * Math.sin(rad) + y * Math.cos(rad)
        );
    }

    // Quay vector ngẫu nhiên ±halfDegree
    public Vector2D rotateRandom(double halfDegree) {
        double offset = GameRandom.nextDouble(-halfDegree, halfDegree); // độ
        return rotate(offset);
    }
}
