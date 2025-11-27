package com.basketbandit.component;

public class Shoe extends Deck {
    private final int decks;
    private final int initialSize;

    public Shoe(int decks) {
        this.decks = decks;
        for(int d = 0; d < decks; d++) {
            for(Suit suit : Suit.values()) {
                for(int i = 1; i < 14; i++) {
                    int value = Math.min(i, 10);
                    this.cards().add(new Card(i, value, suit));
                }
            }
        }
        this.initialSize = this.cards().size();
        this.shuffle();
    }

    public boolean shouldChangeShoe() {
        return this.cards().size() < (this.initialSize() / 4);
    }

    public int decks() {
        return decks;
    }

    @Override
    public int initialSize() {
        return initialSize;
    }
}
