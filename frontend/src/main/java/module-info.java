module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.net.http;
    requires org.json;
    requires io.github.cdimascio.dotenv.java;
    requires com.google.gson;
    requires java.base;

    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
}