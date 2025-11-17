package com.basketbandit;

import com.basketbandit.component.Deck;
import com.basketbandit.game.Blackjack;
import com.basketbandit.player.Player;

import java.util.Arrays;

public class Casino {
    public Casino() {
        Blackjack blackjack = new Blackjack();
        blackjack.addPlayer(new Player("Josh"));
        blackjack.addPlayer(new Player("Julian"));
        blackjack.simulateHand();
    }
}
