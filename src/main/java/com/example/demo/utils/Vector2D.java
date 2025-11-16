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

    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
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

    /**
     * A vector2D when collide with a surface -> tangent stays the same, the parallel to surface is flipped
     * Parallel to surface (into the wall):
     *      v1 = (v * n)n
     * Along the wall:
     *      v2 = v - v1
     * => v = v1 + v2
     *
     * Reflection:
     *      r = v2 - v1
     *        = v - 2 * v1
     *        = v - 2 * (v * n)n
     * @param normal the surface vector to calculate the reflection
     * @return a reflection vector2D
     */
    public Vector2D reflect(Vector2D normal) {
        double dot = this.dot(normal);
        return new Vector2D(
                x - 2 * dot * normal.x,
                y - 2 * dot * normal.y
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
