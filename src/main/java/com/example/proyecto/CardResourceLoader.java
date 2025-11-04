package com.example.proyecto;

import com.example.proyecto.enums.Rank;

import java.net.URL;

/**
 * Cargador de recursos de cartas.
 * Maneja la carga de archivos SVG y errores, incluyendo la cara y el dorso.
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
        // Asegúrate de que config.getBasePath() sea correcto (ej: /cards/)
        String path = config.getBasePath() + fileName;
        URL url = getClass().getResource(path);

        if (url == null) {
            System.err.println("No encontrado: " + path);
        }

        return url;
    }

    /**
     * Genera el HTML para la cara de una carta (Boca Arriba).
     * @param suit Palo de la carta
     * @param rank Rango de la carta
     * @return HTML de la imagen (solo <img>)
     */
    public String generateCardHtml(String suit, Rank rank) {
        URL url = getCardResourceUrl(suit, rank);
        String fileName = config.getCardFileName(suit, rank);

        if (url != null) {
            // CORREGIDO: Se añade 'object-fit: fill' para evitar que la imagen se corte.
            return "<img src='" + url.toExternalForm() +
                    "' alt='" + fileName + "' style='width: 100%; height: 100%; object-fit: fill;'/>";
        } else {
            // DEVUELVE EL DIV DE FALLO
            return "<div style='width:100%;height:100%;background:#f7e26b;color:#000;" +
                    "display:flex;align-items:center;justify-content:center;font-size:0.7em'>Falta</div>";
        }
    }

    /**
     * Genera el HTML para el dorso (parte trasera) de la carta (Boca Abajo).
     * Carga el Boceto.svg.
     * @return HTML de la imagen del dorso (solo <img>)
     */
    public String generateCardBackHtml() {
        // RUTA CORREGIDA: Apunta a tu SVG en /resources/Boceto/
        String resourcePath = "/Boceto/Boceto.svg";

        try {
            URL url = getClass().getResource(resourcePath);

            if (url != null) {
                // CORREGIDO: Se añade 'object-fit: fill' para evitar que el SVG se corte.
                return "<img src='" + url.toExternalForm() + "' alt='Dorso de Carta' style='width: 100%; height: 100%; object-fit: fill;'/>";
            } else {
                System.err.println("¡Error! No se encontró el dorso: " + resourcePath);
                // Dorso de respaldo
                return "<div style='width:100%;height:100%;background:#8B0000;border-radius:5px;'>DORSO NO ENCONTRADO</div>";
            }
        } catch (Exception e) {
            System.err.println("Excepción al cargar el dorso: " + e.getMessage());
            return "<div style='width:100%;height:100%;background:black;'>ERROR</div>";
        }
    }
}