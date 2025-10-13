module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires javafx.graphics;
    requires com.google.gson;

    opens com.example.demo to javafx.fxml;
    //  opens com.example.demo.model.utils to com.google.gson;

    exports com.example.demo;
    exports com.example.demo.model.utils;
    exports com.example.demo.controller;
    exports com.example.demo.model.core;
    exports com.example.demo.model.core.bricks;
    exports com.example.demo.view;
    exports com.example.demo.view.ui;
    exports com.example.demo.model.states;
    exports com.example.demo.view.graphics;
}