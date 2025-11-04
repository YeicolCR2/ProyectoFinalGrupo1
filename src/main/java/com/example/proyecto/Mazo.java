package com.example.proyecto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo {
    private final List<Card> cartas;

    public Mazo() {
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

    public List<Card> sacarTodas() {
        List<Card> copia = new ArrayList<>(cartas);
        cartas.clear();
        return copia;
    }

    public void barajar() {
        Collections.shuffle(cartas);
    }

    public int size() {
        return cartas.size();
    }

    public boolean isEmpty() {
        return cartas.isEmpty();
    }

    public List<Card> obtenerCartas() {
        return new ArrayList<>(cartas);
    }

    /**
     * Roba N cartas del tope (índice 0) y las devuelve.
     */
    public List<Card> robarVarias(int n) {
        int toRemove = Math.min(n, cartas.size());
        List<Card> robadas = new ArrayList<>();
        for (int i = 0; i < toRemove; i++) {
            robadas.add(cartas.remove(0));
        }
        return robadas;
    }

    public Card robarUna() {
        if (cartas.isEmpty()) return null;
        return cartas.remove(0);
    }
}
