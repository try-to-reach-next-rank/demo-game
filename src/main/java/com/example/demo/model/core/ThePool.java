package com.example.demo.model.core;

import com.example.demo.utils.ObjectPool;

public class ThePool {
    public static class PowerUpPool {
        private static final ObjectPool<PowerUp> pool = new ObjectPool<>(() -> new PowerUp("generic"), 20);

        public static PowerUp acquire(String type) {
            PowerUp p = pool.acquire();
            p.reset(type);
            return p;
        }

        public static void release(PowerUp p) {
            p.deactivate();
            pool.release(p);
        }
    }
}
