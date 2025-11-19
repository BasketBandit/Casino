package com.basketbandit;

import com.basketbandit.game.Blackjack;
import com.basketbandit.player.Player;

public class Casino {
    public Casino() {
        Blackjack blackjack = new Blackjack();
        blackjack.addPlayer(new Player("Josh"));
        blackjack.addPlayer(new Player("Julian"));
        blackjack.simulateHand();
    }
}
