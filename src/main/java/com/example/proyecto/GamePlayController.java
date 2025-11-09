package com.example.proyecto;

import com.example.proyecto.enums.Rank;
import com.example.proyecto.enums.Suit;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GamePlayController {

    private GameState gameState;
    private NavigationManager navigationManager;

    private final CardDeckConfig cardDeckConfig = new CardDeckConfig();
    private final CardResourceLoader cardResourceLoader = new CardResourceLoader(cardDeckConfig);

    // --- Bridge persistente entre Java y JavaScript ---
    private final JSBridge jsBridge = new JSBridge();

    // Variable para almacenar temporalmente las 3 cartas seleccionadas por el JS
    private String[] currentSelection = null;

    @FXML
    private WebView gameWebView;

    @FXML
    private Button barajarButton;

    @FXML
    private Label estadoSeleccionLabel;

    @FXML
    private Button validarSandwichButton;

    @FXML
    private Button verMazoButton;


    // ==========================================================
    // JS-JAVA BRIDGE: CLASE EXPUESTA A JAVASCRIPT
    // ==========================================================
    public class JSBridge {
        /**
         * Llamado por JavaScript cada vez que el usuario hace clic en una carta.
         */
        public void actualizarControles(String seleccionadasString) {
            int count = seleccionadasString.isEmpty() ? 0 : seleccionadasString.split(",").length;

            if (count == 3) {
                currentSelection = seleccionadasString.split(",");
            } else {
                currentSelection = null;
            }

            Platform.runLater(() -> {
                if (estadoSeleccionLabel != null)
                    estadoSeleccionLabel.setText(String.format("Seleccionadas: %d / 3", count));
                if (validarSandwichButton != null)
                    validarSandwichButton.setDisable(count != 3);
            });
        }
    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    private Optional<Card> getCardFromId(String cardId) {
        String[] parts = cardId.split("_");
        if (parts.length != 2) return Optional.empty();

        try {
            Rank rank = Rank.valueOf(parts[0].toUpperCase());
            Suit suit = Suit.valueOf(parts[1].toUpperCase());

            return gameState.getMano().stream()
                    .filter(c -> c.getRank() == rank && c.getSuit() == suit)
                    .findFirst();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // ==========================================================
    // INYECCIÓN DE DEPENDENCIAS
    // ==========================================================
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        this.loadGameView();
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    // ==========================================================
    // HANDLERS FXML
    // ==========================================================
    @FXML
    private void handleBarajar() {
        if (gameState != null) {
            gameState.barajar();
            loadGameView();
            if (barajarButton != null) barajarButton.setDisable(true);
        }
    }

    @FXML
    private void handleRobarMano() {
        if (gameState != null) {
            gameState.robarManoInicial();
            loadGameView();
        }
    }

    @FXML
    private void handleValidarSandwich() {
        if (currentSelection != null && currentSelection.length == 3) {
            List<Card> sandwich = new ArrayList<>();
            Arrays.stream(currentSelection).forEach(cardId -> getCardFromId(cardId).ifPresent(sandwich::add));

            if (sandwich.size() != 3) {
                gameWebView.getEngine().executeScript("alert('Error: No se pudieron encontrar 3 cartas para validar.');");
                return;
            }

            int robadas = gameState.intentarDescarte(sandwich);

            String mensaje = (robadas > 0)
                    ? String.format("¡Sándwich Válido! Has robado %d cartas.", robadas)
                    : "Sándwich Inválido. Intenta de nuevo.";

            currentSelection = null;
            loadGameView();
            gameWebView.getEngine().executeScript("alert('" + mensaje + "');");
        }
    }

    @FXML
    private void handleRestartGame() {
        if (gameState != null) {
            gameState.newGame();
            loadGameView();
            if (barajarButton != null) barajarButton.setDisable(false);
        }
    }

    @FXML
    private void handleBackToMenu() {
        if (navigationManager != null) {
            navigationManager.navigateToMenu();
        }
    }

    // ==========================================================
    // WEBVIEW RENDERING
    // ==========================================================
    private void loadGameView() {
        if (gameWebView != null && gameState != null) {
            WebEngine engine = gameWebView.getEngine();

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaBridge", jsBridge); // ✅ Correcto
                }
            });

            engine.loadContent(generarHtmlBase());

            Platform.runLater(() -> {
                if (estadoSeleccionLabel != null)
                    estadoSeleccionLabel.setText("Seleccionadas: 0 / 3");
                if (validarSandwichButton != null)
                    validarSandwichButton.setDisable(true);
            });
        } else {
            System.err.println("⚠️ Advertencia: GameState o WebView es NULL.");
        }
    }

    private String generarHtmlBase() {
        if (gameState == null) return "<html><body><h2>Error de carga: Estado de juego ausente.</h2></body></html>";

        // 1. OBTENER LOS DATOS HTML
        // La caja se muestra boca arriba
        String cajaHtml = generarContenedorHtml("Caja", gameState.getCaja(), "caja-container", false, true);

        // El mazo como pila (boceto).
        String mazoHtml = generarContenedorPilaHtml("Mazo Barajado", gameState.getMazo().size(), "mazo-container");

        // Mano es interactiva y boca arriba.
        String manoHtml = generarContenedorHtml("Tu Mano (Clic para seleccionar)", gameState.getMano(), "mano-container", true, true);

        // Pozo es boca arriba y no interactivo.
        String pozoHtml = generarContenedorHtml("Pozo (Descartadas)", gameState.getPozo(), "pozo-container", false, true);


        String jsCode = """
            <script>
                let seleccionadas = [];
                const manoContainer = document.getElementById('mano-container');
                // Función para limpiar todos los bordes dorados
                function limpiarBordes() {
                    if (manoContainer) {
                        manoContainer.querySelectorAll('.card-wrapper').forEach(card => {
                            card.style.border = 'none';
                        });
                    }
                }
                // ⚠️ Se ejecuta al cargar el HTML para limpiar cualquier estado visual previo.
                limpiarBordes();
                
                // 1. Manejar Clic en las Cartas de la Mano
                if (manoContainer) {
                    manoContainer.addEventListener('click', (event) => {
                        let cardElement = event.target.closest('.card-wrapper');
                        if (!cardElement || !manoContainer.contains(cardElement)) return;
            
                        const cardId = cardElement.getAttribute('data-card-id');
                        if (!cardId) return;
            
                        if (seleccionadas.includes(cardId)) {
                            // Deseleccionar
                            seleccionadas = seleccionadas.filter(id => id !== cardId);
                            cardElement.style.border = 'none';
                        } else if (seleccionadas.length < 3) {
                            // Seleccionar
                            seleccionadas.push(cardId);
                            cardElement.style.border = '4px solid gold';
                        }
                        
                        // LLAMADA CLAVE: Notifica a JavaFX sobre la nueva selección
                        window.javaBridge.actualizarControles(seleccionadas.join(','));

                    });
                }
            </script>
            """;

        // 3. ESTILOS CSS
        String styles = """
            <style>
                body { font-family: Arial, sans-serif; background-color: #202020; color: white; padding: 20px; }
                .contenedor { border: 1px solid #444; border-radius: 8px; padding: 10px; margin-bottom: 20px; background-color: #303030; }
                .card-container { display: flex; flex-wrap: wrap; justify-content: flex-start; }
                .card-wrapper { 
                    width: 80px; height: 120px; margin: 5px; 
                    box-shadow: 2px 2px 5px rgba(0,0,0,0.5);
                    border-radius: 5px; overflow: hidden; cursor: pointer;
                    transition: border 0.1s;
                }
                .card-wrapper img { width: 100%%; height: 100%%; object-fit: fill; } 
            </style>
            """;

        // 4. ENSAMBLAJE FINAL DEL HTML
        return String.format("""
            <html>
            <head>
                %s </head>
            <body>
                <h1>THE SANDWICH GUY - ¡A Jugar!</h1>
        
                <div style="display: flex; justify-content: space-around; margin-bottom: 20px;">
                    %s %s </div>
        
                <h2>Tu Juego</h2>
                %s %s %s </body>
            </html>
        """, styles, cajaHtml, mazoHtml, manoHtml, pozoHtml, jsCode);
    }


//    @FXML
//    private void handleVerMazo() {
//        if (gameState == null || gameWebView == null) return;
//
//        List<Card> mazo = gameState.getMazo();
//        if (mazo.isEmpty()) {
//            gameWebView.getEngine().executeScript("alert('El mazo está vacío.');");
//            return;
//        }
//
//        // Generar el HTML de las cartas del mazo
//        StringBuilder htmlCartas = new StringBuilder(
//                "<div style='display:flex; flex-wrap:wrap; justify-content:center; gap:4px;'>"
//        );
//        for (Card card : mazo) {
//            String cardHtml = cardResourceLoader.generateCardHtml(
//                    card.getSuit().toString().toLowerCase(),
//                    card.getRank()
//            );
//            htmlCartas.append("<div style='width:60px; height:90px;'>").append(cardHtml).append("</div>");
//        }
//        htmlCartas.append("</div>");
//
//
//        String script = String.format("""
//        const overlay = document.createElement('div');
//        overlay.style.position = 'fixed';
//        overlay.style.top = 0;
//        overlay.style.left = 0;
//        overlay.style.width = '100%%';
//        overlay.style.height = '100%%';
//        overlay.style.background = 'rgba(0,0,0,0.9)';
//        overlay.style.zIndex = 9999;
//        overlay.style.overflowY = 'auto';
//        overlay.style.transition = 'transform 0.1s ease-out';
//
//        overlay.innerHTML = `
//            <div style='padding:15px; text-align:center; color:white; font-family:Arial;'>
//                <h2>Mazo Actual (Modo Trampa)</h2>
//                <p style='font-size:14px; color:#ccc;'>(Usa Ctrl + rueda del mouse para hacer zoom)</p>
//                <button id='cerrarMazo'
//                    style='background:red; color:white; font-size:16px; padding:5px 10px;
//                           border:none; border-radius:5px; cursor:pointer; margin-bottom:10px;'>
//                    Cerrar
//                </button>
//                %s
//            </div>`;
//
//        document.body.appendChild(overlay);
//        document.getElementById('cerrarMazo').onclick = () => overlay.remove();
//
//        let scale = 1;
//        overlay.addEventListener('wheel', (e) => {
//            if (e.ctrlKey) {
//                e.preventDefault();
//                scale += e.deltaY * -0.001;
//                scale = Math.min(Math.max(0.5, scale), 2);
//                overlay.style.transform = `scale(${scale})`;
//                overlay.style.transformOrigin = 'center top';
//            }
//        });
//    """, htmlCartas);
//
//        gameWebView.getEngine().executeScript(script);
//    }

    /**
     * el botón para hacer trampa :v
     */
    @FXML
    private void handleVerMazo() {
        if (gameState == null || gameWebView == null) return;

        List<Card> mazo = gameState.getMazo();
        if (mazo.isEmpty()) {
            gameWebView.getEngine().executeScript("alert('El mazo está vacío.');");
            return;
        }

        // Generar el HTML de las cartas
        StringBuilder htmlCartas = new StringBuilder("<div id='cartasContainer'>");
        for (Card card : mazo) {
            String cardHtml = cardResourceLoader.generateCardHtml(
                    card.getSuit().toString().toLowerCase(),
                    card.getRank()
            );
            htmlCartas.append("<div class='card-item'>").append(cardHtml).append("</div>");
        }
        htmlCartas.append("</div>");

        // Crear overlay con animación suave
        String script = String.format("""
        const overlay = document.createElement('div');
        overlay.style.position = 'fixed';
        overlay.style.top = 0;
        overlay.style.left = 0;
        overlay.style.width = '100%%';
        overlay.style.height = '100%%';
        overlay.style.background = 'rgba(0,0,0,0.93)';
        overlay.style.zIndex = 9999;
        overlay.style.display = 'flex';
        overlay.style.flexDirection = 'column';
        overlay.style.justifyContent = 'flex-start';
        overlay.style.alignItems = 'center';
        overlay.style.padding = '30px 0';
        overlay.style.overflow = 'hidden';
        overlay.innerHTML = `
            <style>
                @keyframes fadeIn {
                    from { opacity: 0; transform: scale(0.6) translateY(50px); }
                    to { opacity: 1; transform: scale(1) translateY(0); }
                }
                #cartasContainer {
                    display: flex;
                    flex-wrap: wrap;
                    justify-content: center;
                    gap: 8px;
                    width: 95%%;
                    max-width: 1300px;
                    margin-top: 10px;
                }
                .card-item {
                    width: 75px;
                    height: 110px;
                    animation: fadeIn 0.3s ease forwards;
                    opacity: 0;
                }
                .card-item:nth-child(n) {
                    animation-delay: calc(var(--i) * 0.02s);
                }
                button#cerrarMazo {
                    background: #ff3333;
                    color: white;
                    font-size: 16px;
                    padding: 6px 12px;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-bottom: 12px;
                }
                h2 {
                    color: white;
                    font-family: 'Arial';
                    font-size: 22px;
                    margin-bottom: 5px;
                }
            </style>
            <h2>Mazo Actual (Modo Trampa)</h2>
            <button id='cerrarMazo'>Cerrar</button>
            %s
        `;
        document.body.appendChild(overlay);

        // Animación secuencial
        const cards = overlay.querySelectorAll('.card-item');
        cards.forEach((c, i) => c.style.setProperty('--i', i));

        // Cerrar overlay
        document.getElementById('cerrarMazo').onclick = () => overlay.remove();
    """, htmlCartas);

        gameWebView.getEngine().executeScript(script);
    }

    private String generarContenedorPilaHtml(String titulo, int cantidadCartas, String containerId) {
        StringBuilder contenidoHtml = new StringBuilder();

        if (cantidadCartas == 0) {
            contenidoHtml.append("<div style='color: #888; padding: 10px;'>Vacío.</div>");
        } else {
            String dorsoHtml = cardResourceLoader.generateCardBackHtml();

            contenidoHtml.append(String.format("<div class='card-wrapper' style='position: relative; width: 80px; height: 120px; margin: 5px;'> %s </div>", dorsoHtml));

            contenidoHtml.append(String.format("<div style='margin-top: 10px; font-size: 1.1em; color: #FFD700;'>%d cartas</div>",
                    cantidadCartas));
        }

        return String.format("""
                    <div id='%s' class='contenedor'>
                        <h3>%s</h3>
                        <div class='card-container' style='justify-content: center;'>%s</div>
                    </div>
                """, containerId, titulo, contenidoHtml.toString());
    }

    private String generarContenedorHtml(String titulo, List<Card> cartas, String containerId, boolean isInteractive, boolean isFaceUp) {
        StringBuilder cartasHtml = new StringBuilder();

        if (cartas.isEmpty()) {
            cartasHtml.append("<div style='color: #888; padding: 10px;'>Vacío.</div>");
        } else {
            for (Card card : cartas) {
                String cardId = card.getRank().toString() + "_" + card.getSuit().toString();
                String dataAttribute = isInteractive ? "data-card-id='" + cardId + "'" : "";

                String cardHtml;
                if (isFaceUp) {
                    cardHtml = cardResourceLoader.generateCardHtml(
                            card.getSuit().toString().toLowerCase(),
                            card.getRank()
                    );
                } else {
                    cardHtml = cardResourceLoader.generateCardBackHtml();
                }

                cartasHtml.append(String.format("<div class='card-wrapper' %s>%s</div>",
                        dataAttribute, cardHtml));
            }
        }

        return String.format("""
                    <div id='%s' class='contenedor'>
                        <h3>%s (%d Cartas)</h3>
                        <div class='card-container'>%s</div>
                    </div>
                """, containerId, titulo, cartas.size(), cartasHtml.toString());
    }
}
