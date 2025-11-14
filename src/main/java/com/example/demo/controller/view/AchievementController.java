package com.example.demo.controller.view;

import com.example.demo.model.menu.AchievementModel;

public class AchievementController {
    private final AchievementModel model;

    private Runnable onBackToMenu;
    private Runnable onNextPage;
    private Runnable onPrevPage;

    private int currentPage = 1;

    public AchievementController(AchievementModel model) {
        this.model = model;
    }

    // --- Callback setters ---
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void setOnNextPage(Runnable callback) {
        this.onNextPage = callback;
    }

    public void setOnPrevPage(Runnable callback) {
        this.onPrevPage = callback;
    }

    // --- Public actions ---
    public void backToMenu() {
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }

    public void nextPage() {
        if (currentPage == 1) {
            currentPage = 2;
            if (onNextPage != null) {
                onNextPage.run();
            }
        }
    }

    public void prevPage() {
        if (currentPage == 2) {
            currentPage = 1;
            if (onPrevPage != null) {
                onPrevPage.run();
            }
        }
    }

    // --- Getters ---
    public AchievementModel getModel() {
        return model;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
