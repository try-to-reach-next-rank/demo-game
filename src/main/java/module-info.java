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
    requires org.slf4j;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.model.state to com.google.gson;
    //  opens com.example.demo  to com.google.gson;

    exports com.example.demo;
    exports com.example.demo.model.utils;
    exports com.example.demo.model.core;
    exports com.example.demo.view;
    exports com.example.demo.view.ui;
    exports com.example.demo.model.map;
    exports com.example.demo.view.graphics;
    exports com.example.demo.model.utils.dialogue;
    exports com.example.demo.controller.core;
    exports com.example.demo.controller.map;
    exports com.example.demo.controller.view;
}