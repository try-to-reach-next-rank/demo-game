package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Portal;
import com.example.demo.model.core.factory.PortalFactory;

public class PortalSystem implements Updatable {
    // private final PortalFactory portalFactory;

    // public PortalSystem(PortalFactory portalFactory) {
    //     this.portalFactory = portalFactory;
    // }

    public PortalSystem() {}

    @Override
    public void update(double deltaTime) {
        // System.out.println("UNIMPLEMENT UPDATE PORTALSYSTEM");
    }

    public void handleCollision(Portal portal, Ball ball) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCollision'");
    }

    @Override
    public void clear() {}
}
