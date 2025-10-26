package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.demo.controller.view.AssetManager;
import com.example.demo.engine.Effect;
import com.example.demo.model.core.VisualEffect;
import com.example.demo.model.core.effects.AnimatedEffect;

import javafx.scene.canvas.GraphicsContext;

public class EffectManager {
    private final AssetManager assetManager = AssetManager.getInstance();
    private static final EffectManager instance = new EffectManager();

    private final List<VisualEffect> activeEffects = new ArrayList<>();
    private final Map<String, VisualEffect> allEffectsMap = new HashMap<>();

    private EffectManager() {
        init();
    }

    public static EffectManager getInstance() {
        return instance;
    }

    private void init() {
        add("explosion1", new AnimatedEffect("explosion1"));
        add("explosion2", new AnimatedEffect("explosion2"));
    }

    public void spawn(String name, double x, double y, double duration) {
        VisualEffect template = allEffectsMap.get(name);
        if (template == null) {
            // Log here
            return;
        }

        VisualEffect effect = template.clone();

        effect.activate(x, y, duration);
        activeEffects.add(effect);
    }

    public void update(double deltaTime) {
        Iterator<VisualEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            VisualEffect effect = it.next();
            effect.update(deltaTime);
            if (!effect.isActive()) {
                it.remove();
            }
        }
    }

    public List<VisualEffect> getActiveEffects() {
        return this.activeEffects;
    }

    public void clear() {
        activeEffects.clear();

        for (VisualEffect effect : allEffectsMap.values()) {
            if (effect.isActive()) {
                effect.deactivate();
            }
        }
    }

    private void add(String effectKey, VisualEffect effect) {
        allEffectsMap.put(effectKey, effect);
    }
}
