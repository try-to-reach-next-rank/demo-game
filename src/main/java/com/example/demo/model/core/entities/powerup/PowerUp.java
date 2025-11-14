package com.example.demo.model.core.entities.powerup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.state.PowerUpData;
import com.example.demo.utils.Timer;
import com.example.demo.utils.var.GameVar;

public abstract class PowerUp extends AnimatedObject<PowerUpData> {
    protected String type;
    private static final Logger log = LoggerFactory.getLogger(PowerUp.class);
    private final Timer timer;
    private double fallSpeed;
    private double duration;
    private boolean active;
    
    public PowerUp(String type) {
        super("default", 0, 0);
        this.type = type;
        log.info("Type: {}", type);

        // Initialize
        this.timer = new Timer();
        this.fallSpeed = GameVar.BASE_SPEED_POWERUP;

        setAnimationKey("powerup_" + type.toLowerCase());
    }

    
    
}
