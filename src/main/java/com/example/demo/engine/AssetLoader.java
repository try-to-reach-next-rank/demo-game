package com.example.demo.engine;

import com.example.demo.controller.view.AssetManager;

public interface AssetLoader<T> {
    void loadInto(AssetManager assetManager);
}
