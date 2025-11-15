package com.example.demo.model.state.gameobjectdata;

import com.example.demo.model.core.gameobjects.ImageObject;

public class ImageObjectData extends GameObjectData {
    private final String imageKey;

    public ImageObjectData(ImageObject<?> object) {
        super(object);
        this.imageKey = object.getImageKey();
    }

    public String getImageKey() { return imageKey; }
}
