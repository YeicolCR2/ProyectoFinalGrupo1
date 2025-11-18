package com.example.proyecto;

import com.example.proyecto.enums.Rank;
import com.example.proyecto.enums.Suit;
import java.util.List;
import com.example.proyecto.enums.GameActionListener;

public class GameState {

    // Componentes del juego (Caja, Mazo, Mano, Pozo)
    private final Caja caja;
    private final Mazo mazo;
    private final Mano mano;
    private final Pozo pozo;
    private final CardDeckConfig config;
    private GameActionListener listener;

    // Constante para el cálculo de la diferencia (basado en 10 valores por palo)
    private static final int MAX_RANK_VALUE = 10;

    public GameState() {
        this.caja = new Caja();
        this.mazo = new Mazo();
        this.mano = new Mano();
        this.pozo = new Pozo();
        this.config = new CardDeckConfig();
    }

    public void setListener(GameActionListener listener) {
        this.listener = listener;
    }

    public void newGame() {
        caja.clear();
        mazo.clear();
        mano.clear();
        pozo.clear();

        // 1. Llenar la Caja con todas las cartas
        for (Suit suit : Suit.values()) {
            for (Rank rank : config.getRanks()) {
                caja.add(new Card(rank, suit));
            }
        }
        llenarCaja();
    }

    public void llenarCaja(){
        caja.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : config.getRanks()) {
                caja.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Implementa la funcionalidad de "barajar": pasa las Cartas de la Caja al Mazo aleatoriamente.
     */
    public void barajar() {
        mazo.clear();
        mazo.addAll(caja.sacarTodas());
        mazo.barajar();

        if (listener != null) {
            listener.onMazoBarajado(mazo.size());
        }
    }

    /**
     * Saca 8 cartas del Mazo y las pone en la Mano. (Parte del requisito inicial)
     */
    public void robarManoInicial() {
        mano.clear();
        int numToRob = Math.min(8, mazo.size());
        List<Card> cartasRobadas = mazo.robarVarias(numToRob);
        mano.addAll(cartasRobadas);
        if (listener != null) {
            listener.onManoRobada(numToRob);
        }
    }

    /**
     * Determina el valor numérico de una carta para el cálculo de diferencia.
     */
    private int getCardValue(Card card) {
        // Devuelve el valor ordinal (base 0) + 1.
        return card.getRank().ordinal() + 1;
    }

    /**
     * Función auxiliar para calcular la diferencia entre dos valores de carta,
     * manejando la "vuelta" (wrap-around) de 10 unidades.
     */
    private int calcularDiferencia(int valA, int valB) {
        int diff = Math.abs(valA - valB);
        return Math.min(diff, MAX_RANK_VALUE - diff);
    }

    /**
     * Verifica si tres cartas seleccionadas forman un "sándwich" válido.
     */
    public boolean esSandwichValido(Card c1, Card c2, Card c3) {
        int v1 = getCardValue(c1);
        int v2 = getCardValue(c2);
        int v3 = getCardValue(c3);

        int diff12 = calcularDiferencia(v1, v2);
        int diff23 = calcularDiferencia(v2, v3);

        return diff12 == diff23;
    }

    /**
     * Intenta descartar el sándwich y robar cartas según las reglas de Palo/Color.
     *
     * @param sandwich Lista de 3 cartas que deben estar en la Mano.
     * @return Número de cartas robadas (4, 3, 2, o 0 si inválido/error).
     */
    public int intentarDescarte(List<Card> sandwich) {
        if (sandwich.size() != 3 || !mano.containsAll(sandwich)) {
            System.err.println("Error: Tripleta inválida o no está en la Mano.");
            return 0;
        }

        Card c1 = sandwich.get(0);
        Card c2 = sandwich.get(1);
        Card c3 = sandwich.get(2);

        if (!esSandwichValido(c1, c2, c3)) {
            System.out.println("Sándwich inválido. No se descarta ni se roba.");
            return 0;
        }

        // 1. Descartar al Pozo
        pozo.addAll(sandwich);
        mano.removeAll(sandwich); // Quitamos las cartas de la Mano

        // 2. Determinar cuántas cartas robar
        int cartasARobar;

        // Regla 1: Mismo Palo (4 cartas)
        if (c1.getSuit() == c2.getSuit() && c2.getSuit() == c3.getSuit()) {
            cartasARobar = 4;
        }
        // Regla 2: Mismo Color (3 cartas)
        else if (mismoColor(c1, c2, c3)) {
            cartasARobar = 3;
        }
        // Regla 3: Otro caso (2 cartas)
        else {
            cartasARobar = 2;
        }

        // 3. Robar cartas y devolver el resultado
        int robadas = robarCartas(cartasARobar);
        System.out.printf("Sándwich válido. Robadas %d cartas. Mano actual: %d\n", robadas, mano.size());

        // Verificación de fin de partida (Ganar)
        if (mazo.isEmpty()) {
            System.out.println("¡MAZO VACÍO! El jugador gana la partida.");
        }

        return robadas;
    }

    private boolean mismoColor(Card c1, Card c2, Card c3) {
        boolean rojo = (c1.getSuit() == Suit.HEARTS || c1.getSuit() == Suit.DIAMONDS) &&
                (c2.getSuit() == Suit.HEARTS || c2.getSuit() == Suit.DIAMONDS) &&
                (c3.getSuit() == Suit.HEARTS || c3.getSuit() == Suit.DIAMONDS);
        return rojo;
    }

    /**
     * Saca 'n' cartas del Mazo y las agrega a la Mano.
     */
    private int robarCartas(int n) {
        int espacioDisponible = Mano.getMaxCartas() - mano.size();
        if (espacioDisponible <= 0) {
            return 0; // ya llena, no robamos nada
        }

        int cantidadARobar = Math.min(n, Math.min(espacioDisponible, mazo.size()));
        if (cantidadARobar <= 0) {
            return 0;
        }

        List<Card> cartasRobadas = mazo.robarVarias(cantidadARobar);
        mano.addAll(cartasRobadas);
        return cartasRobadas.size();
    }

    // ==========================================================
    // GETTERS
    // ==========================================================
    public List<Card> getCaja() {
        return caja.obtenerCartas();
    }

    public List<Card> getMazo() {
        return mazo.obtenerCartas();
    }

    public List<Card> getMano() {
        return mano.obtenerCartas();
    }

    public List<Card> getPozo() {
        return pozo.obtenerCartas();
    }

}
