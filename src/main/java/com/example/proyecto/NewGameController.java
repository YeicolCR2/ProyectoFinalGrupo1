package com.example.proyecto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class NewGameController {

    @FXML
    private Button btnPlay;

    private NavigationManager navigationManager;

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    private void handlePlay() {
        if (navigationManager != null) {

            GameState gameState = new GameState();
            gameState.newGame();
            navigationManager.navigateToGamePlay(gameState);
        }
    }

    @FXML
    private void handleBackToMenu() {
        if (navigationManager != null) {
            navigationManager.navigateToMenu();
        }
    }
}
