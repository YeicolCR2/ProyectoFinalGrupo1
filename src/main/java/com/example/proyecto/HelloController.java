package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller principal para la vista de cartas.
 * Coordina la configuración, carga de recursos y generación de HTML.
 */
public class HelloController implements Initializable {

    @FXML
    private WebView svgView;

    @FXML
    private Button btnBackToMenu;

    private final HtmlTemplateBuilder templateBuilder;
    private NavigationManager navigationManager;

    public HelloController() {
        CardDeckConfig config = new CardDeckConfig();
        CardResourceLoader resourceLoader = new CardResourceLoader(config);
        this.templateBuilder = new HtmlTemplateBuilder(config, resourceLoader);
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = svgView.getEngine();
        String htmlContent = templateBuilder.buildFullHtml();
        engine.loadContent(htmlContent, "text/html");
    }

    @FXML
    private void handleBackToMenu() {
        if (navigationManager != null) {
            navigationManager.navigateToMenu();
        }
    }
}
