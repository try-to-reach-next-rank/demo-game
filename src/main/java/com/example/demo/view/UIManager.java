package com.example.demo.view;

import com.example.demo.view.ui.UIComponent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class UIManager {
    /*
    TODO Separate this class to be:
    - screen state:     tracks which ui screen is currently active, player score,...
    - renderer:         reads screenState and draws appropriate UI elements
    - menu controller:  handle input on ui screen, updating ui screen state and telling ui renderer to refresh
     */
    private final List<UIComponent> componentList = new ArrayList<>();

    public void add(UIComponent uiComponent) {
        componentList.add(uiComponent);
    }

    public void update(double deltaTime) {
        for (UIComponent uiComponent : componentList) {
            if(uiComponent.isActive()) {
                uiComponent.update(deltaTime);
            }
        }
    }

    public void render(GraphicsContext gc, double width, double height) {
        for(UIComponent uiComponent : componentList) {
            if(uiComponent.isActive()) {
                uiComponent.render(gc, width, height);
            }
        }
    }

    public void handleInput(KeyCode code) {
        for (UIComponent uiComponent : componentList) {
            if (uiComponent.isActive()) {
                uiComponent.handleInput(code);
                break; // only top-most active UI handles input
            }
        }
    }

    public boolean hasActiveUI() {
        return componentList.stream().anyMatch(UIComponent::isActive);
    }

    public boolean contains(UIComponent uiComponent) {
        return componentList.contains(uiComponent);
    }
}
