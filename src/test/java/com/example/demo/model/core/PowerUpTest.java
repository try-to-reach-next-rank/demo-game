package com.example.demo.model.core;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {
    @Test
    void initialStateAndType_andVisibility() {
        PowerUp p = new PowerUp(GameVar.ACCELERATE);
        assertEquals(GameVar.ACCELERATE, p.getType());
        // default visible flag should be false
        assertFalse(p.isVisible());
        // not active initially
        assertFalse(p.isActive());
        assertFalse(p.hasExpired());
        assertEquals(0, p.getRemainingDuration());
    }

    @Test
    void fallWhenVisibleChangesYAndHidesWhenBeyondScreen() {
        PowerUp p = new PowerUp(GameVar.STRONGER);

        // show it and set initial Y
        p.setVisible(true);
        p.setPosition(0.0, 50.0);
        double before = p.getY();

        // fall for 0.2s -> increase by fallSpeed * deltaTime
        double dt = 0.2;
        p.fall(dt);
        double expected = before + 150.0 * dt;
        assertEquals(expected, p.getY(), 1e-9);

        // put below screen and ensure becoming invisible after fall
        p.setVisible(true);
        p.setPosition(0.0, GlobalVar.HEIGHT + 10.0);
        p.fall(0.016);
        assertFalse(p.isVisible(), "PowerUp should become invisible when y > GlobalVar.HEIGHT");
    }

    @Test
    void activateExpiresAndIsActiveReflectsState() throws InterruptedException {
        PowerUp p = new PowerUp(GameVar.BIGGERPADDLE);

        p.activate(60); // 60 ms
        assertTrue(p.isActive(), "should be active immediately after activation");
        assertFalse(p.hasExpired(), "should not be expired immediately after activation");

        // wait for expiration
        Thread.sleep(90);
        assertTrue(p.hasExpired(), "should be expired after duration");
        assertFalse(p.isActive(), "should not be active after expiration");
        assertEquals(0, p.getRemainingDuration());
    }

    @Test
    void activateWithRemainingDuration_and_getRemainingDuration() throws InterruptedException {
        PowerUp p = new PowerUp(GameVar.ACCELERATE);

        p.activateWithRemainingDuration(200);
        assertTrue(p.isActive());
        long remaining1 = p.getRemainingDuration();
        assertTrue(remaining1 > 0 && remaining1 <= 200);

        Thread.sleep(70);
        long remaining2 = p.getRemainingDuration();
        assertTrue(remaining2 >= 0 && remaining2 < remaining1, "remaining should decrease after waiting");

        // deactivate and ensure remaining = 0
        p.deactivate();
        assertFalse(p.isActive());
        assertEquals(0, p.getRemainingDuration());

        // activating with non-positive does nothing
        p.activateWithRemainingDuration(0);
        assertFalse(p.isActive());
    }

    @Test
    void deactivateClearsActiveFlag() {
        PowerUp p = new PowerUp(GameVar.STRONGER);
        p.activate(1000);
        assertTrue(p.isActive());
        p.deactivate();
        assertFalse(p.isActive());
        assertFalse(p.hasExpired(), "hasExpired should be false when deactivated");
    }

    @Test
    void setVisibleAndGetType() {
        PowerUp p = new PowerUp(GameVar.BIGGERPADDLE);
        assertEquals(GameVar.BIGGERPADDLE, p.getType());
        assertFalse(p.isVisible());
        p.setVisible(true);
        assertTrue(p.isVisible());
    }
}