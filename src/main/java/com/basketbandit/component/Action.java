package com.basketbandit.component;

public enum Action {
    SANS(0),
    BET(1),
    WAITING(2),
    READY(3),
    DEAL(4),
    HIT(5),
    STAND(6),
    SPLIT(7),
    DOUBLE(8);

    private final int value;

    Action(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
