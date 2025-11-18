package com.basketbandit.player;

import com.basketbandit.component.Hand;
import org.slf4j.LoggerFactory;

public class Player {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);
    private final String name;
    Hand hand = new Hand();

    public Player(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Hand hand() {
        return hand;
    }
}
