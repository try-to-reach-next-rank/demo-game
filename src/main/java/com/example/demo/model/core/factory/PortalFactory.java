package com.example.demo.model.core.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.demo.model.core.entities.Portal;
import com.example.demo.utils.var.GameVar;

public class PortalFactory {
    private final List<Portal> portals = new ArrayList<>();
    private final Random random = new Random();

    public void addPortal(String portalAnimKey) {
        // Random x, y, default duration
        int x = random.nextInt(GameVar.MAP_MIN_X, GameVar.MAP_MAX_X);
        int y = random.nextInt(GameVar.MAP_MIN_Y, GameVar.MAP_MAX_Y);
        double lifeTime = 10.0; // default 10 seconds

        addPortal(portalAnimKey, x, y, lifeTime);
    }

    public void addPortal(String portalAnimKey, int x, int y, double lifeTime) {
        Portal portal = new Portal(portalAnimKey);
        portal.activate(x, y, lifeTime);
        portals.add(portal);
    }

    public List<Portal> getPortals() {
        return portals;
    }

    // Random destination portal
    public Portal getRandomDestination(Portal src) {
        List<Portal> candidates = portals.stream()
            .filter(p -> p != src && p.isActive())
            .toList();

        if (candidates.isEmpty()) return null;

        // Random portal in remaining list
        return candidates.get(random.nextInt(candidates.size()));
    }

    public void clear() {
        portals.clear();
    }
}
