package com.example.proyecto.enums;

public enum Rank {
    ACE("ace"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("jack"),
    QUEEN("queen"),
    KING("king");

    private final String fileNameValue;

    Rank(String fileNameValue) {
        this.fileNameValue = fileNameValue;
    }

    public String getFileNameValue() {
        return fileNameValue;
    }
}
