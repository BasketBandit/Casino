package com.basketbandit.game;

import com.basketbandit.component.Deck;
import com.basketbandit.player.Dealer;
import com.basketbandit.player.Player;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;

public class Blackjack extends Banking implements Game {
    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(Blackjack.class);
    private final LinkedList<Player> players = new LinkedList<>();

    public Blackjack() {
        this.deck = new Deck();
        this.players.add(new Dealer());
    }

    public void addPlayers(ArrayList<Player> players) {
        for(Player player: players) {
            addPlayer(player);
        }
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    @Override
    public void simulateHand() {
        boolean dealerBust = false;
        while(players.stream().anyMatch(player -> player.hand().value() < 16)) {
            for(Player player : players) {
                // dealer specific rules (fairly simple)
                if(player instanceof Dealer dealer) {
                    if(dealer.hand().isBust()) {
                        break;
                    }

                    if(dealer.hand().isEmpty()) {
                        dealer.hand().addCard(deck.draw(2));
                        continue;
                    }
                    if(dealer.willDraw()) {
                        dealer.hand().addCard(deck.draw(1));
                    }
                    continue;
                }

                // player specific rules
                if(player.hand().isEmpty()) {
                    player.hand().addCard(deck.draw(2));
                    continue;
                }
                if(player.hand().value() < 17) {
                    player.hand().addCard(deck.draw(1));
                }
            }
        }

        for(Player player: players) {
            log.info("");
            log.info(player.name());
            player.hand().cards().forEach(card -> log.info(card.toString()));
            int total = player.hand().value();
            log.info("Total card value: {}... {}", total, total == 21 ? "BLACKJACK" : total > 21 ? "BUST" : "");
            log.info("");
        }
    }

    @Override
    public void simulateTurn() {

    }

    @Override
    public void input(int key) {

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }
}
