package com.example.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        NavigationManager navigationManager = new NavigationManager(stage);
        navigationManager.navigateToMenu();
        stage.show();
        stage.setFullScreen(false);
    }
}
