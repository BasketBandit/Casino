package com.basketbandit.component;

public enum Action {
    WAITING(0),
    DEAL(1),
    HIT(2),
    STAND(3),
    SPLIT(4),
    DOUBLE(5);

    private final int value;

    Action(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
