module com.example.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires jdk.jsobject;

    opens com.example.proyecto to javafx.fxml;
    exports com.example.proyecto;
    exports com.example.proyecto.enums;
}