package com.basketbandit.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class State {
    protected static final Logger log = LoggerFactory.getLogger(State.class);

    public State() {
    }

    public abstract void input(int key);
    public abstract void update();
    public abstract void render();
}
