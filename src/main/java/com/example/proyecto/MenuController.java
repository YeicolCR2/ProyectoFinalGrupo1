package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    @FXML
    private Button btnNewGame;

    @FXML
    private Button btnViewCards;

    @FXML
    private Button btnExit;

    private NavigationManager navigationManager;


    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    private void handleNewGame() {
        if (navigationManager != null) {
            //TODO: crear metodo
            navigationManager.navigateToNewGame();
        }
    }

    @FXML
    private void handleViewCards() {
        if (navigationManager != null) {
            navigationManager.navigateToCardDeck();
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
