package com.example.demo.view;

import com.example.demo.utils.var.GlobalVar;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.nio.file.Paths;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * Một View mới để phát video kết thúc game.
 * View này sẽ che toàn bộ màn hình.
 */
public class EndGameVideoView extends StackPane {
    private static final Logger log = LoggerFactory.getLogger(EndGameVideoView.class);

    private static final String VIDEO_PATH = "videos/end_game.mp4";

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Runnable onVideoFinished; // Callback khi video kết thúc

    public EndGameVideoView() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        setStyle("-fx-background-color: black;");

        try {
            URL resource = getClass().getResource(VIDEO_PATH);
            String mediaUrl = resource.toExternalForm();
            log.info("Video URL: {}", mediaUrl);
            Media media = new Media(mediaUrl);
            mediaPlayer = new MediaPlayer(media);

            mediaView = new MediaView(mediaPlayer);
            mediaView.fitWidthProperty().bind(widthProperty());
            mediaView.fitHeightProperty().bind(heightProperty());
            mediaView.setPreserveRatio(true);

            getChildren().add(mediaView);

            mediaPlayer.setOnEndOfMedia(() -> {
                log.info("Video kết thúc game đã phát xong.");
                stopVideo();
                if (onVideoFinished != null) {
                    onVideoFinished.run();
                }
            });

            mediaPlayer.setOnError(() -> {
                log.error("Lỗi MediaPlayer: {}", mediaPlayer.getError().getMessage());
                if (onVideoFinished != null) {
                    onVideoFinished.run();
                }
            });

        } catch (Exception e) {
            log.error("Không thể tải video kết thúc game: {}", e.getMessage(), e);
        }
    }

    /**
     * Bắt đầu phát video từ đầu.
     */
    public void playVideo() {
        if (mediaPlayer != null) {
            log.info("Bắt đầu phát video kết thúc game.");
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        }
    }

    /**
     * Dừng và giải phóng tài nguyên video.
     */
    public void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    /**
     * Đặt một hành động (Runnable) để thực thi khi video kết thúc.
     */
    public void setOnVideoFinished(Runnable callback) {
        this.onVideoFinished = callback;
    }
}