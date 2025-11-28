package com.basketbandit.player;

public class Dealer extends Player {

    public Dealer() {
        super("Dealer", false);
    }

    /* Dealer will always hit if below 17 */
    public boolean willDraw() {
        int value = hand().value();
        log.info("{}'s total card value is: {}, will {} hit.", this.name(), value, (value < 17) ? "" : "not");
        return value < 17;
    }

    public void revealCard() {
        if(this.hand().cards().getFirst().flipped()) {
            this.hand().cards().getFirst().flip();
        }
    }
}
