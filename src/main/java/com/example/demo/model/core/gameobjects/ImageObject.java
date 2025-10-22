package com.example.demo.model.core.gameobjects;

import com.example.demo.controller.AssetManager;

import javafx.scene.image.Image;

public abstract class ImageObject extends GameObject {
    protected String imageKey;
    protected Image image;

    public ImageObject(String imageKey, double startX, double startY) {
        super(startX, startY);
        setImageKey(imageKey);
    }
    
    public void setImageKey(String imageKey) {
        Image img = AssetManager.getInstance().getImage(imageKey);
        if (img == null) {
            System.err.println("[ERROR] Image not found for key: " + imageKey);
            return;
        }

        this.imageKey = imageKey;
        this.image = img;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.baseWidth = width;
        this.baseHeight = height;
        applyScale();
    }

    public String getImageKey() { return this.imageKey; }
    public Image getImage() { return this.image; }
}
