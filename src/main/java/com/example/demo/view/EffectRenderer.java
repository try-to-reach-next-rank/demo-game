package com.example.demo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.example.demo.model.core.effects.AnimatedEffect;

import com.example.demo.model.core.effects.ScorePopupEffect;
import com.example.demo.model.core.effects.VisualEffect;
import com.example.demo.utils.ObjectPool;
import javafx.scene.canvas.GraphicsContext;

public class EffectRenderer {
    private static final EffectRenderer instance = new EffectRenderer();

    private final List<VisualEffect> activeEffects = new ArrayList<>();
    private final Map<String, ObjectPool<VisualEffect>> effectPools = new HashMap<>();

    private EffectRenderer() {
        init();
    }

    public static EffectRenderer getInstance() {
        return instance;
    }

    private void init() {
        registerEffect("explosion1", () -> new AnimatedEffect("explosion1"));
        registerEffect("explosion2", () -> new AnimatedEffect("explosion2"));
        registerEffect("scorePopup", () -> new ScorePopupEffect());
    }

    private void registerEffect(String name, Supplier<VisualEffect> creator) {
        effectPools.put(name, new ObjectPool<>(creator, 10));
    }

    public void spawn(String name, double x, double y, double duration) {
        ObjectPool<VisualEffect> pool = effectPools.get(name);
        if (pool == null) return;

        VisualEffect effect = pool.acquire();
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
                ObjectPool<VisualEffect> pool = effectPools.get(effect.getName());
                if (pool != null) pool.release(effect);
            }
        }
    }

    public List<VisualEffect> getActiveEffects() { return this.activeEffects; }

    public void clear() {
        for (VisualEffect e : activeEffects) {
            e.deactivate();
            ObjectPool<VisualEffect> pool = effectPools.get(e.getName());
            if (pool != null) pool.release(e);
        }
        activeEffects.clear();
    }
}