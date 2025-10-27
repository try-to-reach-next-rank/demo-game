package com.example.demo.view;

import com.example.demo.controller.view.EffectManager;
import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Renderable;
import com.example.demo.model.core.Brick;
import com.example.demo.model.core.VisualEffect;
import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.model.core.gameobjects.ImageObject;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class Renderer implements Renderable {
    private final GameWorld world;

    // Bricks reveal control
    private int brickRevealCounter = 0;
    private int currentRevealTick = 0;
    private final int BRICK_REVEAL_INTERVAL = 2;

    public Renderer(GameWorld world) {
        this.world = world;
    }

    @Override
    public void render(GraphicsContext gc) {
        updateBrickReveal();
        renderAllObjects(gc);

        // Draw Effects
        renderEffects(gc);
    }

    public void reset() {
        brickRevealCounter = 0;
        currentRevealTick = 0;

        // Reset reveal bricks
        for (Brick b : world.getBricks()) {
            b.setRevealed(false);
        }
    }

    private void updateBrickReveal() {
        currentRevealTick++;
        if (currentRevealTick >= BRICK_REVEAL_INTERVAL) {
            // Smoother
            currentRevealTick -= BRICK_REVEAL_INTERVAL;
            Brick[] bricks = world.getBricks();
            if (bricks != null && brickRevealCounter < bricks.length) {
                bricks[brickRevealCounter].setRevealed(true); // reveal brick by brick
                brickRevealCounter++;
            }
        }
    }

    private void renderAllObjects(GraphicsContext gc) {
        List<GameObject> objects = world.getAllObjects();
        if (objects == null) {
            // Log here
            return;
        }

        for (GameObject obj : objects) {
            if (obj == null || !obj.isVisible()) continue;
            drawObject(gc, obj);
        }
    }

    private void renderEffects(GraphicsContext gc) {
        for (VisualEffect effect : EffectManager.getInstance().getActiveEffects()) {
            effect.render(gc);
        }
    }

    private void drawObject(GraphicsContext gc, GameObject obj) {
        if (obj instanceof ImageObject imgObj) {
            gc.drawImage(imgObj.getImage(), imgObj.getX(), imgObj.getY(), imgObj.getWidth(), imgObj.getHeight());
        }
        else if (obj instanceof AnimatedObject animObj) {
            animObj.getAnimation().render(gc, animObj.getX(), animObj.getY(), animObj.getWidth(), animObj.getHeight());
        }
    }
}
