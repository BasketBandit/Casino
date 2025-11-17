package com.basketbandit.component;

public class Card {
    private final int id;
    private final String name;
    private final int value;
    private final Suit suit;

    public Card(int id, int value, Suit suit) {
        this.id = id;
        this.value = value;
        this.suit = suit;
        switch(id) {
            case 1 -> name = "Ace";
            case 11 -> name = "Jack";
            case 12 -> name = "Queen";
            case 13 -> name = "King";
            default -> name = id + "";
        }
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int value() {
        return value;
    }

    public Suit suit() {
        return suit;
    }

    @Override
    public String toString() {
        return name() + " of " + suit.name();
    }
}
