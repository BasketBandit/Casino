package com.basketbandit.component;

public enum Suit {
    HEARTS(1),
    DIAMONDS(2),
    SPADES(3),
    CLUBS(4);

    private final int id;

    Suit(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
