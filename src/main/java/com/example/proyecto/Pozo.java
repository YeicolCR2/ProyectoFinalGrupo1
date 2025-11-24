package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class Pozo {
    private final List<Card> cartas;

    public Pozo() {
        this.cartas = new ArrayList<>();
    }

    public void add(Card carta) {
        cartas.add(carta);
    }

    public void addAll(List<Card> otras) {
        cartas.addAll(otras);
    }

    public void clear() {
        cartas.clear();
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

    public Card verUltimaCarta() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.get(cartas.size() - 1);
    }
}
