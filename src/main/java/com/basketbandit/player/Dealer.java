package com.basketbandit.player;

import com.basketbandit.component.Card;

public class Dealer extends Player {

    public Dealer() {
        super("Dealer");
    }

    /* Dealer will always hit if below 17 */
    public boolean willDraw() {
        int value = calculateHandValue();
        log.info("{}'s total card value is: {}, will {} hit.", this.name(), value, (value < 17) ? "" : "not");
        return value < 17;
    }
}
