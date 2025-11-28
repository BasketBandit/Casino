package com.basketbandit.component;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Hand {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Hand.class);
    private ArrayList<Card> cards;
    private int bet = 0;
    private boolean doubled = false;

    public Hand(int bet) {
        this.cards = new ArrayList<>();
        this.bet = bet;
    }

    public ArrayList<Card> cards() {
        return cards;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean isBust() {
        return value() > 21;
    }

    public boolean isBlackjack() {
        return cards().size() == 2 && value() == 21;
    }

    public void reset() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCard(Card[] cards) {
        for(Card card: cards) {
            addCard(card);
        }
    }

    public int value() {
        int total = 0;
        for(Card card: cards) {
            // special case for aces
            if(total < 11 && card.id() == 1) {
                total += 11;
                continue;
            }
            total += card.value();
        }
        return total;
    }

    public boolean doubled() {
        return this.doubled;
    }

    public int bet() {
        return this.bet;
    }

    public void setDoubled() {
        this.doubled = true;
    }
}
