package com.basketbandit.player;

import com.basketbandit.component.Action;
import com.basketbandit.component.Hand;
import org.slf4j.LoggerFactory;

public class Player {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);
    private final String name;
    Hand hand = new Hand();
    private Action action = Action.DEAL;
    private final boolean human;
    private boolean out;

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

    public Hand hand() {
        return hand;
    }

    public Action action() {
        return action;
    }

    public boolean isHuman() {
        return human;
    }

    public boolean isOut() {
        return out;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setOut(boolean out) {
        this.out = out;
    }


}
