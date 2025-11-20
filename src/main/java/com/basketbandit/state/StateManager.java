package com.basketbandit.state;

import com.basketbandit.Engine;
import com.basketbandit.game.Blackjack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StateManager {
    private static final Logger log = LoggerFactory.getLogger(StateManager.class);
    private static final Map<String, State> states = new HashMap<>(Map.ofEntries(
            Map.entry("main_menu", new MainMenu()),
            Map.entry("blackjack", new Blackjack())
    ));
    private static State lastState = new MainMenu();

    public StateManager() {}

    public static void addState(String name, State state) {
        states.put(name, state);
    }

    public static State lastState() {
        return lastState;
    }

    public static boolean hasState(String name) {
        return states.containsKey(name);
    }

    public static State state(String name) {
        if(name.equals("last")) {
            return lastState;
        }

        State state = states.getOrDefault(name, states.get("main_menu"));
        lastState = state;
        return state;
    }

    /**
     * Change state of the engine without calling classes having direct access.
     * @param name
     */
    public static void changeState(String name) {
        log.info("Changing state to: {}", name);
        Engine.setState(state(name));
    }

    public static void addAndChangeState(String name, State state) {
        states.put(name, state);
        changeState(name);
    }
}


