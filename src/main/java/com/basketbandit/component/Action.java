package com.basketbandit.component;

public enum Action {
    DEAL(0),
    HIT(1),
    STAND(2),
    SPLIT(3);

    private final int value;

    Action(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
