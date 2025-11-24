package com.example.proyecto;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationManager {
    private final Stage primaryStage;

    public NavigationManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void navigateToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu-view.fxml"));
            Scene scene = new Scene(loader.load(), 640, 480);

            MenuController controller = loader.getController();
            controller.setNavigationManager(this);

            primaryStage.setScene(scene);
            primaryStage.setFullScreen(false);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar el menú: " + e.getMessage());
        }
    }

    public void navigateToCardDeck() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 640, 480);

            HelloController controller = loader.getController();
            controller.setNavigationManager(this);

            primaryStage.setScene(scene);
            primaryStage.setFullScreen(false);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void navigateToNewGame() {
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/new-game-view.fxml"));
        Scene scene = new Scene(loader.load(), 640, 480);

        NewGameController controller = loader.getController();
        controller.setNavigationManager(this);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void navigateToGamePlay(GameState gameState) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameplay-view.fxml"));
            Scene scene = new Scene(loader.load(), 640, 480);

            GamePlayController controller = loader.getController();
            controller.setNavigationManager(this);

            controller.setGameState(gameState);

            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
