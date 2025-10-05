package com.example.proyecto;

import com.example.proyecto.enums.Rank;

/**
 * Configuración de la baraja de cartas.
 * Define los palos y rangos disponibles.
 */
public class CardDeckConfig {

    public static final String CARDS_BASE_PATH = "/cards/";

    private static final String[] SUITS = {"clubs", "diamonds", "hearts", "spades"};

    public String[] getSuits() {
        return SUITS.clone();
    }

    public Rank[] getRanks() {
        return Rank.values();
    }

    public String getBasePath() {
        return CARDS_BASE_PATH;
    }

    public String getCardFileName(String suit, Rank rank) {
        return suit + "_" + rank.getFileNameValue() + ".svg";
    }
}
