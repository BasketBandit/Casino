package com.basketbandit.player;

import com.basketbandit.component.Action;
import com.basketbandit.component.Hand;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Player {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);
    private final String name;
    private ArrayList<Hand> hands = new ArrayList<>();
    private Action action = Action.WAITING;
    private final boolean human;
    private boolean out;
    private int currency = 250;

    public Player(String name) {
        this.name = name;
        this.human = false;
    }

    public Player(String name, boolean human) {
        this.name = name;
        this.human = human;
    }

    public String name() {
        return name;
    }

    public boolean isHuman() {
        return human;
    }

    public boolean isOut() {
        return out;
    }

    public ArrayList<Hand> hands() {
        return hands;
    }

    public Hand hand() {
        return hands.getFirst();
    }

    public Action action() {
        return action;
    }

    public int currency() {
        return currency;
    }

    public boolean placeBet(int value) {
        if(this.currency >= value) {
            this.currency -= value;
            addHand(new Hand(value));
            return true;
        }
        return false;
    }

    public void addCurrency(int currency) {
        this.currency += currency;
    }

    public void addHand(Hand hand) {
        this.hands.add(hand);
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public void clearHands() {
        this.hands = new ArrayList<>();
    }
}
