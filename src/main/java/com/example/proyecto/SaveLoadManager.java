package com.example.proyecto;

import com.example.proyecto.enums.Rank;
import com.example.proyecto.enums.Suit;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {

    private static Element cardToElement(Document doc, Card card) {
        Element cardEl = doc.createElement("card");
        cardEl.setAttribute("rank", card.getRank().toString());
        cardEl.setAttribute("suit", card.getSuit().toString());
        return cardEl;
    }

    private static Card elementToCard(Element element) {
        Rank rank = Rank.valueOf(element.getAttribute("rank"));
        Suit suit = Suit.valueOf(element.getAttribute("suit"));
        return new Card(rank, suit);
    }

    private static void saveCardList(Document doc, Element parent, List<Card> cards, String tag) {
        Element listEl = doc.createElement(tag);
        for (Card card : cards) {
            listEl.appendChild(cardToElement(doc, card));
        }
        parent.appendChild(listEl);
    }

    private static List<Card> loadCardList(Element root, String tag) {
        List<Card> list = new ArrayList<>();
        NodeList nodes = root.getElementsByTagName(tag).item(0).getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element cardEl) {
                list.add(elementToCard(cardEl));
            }
        }
        return list;
    }

    // ==========================================================
    // SAVE
    // ==========================================================
    public static void saveGame(GameState gameState, File file) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("gameState");
        doc.appendChild(root);

        saveCardList(doc, root, gameState.getCaja(), "caja");
        saveCardList(doc, root, gameState.getMazo(), "mazo");
        saveCardList(doc, root, gameState.getMano(), "mano");
        saveCardList(doc, root, gameState.getPozo(), "pozo");

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    // ==========================================================
    // LOAD
    // ==========================================================
    public static void loadGame(GameState gameState, File file) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();

        List<Card> caja = loadCardList(root, "caja");
        List<Card> mazo = loadCardList(root, "mazo");
        List<Card> mano = loadCardList(root, "mano");
        List<Card> pozo = loadCardList(root, "pozo");

        gameState.setLoadedState(caja, mazo, mano, pozo);
    }
}
