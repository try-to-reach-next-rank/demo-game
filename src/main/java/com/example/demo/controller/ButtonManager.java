package com.example.demo.controller;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class ButtonManager {
    // lưu hiệu ứng pulse đang chạy
    private static final Map<Button, ScaleTransition> activePulses = new HashMap<>();

    // tay trái/phải dùng chung cho mọi button (giống style trong menu.css)
    private static Image handImage;
    private static final Map<Button, ImageView[]> hands = new HashMap<>();

    // nút hiện tại được chọn bằng bàn phím
    private static Button selectedButton = null;

    // ---------------------------
    // Load tài nguyên tay 1 lần
    // ---------------------------
    static {
        try {
            var url = ButtonManager.class.getResource("/images/hand.png");
            if (url != null)
                handImage = new Image(url.toExternalForm(), true);
        } catch (Exception ex) {
            System.err.println("[ButtonManager] Cannot load hand.png: " + ex.getMessage());
        }
    }

    // ---------------------------
    // Tạo một hàng button (tay + nút + tay)
    // ---------------------------
    public static HBox createButton(String text) {
        Button btn = new Button(text);
        btn.setMinWidth(220);
        btn.setFocusTraversable(false);
        btn.setFont(javafx.scene.text.Font.font(18));
        btn.getStyleClass().add("menu-button");

        // tạo tay trái/phải
        ImageView left = createHand(false);
        ImageView right = createHand(true);
        hands.put(btn, new ImageView[]{left, right});

        // hàng chứa
        HBox row = new HBox(12, left, btn, right);
        row.setAlignment(Pos.CENTER);

        // hiệu ứng hover
        addHoverAnimation(btn);
        btn.setOnMouseEntered(e -> {
            setSelectedButton(btn);
        });
        btn.setOnMouseExited(e -> {
            stopPulse(btn);
            hideHands(btn);
            btn.getStyleClass().remove("selected");
        });

        return row;
    }

    // ---------------------------
    // Hover animation nhẹ (giống mẫu)
    // ---------------------------
    private static void addHoverAnimation(Button b) {
        b.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(160), b);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });
        b.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(160), b);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    // ---------------------------
    // Quản lý tay
    // ---------------------------
    private static ImageView createHand(boolean flipped) {
        ImageView hand = new ImageView();
        if (handImage != null)
            hand.setImage(handImage);
        hand.setFitWidth(28);
        hand.setFitHeight(28);
        hand.setPreserveRatio(true);
        if (flipped) hand.setScaleX(-1);
        hand.setVisible(false);
        return hand;
    }

    private static void showHands(Button b) {
        ImageView[] imgs = hands.get(b);
        if (imgs != null) {
            imgs[0].setVisible(true);
            imgs[1].setVisible(true);
        }
    }

    private static void hideHands(Button b) {
        ImageView[] imgs = hands.get(b);
        if (imgs != null) {
            imgs[0].setVisible(false);
            imgs[1].setVisible(false);
        }
    }

    // ---------------------------
    // Pulse animation
    // ---------------------------
    private static void startPulse(Button b) {
        // dừng mọi pulse khác
        for (Button other : activePulses.keySet().toArray(new Button[0])) {
            if (other != b) stopPulse(other);
        }

        // bỏ qua nếu đang chạy
        if (activePulses.containsKey(b)) return;

        ScaleTransition st = new ScaleTransition(Duration.millis(420), b);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.06);
        st.setToY(1.06);
        st.setCycleCount(Timeline.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
        activePulses.put(b, st);
    }

    private static void stopPulse(Button b) {
        ScaleTransition st = activePulses.remove(b);
        if (st != null) {
            st.stop();
            b.setScaleX(1.0);
            b.setScaleY(1.0);
        }
    }

    // ---------------------------
    // API cho MenuView / SettingsView
    // ---------------------------
    public static void setSelectedButton(Button b) {
        // bỏ chọn nút cũ
        if (selectedButton != null && selectedButton != b) {
            stopPulse(selectedButton);
            hideHands(selectedButton);
            selectedButton.getStyleClass().remove("selected");
        }

        // chọn nút mới
        selectedButton = b;
        b.getStyleClass().add("selected");
        showHands(b);
        startPulse(b);
    }

    public static Button getSelectedButton() {
        return selectedButton;
    }

    public static void stopAll() {
        for (Button b : activePulses.keySet().toArray(new Button[0])) stopPulse(b);
    }
}
