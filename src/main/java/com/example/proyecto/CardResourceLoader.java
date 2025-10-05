package com.example.proyecto;

import com.example.proyecto.enums.Rank;

import java.net.URL;

/**
 * Cargador de recursos de cartas.
 * Maneja la carga de archivos SVG y errores.
 */
public class CardResourceLoader {

    private final CardDeckConfig config;

    public CardResourceLoader(CardDeckConfig config) {
        this.config = config;
    }

    /**
     * Obtiene la URL de una carta específica.
     * @param suit Palo de la carta
     * @param rank Rango de la carta
     * @return URL del recurso o null si no existe
     */
    public URL getCardResourceUrl(String suit, Rank rank) {
        String fileName = config.getCardFileName(suit, rank);
        String path = config.getBasePath() + fileName;
        URL url = getClass().getResource(path);

        if (url == null) {
            System.err.println("No encontrado: " + path);
        }

        return url;
    }

    /**
     * Genera el HTML para una carta.
     * @param suit Palo de la carta
     * @param rank Rango de la carta
     * @return HTML de la carta
     */
    public String generateCardHtml(String suit, Rank rank) {
        URL url = getCardResourceUrl(suit, rank);
        String fileName = config.getCardFileName(suit, rank);

        if (url != null) {
            return "<div class='card-wrapper'><img src='" + url.toExternalForm() +
                   "' alt='" + fileName + "'/></div>";
        } else {
            return "<div style='width:100%;height:100%;background:#f7e26b;color:#000;" +
                   "display:flex;align-items:center;justify-content:center;font-size:0.7em'>Falta</div>";
        }
    }
}
