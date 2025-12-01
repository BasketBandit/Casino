package com.basketbandit.state.game;

import com.basketbandit.Engine;
import com.basketbandit.Renderer;
import com.basketbandit.component.Action;
import com.basketbandit.component.Card;
import com.basketbandit.component.Hand;
import com.basketbandit.component.Shoe;
import com.basketbandit.io.Input;
import com.basketbandit.io.Keyboard;
import com.basketbandit.io.Mouse;
import com.basketbandit.io.audio.AudioLibrary;
import com.basketbandit.player.AdvantagePlayer;
import com.basketbandit.player.Dealer;
import com.basketbandit.player.Player;
import com.basketbandit.ui.Button;
import com.basketbandit.util.Colours;
import com.basketbandit.util.Fonts;
import com.basketbandit.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Blackjack extends Banking implements Game {
    protected static final Logger log = LoggerFactory.getLogger(Blackjack.class);
    private final LinkedList<Rectangle> positions = new LinkedList<>();
    private final HashMap<String, Rectangle> pointers = new HashMap<>();
    private final HashMap<String, Button> actionButtons = new HashMap<>();
    private final HashMap<String, Button> betButtons = new HashMap<>();
    private Button selectedButton;

    private boolean autoplay = true;

    private boolean placingBets = true;
    private boolean handFinished = false;
    private boolean turnInProgress = false;
    private boolean betsSettled = false;
    private int minimumBet = 10;
    private int cardDrawDelayMs = 250; // 500

    private int handsWon;
    private int handsBlackjack;
    private int handsPushed;
    private int handsLost;

    private final LinkedList<Player> players = new LinkedList<>();
    private final Dealer dealer;
    private Player playersTurn = new Player("placeholder");
    private Hand playerActiveHand = new Hand(0);
    private Player human;

    public Blackjack() {
        this.deck = new Shoe(8);

        addPlayer(this.dealer = new Dealer());
        addPlayer(new AdvantagePlayer("Slick"));
        addPlayer(new Player("Jacob"));
        addPlayer(new Player("Josh", true));
        addPlayer(new Player("Ant"));
        addPlayer(new Player("Mark"));

        int space = (Renderer.width() - ((players.size()-1)*175)) / ((players.size()-1)+1); // calculates spacing between objects (width - (occurrences - 1) / occurrences)
        int middle = (players.size()-1)/2;
        positions.add(new Rectangle((Renderer.width() - 175) / 2, 100, 175, 100)); // dealers position
        for(int i = 0; i < (players.size()-1); i++) {
            positions.add(new Rectangle(space+(i*175)+(i*space), (Renderer.height() - 300 - (players.size() % 2 == 0 ? (Math.abs(i - middle)*50) : 0)), 175, 100));
        }
        // subtract the total width of your objects from the total length available, and then divide that result by the number of spaces between the objects

        if(human != null) {
            Rectangle position = positions.get(players.indexOf(human));
            actionButtons.put("hit", new Button("hit", position.x, position.y + position.height + 10, 85, 40));
            actionButtons.put("stand", new Button("stand", position.x + (position.width / 2) + 3, position.y + position.height + 10, 85, 40));
            actionButtons.put("split", new Button("split", position.x, position.y + position.height + 55, 85, 40));
            actionButtons.put("double", new Button("double", position.x + (position.width / 2) + 3, position.y + position.height + 55, 85, 40));
            actionButtons.put("surrender", new Button("surrender", position.x, position.y + position.height + 100, 175, 40));
            betButtons.put("10", new Button("10", position.x, position.y + position.height + 10, 40, 40));
            betButtons.put("25", new Button("25", position.x + 45, position.y + position.height + 10, 40, 40));
            betButtons.put("50", new Button("50", position.x + 90, position.y + position.height + 10, 40, 40));
            betButtons.put("100", new Button("100", position.x + 135, position.y + position.height + 10, 40, 40));
            betButtons.put("sans", new Button("sans", position.x, position.y + position.height + 55, 175, 40));
        }

        pointers.put("mouse", new Rectangle(0, 0, 1, 1));
        pointers.put("pointer", new Rectangle(0, 0, 10, 10));
        pointers.put("player-pointer", new Rectangle(0, 0, 10, 10));
    }

    public void addPlayers(ArrayList<Player> players) {
        for(Player player: players) {
            addPlayer(player);
        }
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        if(player.isHuman()) {
            this.human = player;
        }
    }

    public void placeBets() {
        dealer.addHand(new Hand(0));
        dealer.setAction(Action.DEAL);

        for(Player player: players.reversed()) {
            playersTurn = player;

            if(!player.equals(dealer)) {
                // decide actions for dealer and npcs
                if(!player.isHuman()) {
                    if(player.currency() >= minimumBet) {
                        player.setAction(Action.BET);
                    } else {
                        player.setAction(Action.SANS);
                    }
                }

                // don't wait for bet if player can't
                if(player.currency() < minimumBet && player.hands().isEmpty()) {
                    player.setOut(true);
                    player.setAction(Action.STAND);
                }

                // wait for player input
                while(!player.isOut() && player.isHuman() && player.action() == Action.WAITING) {
                    Thread.onSpinWait();
                }

                // resolve actions for all players
                if(player.action() == Action.BET) {
                    if(player instanceof AdvantagePlayer advantagePlayer) {
                        switch(deck.trueCount()) {
                            case 1 -> advantagePlayer.placeBet(25);
                            case 2 -> advantagePlayer.placeBet(50);
                            case 3 -> advantagePlayer.placeBet(100);
                            case 5 -> advantagePlayer.placeBet(250);
                            case 6 -> advantagePlayer.placeBet(500);
                            case 7, 8, 9, 10, 11, 12 -> advantagePlayer.placeBet(1000);
                            default -> advantagePlayer.placeBet(10);
                        }
                        player.setAction(Action.DEAL);
                        continue;
                    }
                    if(player.placeBet(minimumBet)) {
                        player.setAction(Action.DEAL);
                    }
                    continue;
                }

                if(player.action() == Action.SANS) {
                    player.setOut(true);
                }
            }
        }
    }

    @Override
    public void simulateTurn() {
        turnInProgress = true;
        try {
            for(Player player: players.reversed()) {
                boolean split = false;
                Hand splitHand = null;
                playersTurn = player;

                // if player is out, fail fast
                if(player.isOut()) {
                    continue;
                }

                // if player is dealer, their action isn't to deal (get 2 cards) and all other players aren't out --- dealer only draws after rest of players have concluded their hands
                if(player instanceof Dealer && player.action() != Action.DEAL && !players.stream().filter(p -> !(p instanceof Dealer)).allMatch(Player::isOut)) {
                    continue;
                }

                // figure out if players are out
                if(player.hands().stream().allMatch(hand -> hand.isBlackjack() || hand.isBust())) {
                    player.setOut(true);
                    for(Hand hand: player.hands()) {
                        hand.setAction(Action.STAND);
                    }
                    continue;
                }

                for(Hand hand: player.hands()) {
                    playerActiveHand = hand;

                    // set playing players actions from deal to waiting
                    if(player.isHuman() && !player.mainHand().isEmpty()) {
                        hand.setAction(Action.READY);
                    }

                    // wait for player input
                    while(player.isHuman() && hand.action() == Action.READY) {
                        Thread.onSpinWait();
                    }

                    // decide actions for dealer and npcs
                    if(!player.isHuman()) {
                        if(hand.equals(player.mainHand()) && player.mainHand().isEmpty()) {
                            hand.setAction(Action.DEAL);
                        } else {
                            if(player instanceof AdvantagePlayer advantage) {
                                hand.setAction(advantage.decideAction(hand, dealer.visibleCard()));
                            } else {
                                if(hand.value() < 17) {
                                    hand.setAction(Action.HIT);
                                } else {
                                    hand.setAction(Action.STAND);
                                }
                            }
                        }
                    }

                    // resolve actions for all players
                    switch(hand.action()) {
                        case Action.STAND -> {
                            if(player.equals(dealer)) {
                                dealer.revealCard();
                                Thread.sleep(cardDrawDelayMs);
                            }
                            player.setOut(true);
                        }
                        case Action.DEAL -> {
                            Card[] card = deck.draw(2);
                            if(player.equals(dealer)) {
                                card[0].flip(); // dealers first card is always flipped
                            }
                            for(int i = 0; i < 2; i++) {
                                hand.addCard(card[i]);
                                AudioLibrary.effect("deal1").play(-15);
                                Thread.sleep(cardDrawDelayMs);
                            }
                            player.setAction(Action.READY);
                        }
                        case Action.HIT -> {
                            if(player.equals(dealer)) {
                                dealer.revealCard();
                                Thread.sleep(cardDrawDelayMs);
                            }
                            hand.addCard(deck.draw(1));
                            AudioLibrary.effect("deal1").play(-15);
                            Thread.sleep(cardDrawDelayMs);
                            player.setAction(Action.READY);
                        }
                        case Action.DOUBLE -> {
                            player.doubleBet(hand);
                            hand.addCard(deck.draw(1));
                            player.setAction(Action.STAND);
                        }
                        /// ////// WIP WIP WIP
                        case Action.SPLIT ->  {
                            split = true;
                            splitHand = hand;
                        }
                    }
                }

                // deal with splitting outside of loop (concurrent modification)
                if(split && splitHand != null) {
                    player.placeBet(splitHand.bet());
                    Card card = splitHand.cards().removeFirst();
                    player.hands().getLast().addCard(card);
                    AudioLibrary.effect("deal1").play(-15);
                    Thread.sleep(cardDrawDelayMs);
                    splitHand.addCard(deck.draw(1));
                    AudioLibrary.effect("deal1").play(-15);
                    Thread.sleep(cardDrawDelayMs);
                }
            }

            if(dealer.mainHand().isBlackjack() || dealer.mainHand().isBust()) {
                dealer.setOut(true);
                this.handFinished = true;
            }
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        playersTurn = new Player("placeholder");
        turnInProgress = false;
    }

    public void calculatePayout() {
        int dealerHandValue = dealer.mainHand().value();
        boolean dealerBlackJack = dealer.mainHand().isBlackjack();
        boolean dealerBust = dealer.mainHand().isBust();
        players.stream().filter(player -> !player.equals(dealer) && !player.hands().isEmpty()).forEach(player -> player.hands().forEach(hand -> {
            if(hand.isBust()) {
                handsLost++;
                return;
            }
            if(dealerBlackJack && hand.isBlackjack()) {
                player.addCurrency(hand.bet());
                handsPushed++;
                return;
            }
            if(hand.value() == dealerHandValue) {
                player.addCurrency(hand.bet());
                handsPushed++;
                return;
            }
            if(hand.isBlackjack() && !dealerBlackJack) {
                player.addCurrency((int) (hand.bet()*2.5));
                handsBlackjack++;
                return;
            }
            if(hand.value() > dealerHandValue) {
                player.addCurrency(hand.bet()*2);
                handsWon++;
                return;
            }
            if(dealerBust) {
                player.addCurrency(hand.bet()*2);
                handsWon++;
                return;
            }
            handsLost++;
        }));
    }

    public void reset() {
        this.deck = new Shoe(8);
    }

    public void nextHand() {
        this.players.forEach(player -> {
            player.clearHands();
            player.setAction(Action.WAITING);
            player.setOut(false);
        });
        playersTurn = new Player("placeholder");
        handFinished = false;
        turnInProgress = false;
        placingBets = true;
        betsSettled = false;
        if(this.deck instanceof Shoe shoe) {
            if(shoe.shouldChangeShoe()) {
                reset();
            }
        }
    }

    @Override
    public void input(Input type, int[] id) {
        if(type == Input.MOUSE && id[0] == Mouse.MOUSE_MOVED) {
            pointers.get("mouse").setLocation(id[1], id[2]);
        }

        // allow next hand even if not players turn
        if(handFinished && type == Input.KEYBOARD && id[0] == Keyboard.E) {
            nextHand();
            return;
        }

        if(!playersTurn.equals(human) || human.isOut()) {
            return;
        }

        if(type == Input.MOUSE) {
            if(id[0] == Mouse.MOUSE_MOVED) {
                pointers.get("mouse").setLocation(id[1], id[2]);
                Rectangle point = pointers.get("mouse");

                actionButtons.forEach((_, button) -> {
                    if(!placingBets && button.intersects(point)) {
                        selectedButton = button;
                    }
                });
                betButtons.forEach((_, button) -> {
                    if(placingBets && button.intersects(point)) {
                        selectedButton = button;
                    }
                });
                return;
            }

            if(id[0] == Mouse.LEFT_CLICK) {
                pointers.get("mouse").setLocation(id[1], id[2]);
                Rectangle point = pointers.get("mouse");

                if(placingBets) {
                    betButtons.forEach((name, button) -> {
                        if(point.intersects(button)) {
                            switch(name) {
                                case "10", "25", "50", "100" -> {
                                    if(human.placeBet(Integer.parseInt(name))) {
                                        human.mainHand().setAction(Action.DEAL);
                                        human.setAction(Action.READY); // hack to get things moving again
                                    }
                                }
                                case "sans" -> {
                                    human.setOut(true);
                                    human.mainHand().setAction(Action.STAND);
                                }
                            }
                        }
                    });
                    return;
                }

                if(turnInProgress) {
                    actionButtons.forEach((name, button) -> {
                        if(point.intersects(button)) {
                            if(human.hands().stream().anyMatch(hand -> hand.cards().size() > 1) && !human.isOut()) {
                                switch(name) {
                                    case "hit" -> playerActiveHand.setAction(Action.HIT);
                                    case "stand" -> playerActiveHand.setAction(Action.STAND);
                                    case "split" -> playerActiveHand.setAction(Action.SPLIT);
                                    case "double" -> playerActiveHand.setAction(Action.DOUBLE);
                                }
                            }
                        }
                    });
                }
            }
            return;
        }

        if(type == Input.KEYBOARD) {
            if(id[0] == Keyboard.ENTER) {
                if(selectedButton.equals(actionButtons.get("hit"))) {
                    playerActiveHand.setAction(Action.HIT);
                }
                if(selectedButton.equals(actionButtons.get("stand"))) {
                    playerActiveHand.setAction(Action.STAND);
                }
            }
            if(id[0] == Keyboard.LEFT_ARROW || id[0] == Keyboard.RIGHT_ARROW) {
                selectedButton = (selectedButton == actionButtons.get("hit")) ? actionButtons.get("stand") : actionButtons.get("hit");
            }
        }
    }

    @Override
    public void update() {
        // simulate next turn
        if(placingBets) {
            placeBets();
            placingBets = false;
        }
        if(dealer.mainHand().isBust() || players.stream().allMatch(Player::isOut)) {
            handFinished = true;
        }
        if(handFinished && !betsSettled) {
            calculatePayout();
            betsSettled = true;
        }
        if(Time.ticks() % 10 == 0 && !turnInProgress && !handFinished && !placingBets) {
            simulateTurn();
        }

        // auto play
        if(autoplay) {
            if(!turnInProgress && !placingBets && handFinished && betsSettled) {
                nextHand();
            }
        }
    }

    @Override
    public void render() {
        Graphics2D graphics = Renderer.graphics();
        graphics.setColor(Colours.GREEN_75);
        graphics.fillRect(0, 0, Renderer.width(), Renderer.height());
        graphics.setFont(Fonts.default20);
        graphics.setColor(Colours.WHITE);
        graphics.drawString("Blackjack", 50, 50);
        graphics.drawString("fps: " + Engine.framerate(), 50, 80);

        for(Player player: players) {
            int index = players.indexOf(player);
            Rectangle r = positions.get(index);

            // player card box
            graphics.setColor(Colours.WHITE);
            graphics.draw(r);

            // player names and funds
            if(!player.equals(dealer)) {
                graphics.drawRect(r.x, r.y - 60, 25, 25);
                graphics.drawString(player.hands().isEmpty() ? "" : player.mainHand().bet() + "", r.x + 1, r.y - 38);
                graphics.drawString(player.name() + " - $" + player.currency(), r.x, r.y - 10);
            }

            if(player.hands().isEmpty()) {
                continue;
            }

            // player cards
            for(int h = 0; h < player.hands().size(); h++) {
                for(int i = 0; i < player.hands().get(h).cards().size(); i++) {
                    player.hands().get(h).cards().get(i).render(r.x + 2 + (25 * i), (r.y + (h*100)) + 2 + (int) (Math.sin(Time.slowTicks() + i) * 2));
                }
            }

            // if blackjack
            if(player.mainHand().isBlackjack()) {
                graphics.setColor(Colours.WHITE);
                int[] c = Fonts.centered(graphics, "Blackjack", r, Fonts.default20);
                graphics.drawString("Blackjack", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            }

            // if bust or stand
            graphics.setColor(Colours.WHITE);
            if(player.mainHand().isBust()) {
                int[] c = Fonts.centered(graphics, "Bust", r, Fonts.default20);
                graphics.drawString("Bust", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            } else if(!player.equals(dealer) && !player.mainHand().isBlackjack() && player.mainHand().action() == Action.STAND) {
                int[] c = Fonts.centered(graphics, "Stand", r, Fonts.default20);
                graphics.drawString("Stand", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            }

            // player turn pointer
            if(playersTurn != null && playersTurn.equals(player)) {
                pointers.get("player-pointer").setLocation(r.x + r.width-10, r.y - 20);
                graphics.fill(pointers.get("player-pointer"));
            }
        }

        // players action buttons
        if(!handFinished && playersTurn != null && playersTurn.equals(human) && !human.isOut() && !dealer.mainHand().isBust()) {
            actionButtons.forEach((_, button) -> {
                // hit & stand buttons
                if(!placingBets && turnInProgress && dealer.mainHand().cards().size() >= 2) {
                    button.render(button.equals(selectedButton) ? Colours.BLUE : Colours.CRIMSON_75);
                }
            });
            betButtons.forEach((name, button) -> {
                // bet buttons
                if(placingBets && human.action() == Action.WAITING) {
                    Color colour = name.equals("sans")
                            ? (button.equals(selectedButton) ? Colours.BLUE : Colours.CRIMSON_75)
                            : ((human.currency() < Integer.parseInt(name)) ? Colours.DARK_GREY_75 : button.equals(selectedButton) ? Colours.BLUE : Colours.CRIMSON_75);
                    button.render(colour);
                }
            });
        }

        // advantage play stats
        graphics.drawString(deck.runningCount() + " / " + deck.trueCount(), Renderer.width() - 100, 50);
        graphics.drawString(deck.cards().size() + "",Renderer.width() - 100, 75);

        graphics.drawString(handsWon + " - W",Renderer.width() - 100, 120);
        graphics.drawString(handsBlackjack + " - B",Renderer.width() - 100, 145);
        graphics.drawString(handsPushed + " - P",Renderer.width() - 100, 170);
        graphics.drawString(handsLost + " - L",Renderer.width() - 100, 195);

        if(players.stream().allMatch(Player::isOut)) {
            graphics.drawString("Press 'E' to start the next hand!", 20, Renderer.height() - 20 - ((int) (Math.sin(Time.slowTicks()) * 2)));
        }
    }
}
