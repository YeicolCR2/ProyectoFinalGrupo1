package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MenuController {


    //componenentes visuales

    @FXML
    private Label lblTitle;

    @FXML
    private Button btnNewGame;

    @FXML
    private Button btnLoadGame;

    @FXML
    private Button btnViewCards;

    @FXML
    private Button btnExit;

    private NavigationManager navigationManager;

    //cierre de componentes visuales

    @FXML
    public void initialize() {
        lblTitle.setText("THE SANDWICH GUY");
    }


    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    private void handleNewGame() {
        if (navigationManager != null) {
            navigationManager.navigateToNewGame();
        }
    }
//    @FXML
//    private void setBtnLoadGame() {
//        if (navigationManager != null) {
//            navigationManager.handleLoadGame();
//        }
//    }


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
