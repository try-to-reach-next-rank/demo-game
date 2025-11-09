package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.core.factory.PortalFactory;

public class PortalSystem implements Updatable {
    private final PortalFactory portalFactory;

    public PortalSystem(PortalFactory portalFactory) {
        this.portalFactory = portalFactory;
    }

    @Override
    public void update(double deltaTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void handleCollision(Portal portal, Ball ball) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCollision'");
    }
}
