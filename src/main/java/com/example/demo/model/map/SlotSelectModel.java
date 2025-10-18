package com.example.demo.model.map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SlotSelectModel {
    // 0 = Slot 1, 1 = Slot 2
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(0);

    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    public void setSelectedIndex(int value) {
        selectedIndex.set(value);
    }

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }
}
