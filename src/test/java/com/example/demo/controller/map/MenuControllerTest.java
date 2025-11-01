package com.example.demo.controller.map;

import com.example.demo.model.menu.MenuModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {

    private MenuModel model;
    private MenuController controller;

    @BeforeEach
    void setUp() {
        model = new MenuModel();
        controller = new MenuController(model);
    }

    @Test
    void testIsPlayingSetsScreenToPlay() {
        controller.isPlaying();
        assertEquals(MenuModel.Screen.PLAY, model.getCurrentScreen());
    }

    @Test
    void testIsSelectingSetsScreenToSelect() {
        controller.isSelecting();
        assertEquals(MenuModel.Screen.SELECT, model.getCurrentScreen());
    }

    @Test
    void testIsPauseSetsScreenToPause() {
        controller.isPause();
        assertEquals(MenuModel.Screen.PAUSE, model.getCurrentScreen());
    }

    @Test
    void testIsSettingsSetsScreenToSettings() {
        controller.isSettings();
        assertEquals(MenuModel.Screen.SETTINGS, model.getCurrentScreen());
    }
}
