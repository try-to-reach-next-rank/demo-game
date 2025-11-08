package com.example.demo.engine;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.core.gameobjects.GameObject;

public class CollisionDetector {
    public List<CollisionPair> detect(List<GameObject> objects) {
        List<CollisionPair> result = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            GameObject a = objects.get(i);
            for (int j = i + 1; j < objects.size(); j++) {
                GameObject b = objects.get(j);
                if (a.getBounds().intersects(b.getBounds())) {
                    result.add(new CollisionPair(a, b));
                }
            }
        }
        return result;
    }

    public static class CollisionPair {
        public final GameObject a, b;
        public CollisionPair(GameObject a, GameObject b) { this.a = a; this.b = b; }
    }
}