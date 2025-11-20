package com.basketbandit.game;

import com.basketbandit.Renderer;
import com.basketbandit.component.Deck;
import com.basketbandit.io.Keyboard;
import com.basketbandit.player.Dealer;
import com.basketbandit.player.Player;
import com.basketbandit.util.Colours;
import com.basketbandit.util.Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Blackjack extends Banking implements Game {
    protected static final Logger log = LoggerFactory.getLogger(Blackjack.class);
    private final LinkedList<Rectangle> positions = new LinkedList<>();
    private final LinkedList<Player> players = new LinkedList<>();

    public Blackjack() {
        this.deck = new Deck();

        addPlayer(new Dealer());
        positions.add(new Rectangle((175 + (2 * 200)), 150, 125, 125));

        addPlayer(new Player("Josh"));
        addPlayer(new Player("Julian"));
        addPlayer(new Player("Jacob"));
        addPlayer(new Player("Ant"));
        addPlayer(new Player("Mark"));
        for(int i = 0; i < (players.size()-1); i++) {
            int space = (Renderer.width() - ((players.size()-1)*125)) / ((players.size()-1)+1); // calculates spacing between objects
            positions.add(new Rectangle(space+(i*125)+(i*space), (Renderer.height() - 250), 125, 125));
        }
        // subtract the total width of your objects from the total length available, and then divide that result by the number of spaces between the objects
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
        if(key == Keyboard.ENTER) {
            simulateHand();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        Graphics2D graphics = Renderer.graphics();
        graphics.setColor(Colours.BLACK);
        graphics.fillRect(0, 0, Renderer.width(), Renderer.height());
        graphics.setFont(Fonts.default24);
        graphics.setColor(Colours.WHITE);
        graphics.drawString("BLACKJACK", 50, 50);

        for(Player player: players) {
            int index = players.indexOf(player);
            Rectangle r = positions.get(index);

//            graphics.setColor(Colours.WHITE_20);
//            graphics.fill(r);

            graphics.setColor(Colours.WHITE);
            graphics.drawString(player.name(), r.x, r.y);
            for(int i = 0; i < player.hand().cards().size(); i++) {
                player.hand().cards().get(i).render(r.x + (25*i), r.y + 10);
            }
        }
    }
}
