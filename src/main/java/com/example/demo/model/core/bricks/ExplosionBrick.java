package com.example.demo.model.core.bricks;

import javafx.scene.image.Image;

import java.util.Objects;

public class ExplosionBrick extends Brick {
        private static final Image EXPLOSION_TEXTURE;

        static {
            EXPLOSION_TEXTURE = new Image(Objects.requireNonNull(
                    SteelBrick.class.getResourceAsStream("/images/ExplosionBricks.png"),
                    "Missing image file: /images/ExplosionBricks.png"));
        }

        public ExplosionBrick(int x, int y) {
            super(EXPLOSION_TEXTURE, x, y);
            setHealth(1);
        }

        @Override
        public String takeDamage() {
            if (isDestroyed()) return null;
            setDestroyed(true);
            return "explosion_hit";
        }
}
