package com.example.demo.model.core.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.demo.model.core.entities.Portal;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class PortalFactory {
    private final List<Portal> portals;

    public PortalFactory() {
        portals = new ArrayList<>();
    }

    public void create(String portalAnimKey, int x, int y, double lifeTime) {
        Portal portal = new Portal(portalAnimKey);

        // Random shooting direction
        double angle = GameRandom.nextDouble(0, 2 * Math.PI);
        portal.setDirection(new Vector2D(Math.cos(angle), Math.sin(angle)));
        portal.activate(x, y, lifeTime);

        portals.add(portal);
    }

    public List<Portal> getPortals() {
        return portals;
    }

    // Random destination portal
    public Portal getRandomDestination(Portal src) {
        List<Portal> candidates = portals.stream()
            .filter(p -> p != null && p != src && p.isActive())
            .toList();

        if (candidates.isEmpty()) return null;

        // Random portal in remaining list
        return candidates.get(GameRandom.nextInt(candidates.size()));
    }

    public void clear() {
        portals.clear();
    }
}
