package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class Mano {
    private final List<Card> cartas;
    private static final int MAX_CARTAS = 8; // límite según especificación del juego

    public Mano() {
        this.cartas = new ArrayList<>();
    }

    public void add(Card carta) {
        if (cartas.size() >= MAX_CARTAS) {
            return;
        }
        cartas.add(carta);
    }

    public void addAll(List<Card> otras) {

        for (Card c : otras) {
            if (cartas.size() >= MAX_CARTAS) {
                break;
            }
            cartas.add(c);
        }
    }

    public void remove(Card carta) {
        cartas.remove(carta);
    }

    public void removeAll(List<Card> aRemover) {
        cartas.removeAll(aRemover);
    }

    public boolean contains(Card c) {
        return cartas.contains(c);
    }

    public boolean containsAll(List<Card> lista) {
        return cartas.containsAll(lista);
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

    public static int getMaxCartas() {
        return MAX_CARTAS;
    }
}