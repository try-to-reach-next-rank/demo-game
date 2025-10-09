package com.example.demo.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

import com.example.demo.controller.*;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.interfaces.GameDataProvider;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.view.graphics.ParallaxLayer;

public class Renderer {
    /*
     * This is class Renderer
     * It can be helpful for:
     *  - render text
     *  - render image
     * 
     * This class help GameManager 
     * draw all in the screen
     */
    private GraphicsContext gc;
    private static Renderer instance;
    private final GameDataProvider data;

    // Constructor
    Renderer(GameDataProvider data) {
        this.data = data;
    }

    // Singleton
    public static Renderer getInstance(GameDataProvider data) {
        if (instance == null) {
            instance = new Renderer(data);
        }
        return instance;
    }

    public static Renderer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Renderer is null");
        }
        return instance;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
    
    public void clear() {
        if (gc == null) {
            throw new IllegalStateException("GraphicsContext is null");
        }
        
        gc.clearRect(0, 0, GlobalVar.WIDTH, GlobalVar.HEIGHT);
    }

    public void drawInGame() {
        if (gc == null) {
            throw new IllegalStateException("GraphicsContext is null");
        }

        drawObjects();
        drawEffects();
        data.getUiManager().render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
    }

    public void drawGameOver() {
        if (gc == null) {
            throw new IllegalStateException("GraphicsContext is null");
        }
        
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", 18));
        String message = "Game Over";
        gc.fillText(message,
                (GlobalVar.WIDTH - message.length() * 10) / 2.0,
                GlobalVar.HEIGHT / 2.0);
    }


    private void drawObjects() {
        // Get all objects
        Ball ball = data.getBall();
        Paddle paddle = data.getPaddle();
        Brick[] bricks = data.getBricks();
        List<Wall> walls = data.getWalls();
        List<PowerUp> activePowerUps = data.getPowerUps();
        List<ParallaxLayer> parallaxLayers = data.getParallaxLayers();
        
        drawParallax(gc, parallaxLayers);

        gc.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getWidth(), ball.getHeight());

        // Vẽ thanh đỡ
        gc.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getWidth(), paddle.getHeight());

        // Vẽ gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                        brick.getWidth(), brick.getHeight());
            }
        }

        // Vẽ PowerUp
        for (PowerUp p : activePowerUps) {
            if (p.isVisible()) {
                gc.drawImage(p.getImage(), p.getX(), p.getY(),
                        p.getWidth(), p.getHeight());
            }
        }

        // Vẽ tường
        for (Wall wall : walls){
            gc.drawImage(wall.getImage(), wall.getX(), wall.getY(),
                    wall.getWidth(), wall.getHeight());
        }
    }

    private void drawEffects() {
        EffectRenderer.getInstance().draw(gc);
    }

    private void drawParallax(GraphicsContext gc, List<ParallaxLayer> layers) {
        for(ParallaxLayer layer : layers) {
            Image img = layer.getImage();
            double x = layer.getX();
            double w = layer.getWidth();
            double h = layer.getHeight();

            gc.drawImage(img, x, 0, w, h);
            if(x + w < GlobalVar.WIDTH) {
                gc.drawImage(img, x + w, 0, w, h);
            }
        }
    }

}
