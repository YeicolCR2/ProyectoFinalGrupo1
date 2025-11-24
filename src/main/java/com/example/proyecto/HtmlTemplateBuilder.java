package com.example.proyecto;

import com.example.proyecto.enums.Rank;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Constructor de plantillas HTML para visualizar las cartas.
 * Contiene el CSS y estructura HTML necesarios.
 */
public class HtmlTemplateBuilder {

    private final CardDeckConfig config;
    private final CardResourceLoader resourceLoader;

    public HtmlTemplateBuilder(CardDeckConfig config, CardResourceLoader resourceLoader) {
        this.config = config;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Genera el HTML completo con todas las cartas.
     * @return HTML completo como String
     */
    public String buildFullHtml() {

        return getHtmlHeader() +
                getCssStyles() +
                getStyleFooter() +
                generateCardsGrid() +
                getHtmlFooter();
    }

    private String getHtmlHeader() {
        return """
            <!doctype html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
        """;
    }

    private String getCssStyles() {
        try (InputStream is = getClass().getResourceAsStream("/card-deck-styles.css")) {
            if (is == null) {
                System.err.println("No se pudo cargar el archivo CSS. Usando estilos por defecto.");
                return getDefaultStyles();
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSS: " + e.getMessage());
            return getDefaultStyles();
        }
    }

    private String getDefaultStyles() {
        try (InputStream is = getClass().getResourceAsStream("/card-deck-styles-default.css")) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSS por defecto: " + e.getMessage());
        }

        // Estilos minimalistas de emergencia si no se puede cargar ningún archivo
        return """
                html,body{margin:0;padding:0;background:#1a1a1d;color:#e8eaed;font-family:'Segoe UI',sans-serif;}
            body{display:flex;flex-direction:column;padding:8px;}
            .suit-container{flex:1;display:flex;flex-direction:column;}
            .grid{display:grid;grid-template-columns:repeat(13,1fr);gap:4px;}
            img{width:100%;height:100%;object-fit:contain;}

            /* estilo de los nombres de los palos */
            .cap {
                color: #7ddf64; /* verde suave */
                font-weight: bold;
                font-size: 1.5em;
                text-transform: uppercase;
                text-align: center;
                margin-bottom: 8px;
            }
            """;
    }

    private String getStyleFooter() {
        return """
              </style>
            </head>
        """;
    }


    private String generateCardsGrid() {
        StringBuilder grid = new StringBuilder();

        for (String suit : config.getSuits()) {
            // Agregar el símbolo sin afectar la funcionalidad
            String suitDisplay = switch (suit.toLowerCase()) {
                case "clubs" -> "clubs ♣";
                case "diamonds" -> "diamonds ♦";
                case "hearts" -> "hearts ♥";
                case "spades" -> "spades ♠";
                default -> suit;
            };

            grid.append("<div class='suit-container'>");
            grid.append("<div class='cap'>").append(suitDisplay).append("</div>");
            grid.append("<div class='grid'>");

            for (Rank rank : config.getRanks()) {
                grid.append(resourceLoader.generateCardHtml(suit, rank));
            }

            grid.append("</div>");
            grid.append("</div>");
        }

        return grid.toString();
    }


    private String getHtmlFooter() {
        return "</body></html>";
    }
}
