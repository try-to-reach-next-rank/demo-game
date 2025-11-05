package com.example.demo.view;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.*;
import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.core.gameobjects.AnimationBatchUpdate;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.map.ParallaxLayer;
import com.example.demo.model.system.ParallaxSystem;
import com.example.demo.utils.var.AssetPaths;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreView {
    private final GameWorld world;
    private final GraphicsContext gc;
    private final Set<Brick> revealedBricks = new HashSet<>();
    private int brickRevealCounter = 0;
    private int currentRevealTick = 0;
    private boolean reveal = true;
    private ParallaxSystem parallaxSystem;
    private final Rectangle2D viewport = new Rectangle2D(
            0,0, GlobalVar.WIDTH, GlobalVar.HEIGHT
    );

    public CoreView(GraphicsContext gc, GameWorld world) {
        this.gc = gc;
        this.world = world;
    }

    public void render(GraphicsContext gc) {
        renderParallax(gc);

        drawBricks(gc);
        renderAllObjects(gc);
        
        renderEffects(gc);
    }

    public void update(double deltaTime) {
        parallaxSystem.update(deltaTime);
        EffectRenderer.getInstance().update(deltaTime);
        AnimationBatchUpdate.getInstance().updateAll(deltaTime);
        setupBrickReveal();
    }

    private void renderParallax(GraphicsContext gc) {
        if (parallaxSystem != null) {
            parallaxSystem.render(gc);
        }
    }

    private void renderAllObjects(GraphicsContext gc) {
        List<GameObject> objects = world.getAllObjects();
        if (objects == null) {
            return;
        }

        for (GameObject obj : objects) {
            if (obj == null || !obj.isVisible()) continue;

            double x = obj.getX();
            double y = obj.getY();
            double w = obj.getWidth();
            double h = obj.getHeight();

            if (!isInView(x, y, w, h)) continue;

            drawObject(gc, obj);
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

    private void renderEffects(GraphicsContext gc) {
        for (VisualEffect effect : EffectRenderer.getInstance().getActiveEffects()) {
            effect.render(gc);
        }
    }

    private void setupBrickReveal() {
        currentRevealTick++;

        if (reveal) {
            int brickRevealInterval = 5;
            if (currentRevealTick >= brickRevealInterval) {
                currentRevealTick = 0;
                Brick[] bricks = world.getBricks();
                if (bricks != null && brickRevealCounter < bricks.length) {
                    revealedBricks.add(bricks[brickRevealCounter]);
                    brickRevealCounter++;
                }
            }
        } else {
            Brick[] bricks = world.getBricks();
            if (bricks != null) {
                revealedBricks.clear();
                revealedBricks.addAll(Arrays.asList(bricks));
                brickRevealCounter = bricks.length;
            }
        }
    }

    private void drawBricks(GraphicsContext gc) {
        Brick[] bricks = world.getBricks();
        if (bricks == null) return;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            if (!revealedBricks.contains(brick)) continue;

            if (!isInView(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight()))
                continue;

            gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                    brick.getWidth(), brick.getHeight());
        }
    }

    public void reset() {
        revealedBricks.clear();
        brickRevealCounter = 0;
        currentRevealTick = 0;
    }
    
    public void initParallax() {
        parallaxSystem = new ParallaxSystem(
                world,
                GameVar.PARALLAX_BASE_SPEED,
                GameVar.PARALLAX_DEPTH,
                GameVar.PARALLAX_SPEED_LAYERS
        );

        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER1, GameVar.PARALLAX_SPEED_LAYERS[3]));
        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER2, GameVar.PARALLAX_SPEED_LAYERS[2]));
        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER3, GameVar.PARALLAX_SPEED_LAYERS[1]));
        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER4, GameVar.PARALLAX_SPEED_LAYERS[0]));
    }

    private boolean isInView(double x, double y, double width, double height) {
        return viewport.intersects(x, y, width, height);
    }
}
