package com.example.demo.controller.view;

import com.example.demo.model.menu.MenuModel;
import com.example.demo.model.menu.SettingsModel;

// File: SettingsController.java (Đã sửa)

import com.example.demo.model.menu.SettingsModel;

public class SettingsController {
    private final SettingsModel model;
    // Bỏ tham chiếu đến MenuModel
    // private final MenuModel menuModel;

    // ⭐ THÊM CALLBACK ⭐
    private Runnable onBackToMenu;

    // Constructor đã sửa
    public SettingsController(SettingsModel model) {
        this.model = model;
        // Bỏ menuModel khỏi constructor
    }

    // ⭐ Phương thức mới để gán Callback ⭐
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void backToMenu() {
        // Gọi callback thay vì setState cho MenuModel
        if (onBackToMenu != null) {
            System.out.println("DEBUG P1: SettingsController ĐÃ phát tín hiệu: BACK TO MENU (qua callback)");
            onBackToMenu.run(); // Thực thi lệnh quay về Menu
        }
    }

    public SettingsModel getModel() {
        return model;
    }
}