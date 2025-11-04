package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class Caja {
    private final List<Card> cartas;

    public Caja() {
        this.cartas = new ArrayList<>();
    }

    public void add(Card carta) {
        cartas.add(carta);
    }

    public void addAll(List<Card> cartasToAdd) {
        cartas.addAll(cartasToAdd);
    }

    public void clear() {
        cartas.clear();
    }

    public List<Card> sacarTodas() {
        List<Card> copia = new ArrayList<>(cartas);
        cartas.clear();
        return copia;
    }

    public int size() {
        return cartas.size();
    }

    public boolean estaVacia() {
        return cartas.isEmpty();
    }

    public List<Card> obtenerCartas() {
        return new ArrayList<>(cartas);
    }
}
