package com.basketbandit.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final ArrayList<Card> cards = new ArrayList<>();
    private final int initialSize;
    private int runningCount;

    public Deck() {
        for(Suit suit : Suit.values()) {
            for(int i = 1; i < 14; i++) {
                int value = Math.min(i, 10);
                cards.add(new Card(i, value, suit));
            }
        }
        initialSize = cards.size();
        runningCount = 0;
    }

    /**
     * @return {@link ArrayList<Card>}
     */
    public ArrayList<Card> cards() {
        return cards;
    }

    /**
     * Draws x number of cards, randomly selected using {@link Random#nextInt(int)}, removing them from the list of cards and returning them as an array of type {@link Card}.
     * @param count
     * @return {@link Card[]}
     */
    public Card[] draw(int count) {
        Random random = new Random();
        Card[] draw = new Card[count];
        for(int i = 0; i < count; i++) {
            draw[i] = cards.removeFirst();
            updateRunningCount(draw[i]);
        }
        return draw;
    }

    public int initialSize() {
        return initialSize;
    }

    /**
     * Shuffles the deck using {@link Collections#shuffle(List)}
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    private void updateRunningCount(Card card) {
        switch(card.id()) {
            case 2, 3, 4, 5, 6 -> runningCount++;
            case 1, 10, 11, 12, 13 -> runningCount--;
        }
    }

    public int runningCount() {
        return runningCount;
    }

    public int trueCount() {
        return runningCount / (cards().size() / 52);
    }
}
