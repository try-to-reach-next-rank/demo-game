package com.example.demo.engine.ui;

import java.util.List;
import java.util.function.Consumer;

public class UISelectionController {
    private final List<String> options;
    private int index = 0;
    private Consumer<String> onConfirm;

    public UISelectionController(List<String> options) {
        this.options = options;
    }

    public void moveUp() {
        index = (index - 1 + options.size()) % options.size();
    }

    public void moveDown() {
        index = (index + 1) % options.size();
    }

    public void confirm() {
        if (onConfirm != null) onConfirm.accept(options.get(index));
    }

    public void setOnConfirm(Consumer<String> onConfirm) {
        this.onConfirm = onConfirm;
    }

    public int getIndex() { return index; }
    public String getSelected() { return options.get(index); }
}
