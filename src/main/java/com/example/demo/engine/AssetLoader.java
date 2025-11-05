package com.example.demo.engine;

import com.example.demo.model.assets.AssetManager;

public interface AssetLoader<T> {
    void loadInto(AssetManager assetManager);
}
