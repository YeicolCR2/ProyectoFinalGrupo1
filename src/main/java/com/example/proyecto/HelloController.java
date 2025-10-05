package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    private final HtmlTemplateBuilder templateBuilder;

    public HelloController() {
        CardDeckConfig config = new CardDeckConfig(); //Carga las configuraciones, saber las rutas, nombres de las cartas
        CardResourceLoader resourceLoader = new CardResourceLoader(config); //carga las cartas, las recibe todas y crea el html de las cartas indiviodual
        this.templateBuilder = new HtmlTemplateBuilder(config, resourceLoader);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = svgView.getEngine();
        String htmlContent = templateBuilder.buildFullHtml();
        engine.loadContent(htmlContent, "text/html"); /*Start*/
    }
}
