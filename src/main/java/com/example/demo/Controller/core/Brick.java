package com.example.demo.Controller.core;

import javafx.scene.image.Image;

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
        // avoid repeating code.
        this(x, y, new Random().nextInt(MAX_HEALTH) + 1); // load brick (with random health) at given position
    }

    public Brick(int x, int y, int health){
        super(getImageFromHealth(health), x, y);
        this.health = health;
        destroyed = false;

    }

    // This allows subclasses (SteelBrick,.etc) to pass its preloaded image.
    protected Brick(Image image, int x, int y) {
        super(image, x, y);
        this.destroyed = false;
    }

    private static Image getImageFromHealth(int health){
        if(health > 0 && health <= MAX_HEALTH) return BRICK_TEXTURES.get(health - 1); //if index is 0, health = 1;
        return BRICK_TEXTURES.get(0);
    }

    private int getHealthFromImage(Image image){ //đã xử lý index= lưu ý : ảnh thứ nhất -> 1
        return BRICK_TEXTURES.indexOf(image) + 1; //if index = 4, health = 5;
    }

    public String takeDamage() {
        if (health > 0) {
            health--;
            if (health <= 0) {
                setDestroyed(true);
            } else {
                updateImage();
            }
            // Return the sound for a normal hit
            return "brick_hit";
        }
        // If the brick is already destroyed, no sound is made
        return null;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void handleExplosion(Brick sourceBrick, Brick[] bricks) {
        double centerX = sourceBrick.getX() + sourceBrick.getWidth() / 2;
        double centerY = sourceBrick.getY() + sourceBrick.getHeight() / 2;

        // Bán kính vụ nổ ( điều chỉnh giá trị 2.5)
        double radius = sourceBrick.getWidth() * 2.5;

        // Duyệt qua tất cả các viên gạch
        for (Brick otherBrick : bricks) {
            // Bỏ qua self và những viên đã bị phá hủy
            if (otherBrick == sourceBrick || otherBrick.isDestroyed()) {
                continue;
            }

            // Tâm gạch tiếp theo
            double otherCenterX = otherBrick.getX() + otherBrick.getWidth() / 2;
            double otherCenterY = otherBrick.getY() + otherBrick.getHeight() / 2;

            // Tính khoảng cách 2 gạch
            double distance = Math.sqrt(Math.pow(centerX - otherCenterX, 2) + Math.pow(centerY - otherCenterY, 2));

            // Nếu nằm trong bán kính vụ nổ = gọi takeDamage
            if (distance <= radius) {
                otherBrick.takeDamage();
            }
        }
    }
}
