package com.basketbandit.player;

import com.basketbandit.component.Card;

public class Dealer extends Player {

    public Dealer() {
        super("Dealer", false);
    }

    /* Dealer will always hit if below 17 */
    public boolean willDraw() {
        int value = mainHand().value();
        log.info("{}'s total card value is: {}, will {} hit.", this.name(), value, (value < 17) ? "" : "not");
        return value < 17;
    }

    public void revealCard() {
        if(this.mainHand().cards().getFirst().flipped()) {
            this.mainHand().cards().getFirst().flip();
        }
    }

    public Card visibleCard() {
        return this.mainHand().cards().getLast();
    }
}
