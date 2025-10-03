package com.example.demo.core;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParallaxLayer extends GameObject {

    private  double initialWidth;
    private final double scrollRatio;

    // THUỘC TÍNH ANIMATION
    protected List<Image> animationFrames = null;
    protected int currentFrameIndex = 0;
    protected double timeSinceLastFrame = 0.0;
    private final double FRAME_DURATION = 0.2; // 5 FPS - Tốc độ chuyển khung hình

    // Constructor 1: Cho Layer TĨNH (Chỉ có 1 ảnh)
    public ParallaxLayer(String imagePath, double ratio) {
        super(imagePath, 0, 0);
        this.scrollRatio = ratio;
        setupDimensions();
    }

    // Constructor 2: Cho Layer ĐỘNG (Nhiều ảnh)
    public ParallaxLayer(String[] imagePaths, double ratio) {
        // Gọi constructor của GameObject với khung hình đầu tiên (giải quyết lỗi thiếu super())
        super(imagePaths[0], 0, 0);
        this.scrollRatio = ratio;

        // Tải các khung hình còn lại và lưu vào list
        this.animationFrames = new ArrayList<>();
        // Thêm khung hình đầu tiên đã được tải
        this.animationFrames.add(this.image);

        for (int i = 1; i < imagePaths.length; i++) {
            this.animationFrames.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePaths[i]))));
        }
        setupDimensions();
    }

    // Khởi tạo kích thước chung
    private void setupDimensions() {
        this.initialWidth = this.width;
        this.height = VARIABLES.HEIGHT;
        this.imageView.setFitHeight(this.height);
        this.imageView.setFitWidth(this.initialWidth);
        this.width = this.initialWidth;
    }

    // Logic Parallax (Không đổi)
    public double getParallaxX(double totalMaxOffset, double normalizedPaddleX) {
        double layerMaxOffset = totalMaxOffset * this.scrollRatio;
        return -normalizedPaddleX * layerMaxOffset;
    }

    public void setXOffset(double offset) {
        this.x = offset;
        setPosition(this.x, this.y);
    }

    // PHƯƠNG THỨC MỚI: Cập nhật Animation
    public void update(double deltaTime) {
        // Chỉ chạy animation nếu có nhiều khung hình
        if (animationFrames != null && animationFrames.size() > 1) {
            this.timeSinceLastFrame += deltaTime;

            if (this.timeSinceLastFrame >= FRAME_DURATION) {
                this.currentFrameIndex = (this.currentFrameIndex + 1) % this.animationFrames.size();
                this.image = this.animationFrames.get(this.currentFrameIndex);
                this.timeSinceLastFrame -= FRAME_DURATION;
            }
        }
    }
}