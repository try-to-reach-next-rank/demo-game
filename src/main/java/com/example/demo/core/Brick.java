package com.example.demo.core;

import javafx.scene.image.Image;

import java.time.chrono.MinguoDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Brick extends GameObject {
    private boolean destroyed;
    private static final int MAX_HEALTH = VARIABLES.MAXHEALTH_OF_BRICKS;
    private static final List<Image> BRICK_TEXTURES = new ArrayList<>(); // The list is ordered [1hp, 2hp, 3hp, 4hp, 5hp]
    private int health;

    static {
        for (int i = 1; i <= MAX_HEALTH; i++) {
            String path = (i > 1) ? "/images/Bricks" + i + ".png" : "/images/Bricks.png";
            BRICK_TEXTURES.add(new Image(Objects.requireNonNull(Brick.class.getResourceAsStream(path), "Missing image file: " + path)));
        }
    }

    public Brick(int x, int y) {

        super(getImageFromHealth(new Random().nextInt(MAX_HEALTH) + 1), x, y); // load brick (with random health) at given position
        this.health = getHealthFromImage(this.image);
        destroyed = false;
    }

    private static Image getImageFromHealth(int health){
        if(health > 0) return BRICK_TEXTURES.get(health - 1); //if index is 0, health = 1;
        return BRICK_TEXTURES.get(0);
    }

    private int getHealthFromImage(Image image){
        return BRICK_TEXTURES.indexOf(image) + 1; //if index = 4, health = 5;
    }

    public void takeDamage() {
        if (health > 0) {
            health--;
            if (health <= 0) setDestroyed(true);
            else updateImage();
        }
    }

    private void updateImage() {
        setImage(getImageFromHealth(this.health));
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean val) {
        destroyed = val;
    }
}
