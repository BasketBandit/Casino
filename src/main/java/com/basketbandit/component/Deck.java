package com.basketbandit.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final ArrayList<Card> cards = new ArrayList<>();

    public Deck() {
        for(Suit suit : Suit.values()) {
            for(int i = 1; i < 14; i++) {
                int value = Math.min(i, 10);
                cards.add(new Card(i, value, suit));
            }
        }
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
            int index = random.nextInt(cards.size());
            draw[i] = cards.get(index);
            cards.remove(index);
        }
        return draw;
    }

    /**
     * Shuffles the deck using {@link Collections#shuffle(List)}
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }
}
