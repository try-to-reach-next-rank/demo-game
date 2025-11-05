package com.example.demo.controller.map;

import com.example.demo.controller.map.MenuController;
import com.example.demo.model.menu.MenuModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {

    @Test
    void testIsSelecting() {
        MenuModel model = new MenuModel();
        MenuController controller = new MenuController(model);

        controller.isSelecting();
        assertEquals(MenuModel.Screen.SELECT, model.getCurrentScreen());
    }

    @Test
    void testIsSettings() {
        MenuModel model = new MenuModel();
        MenuController controller = new MenuController(model);

        controller.isSettings();
        assertEquals(MenuModel.Screen.SETTINGS, model.getCurrentScreen());
    }
}
