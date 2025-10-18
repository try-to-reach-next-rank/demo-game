package com.example.demo.engine;

import java.util.Map;

import com.example.demo.controller.AssetManager;

public interface AssetLoader<T> {
    void loadInto(AssetManager assetManager);
}
