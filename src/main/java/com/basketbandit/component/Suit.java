package com.basketbandit.component;

public enum Suit {
    HEARTS("hearts"),
    DIAMONDS("diamonds"),
    SPADES("spades"),
    CLUBS("clubs");

    private final String lowercase;

    Suit(String lowercase) {
        this.lowercase = lowercase;
    }

    public String lowercase() {
        return lowercase;
    }
}
