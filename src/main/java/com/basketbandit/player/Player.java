package com.basketbandit.player;

import com.basketbandit.component.Card;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Player {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);
    private final String name;
    private ArrayList<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public ArrayList<Card> hand() {
        return hand;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void addToHand(Card[] cards) {
        for(Card card: cards) {
            addToHand(card);
            log.info("{} added {} to their hand.", name(), card.toString());
        }
    }

    public void clearHand() {
        hand = new ArrayList<>();
    }

    public int calculateHandValue() {
        int total = 0;
        for(Card card: this.hand()) {
            if(total < 11 && card.value() == 1) {
                total += 11;
                continue;
            }
            total += card.value();
        }
        return total;
    }
}
