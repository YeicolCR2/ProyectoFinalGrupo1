package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class Mano {
    private final List<Card> cartas;

    public Mano() {
        this.cartas = new ArrayList<>();
    }

    public void add(Card carta) {
        cartas.add(carta);
    }

    public void addAll(List<Card> otras) {
        cartas.addAll(otras);
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
}
