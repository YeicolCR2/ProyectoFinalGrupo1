package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<String> mano;

    public GameState() {
        mano = new ArrayList<>();
    }

    public void newGame() {
        mano.clear();

        System.out.println("Partida inicializada con nueva mano.");
    }

    public List<String> getMano() {
        return mano;
    }

    public void agregarCarta(String carta) {
        mano.add(carta);
    }

    public void removerCarta(String carta) {
        mano.remove(carta);
    }
}
