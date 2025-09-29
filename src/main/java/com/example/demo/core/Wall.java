package com.example.demo.core;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class Wall extends GameObject{
        public Wall(double startX, double startY) {
            super("/images/Wall.png", startX, startY);
        }
}
