package com.basketbandit.state;

import com.basketbandit.io.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class State {
    protected static final Logger log = LoggerFactory.getLogger(State.class);

    public State() {
    }

    public abstract void input(Input type, int[] id);
    public abstract void update();
    public abstract void render();
}
