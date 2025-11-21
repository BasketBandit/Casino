package com.basketbandit.player;

import com.basketbandit.component.Action;
import com.basketbandit.component.Hand;
import org.slf4j.LoggerFactory;

public class Player {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);
    private final String name;
    Hand hand = new Hand();
    private Action action = Action.DEAL;
    private boolean playing;
    private boolean out;

    public Player(String name) {
        this.name = name;
        this.playing = false;
    }

    public Player(String name, boolean playing) {
        this.name = name;
        this.playing = playing;
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

    public boolean isPlaying() {
        return playing;
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
