package com.example.demo.model.interfaces;

import java.util.List;

import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.view.UIManager;
import com.example.demo.view.graphics.ParallaxLayer;

public interface GameDataProvider {
    /* *
     * This class is the interface of GameManager
     * Provide data from GameManager to Renderer and others
     * 
     * Purpose:
     *  - Control logic from controller and view
     *  - Reduce dependent from GameManager to others
     * 
     * This class provides read-only access to core gameplay objects
     * likes the ball, paddle, bricks, walls, power-ups, UI state,
     * parallax layers
     */

    Ball getBall();
    Paddle getPaddle();
    Brick[] getBricks();
    List<Wall> getWalls();
    List<PowerUp> getPowerUps();
    UIManager getUiManager();
    List<ParallaxLayer> getParallaxLayers();
}
