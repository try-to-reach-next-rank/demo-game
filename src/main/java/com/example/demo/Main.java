package com.example.demo;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.menu.SettingsModel;
import com.example.demo.model.stage.GameStateManager;
import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.stage.MenuState;
import com.example.demo.utils.var.AssetPaths;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private GameStateManager stateManager;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        AssetManager.getInstance().loadAll();
        SettingsModel.getInstance();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource(AssetPaths.ICON)).toExternalForm()));
        stage.setTitle("Brick Breaker");
        stage.setResizable(false);
        log.info("All assets loaded.");

        this.stateManager = new GameStateManager(this, stage);

        this.stateManager.changeState(new MenuState(this, stage));
    }

    public GameStateManager getStateManager() {
        return this.stateManager;
    }
}