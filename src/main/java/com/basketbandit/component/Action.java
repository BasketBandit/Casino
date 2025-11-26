package com.basketbandit.component;

public enum Action {
    SANS(0),
    BET(1),
    WAITING(2),
    DEAL(3),
    HIT(4),
    STAND(5),
    SPLIT(6),
    DOUBLE(7);

    private final int value;

    Action(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
