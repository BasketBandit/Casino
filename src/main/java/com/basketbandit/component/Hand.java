package com.basketbandit.component;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Hand {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Hand.class);
    private Action action = Action.WAITING;
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
        int aces = 0;
        int total = 0;

        for(Card card: cards) {
            if(card.id() != 1) {
                total += card.value();
            } else {
                aces++;
            }
        }

        // need to check if more than 0 aces else hard 11 becomes blackjack
        if(aces > 0 && (total + (11 + (aces-1)) < 22)) {
            total += (11 + (aces-1));
        } else {
            total += aces;
        }

        return total;
    }

    public boolean isSoft() {
        int aces = 0;
        int total = 0;

        for(Card card: cards) {
            if(card.id() != 1) {
                total += card.value();
            } else {
                aces++;
            }
        }

        if(aces == 0) {
            return false;
        }

        return (total + (11 + (aces-1)) < 22);
    }

    public boolean isPair() {
        if(cards.size() != 2) {
            return false;
        }

        int id = 0;
        for(Card card: cards) {
            if(card.id() == id) {
                return true;
            }
            id = card.id();
        }

        return false;
    }

    public Action action() {
        return action;
    }

    public boolean doubled() {
        return this.doubled;
    }

    public int bet() {
        return this.bet;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setDoubled() {
        this.bet *= 2;
        this.doubled = true;
    }
}
