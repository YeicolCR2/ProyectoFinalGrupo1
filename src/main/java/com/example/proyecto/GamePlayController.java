package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.IOException;

public class GamePlayController {

    // Dependencias inyectadas por el NavigationManager
    private GameState gameState;
    private NavigationManager navigationManager;

    // Componente visual inyectado desde gameplay-view.fxml
    @FXML
    private WebView gameWebView;


    //(Setters)

    // 1. Recibe el estado del juego y activa la carga de la vista
    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        // Llamar a la carga de datos AQUÍ, donde 'gameState' ya no es NULL.
        this.loadGameView();
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }


    // Nuevo método para cargar o refrescar la vista del juego
    private void loadGameView() {
        if (gameWebView != null && gameState != null) {
            WebEngine engine = gameWebView.getEngine();
            // Carga el contenido HTML generado con los datos del GameState
            engine.loadContent(generarHtmlBase());
        } else {
            System.err.println("Advertencia: No se pudo cargar la vista, GameState o WebView es NULL.");
        }
    }


    private String generarHtmlBase() {
        // Esta comprobación redundante pero segura.
        if (gameState == null) return "<html><body><h2>Error de carga: Estado de juego ausente.</h2></body></html>";

        StringBuilder cartasHtml = new StringBuilder();

        // Genera el HTML para cada carta de la mano
        for (String carta : gameState.getMano()) {
            cartasHtml.append("<div class='card'>").append(carta).append("</div>");
        }

        // Estructura HTML para mostrar las cartas
        return """
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f0f0f0; padding: 20px; }
                        .card { background: white; border: 1px solid #ccc; border-radius: 8px; padding: 10px; margin: 10px; display: inline-block; }
                    </style>
                </head>
                <body>
                    <h2>Tu Mano de Cartas</h2>
                    <div class="card-container">
                        %s
                    </div>
                </body>
                </html>
                """.formatted(cartasHtml.toString());
    }

    // --- Manejadores de Eventos ---

    @FXML
    private void handleBackToMenu() {
        if (navigationManager != null) {
            navigationManager.navigateToMenu();
        }
    }

    @FXML
    private void handleRestartGame() {
        if (gameState != null) {
            gameState.newGame();
            // Vuelve a cargar la vista con el estado de juego reseteado
            loadGameView();
        }
    }
}