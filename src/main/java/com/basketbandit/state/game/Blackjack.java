package com.basketbandit.state.game;

import com.basketbandit.Engine;
import com.basketbandit.Renderer;
import com.basketbandit.component.Action;
import com.basketbandit.component.Hand;
import com.basketbandit.component.Shoe;
import com.basketbandit.io.Input;
import com.basketbandit.io.Keyboard;
import com.basketbandit.io.Mouse;
import com.basketbandit.io.audio.AudioLibrary;
import com.basketbandit.io.image.SpriteLibrary;
import com.basketbandit.player.Dealer;
import com.basketbandit.player.Player;
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
    private final HashMap<String, Rectangle> buttons = new HashMap<>();
    private Rectangle selectedButton;

    private boolean placingBets = true;
    private boolean handFinished = false;
    private boolean turnInProgress = false;
    private boolean betsSettled = false;
    private int minimumBet = 10;
    private int cardDrawDelayMs = 500; // 500

    private final LinkedList<Player> players = new LinkedList<>();
    private final Dealer dealer;
    private Player playersTurn = new Player("placeholder");
    private Player human;

    public Blackjack() {
        this.deck = new Shoe(8);

        addPlayer(this.dealer = new Dealer());
        addPlayer(new Player("Julian"));
        addPlayer(new Player("Jacob"));
        addPlayer(new Player("Josh", true));
        addPlayer(new Player("Ant"));
        addPlayer(new Player("Mark"));

        int space = (Renderer.width() - ((players.size()-1)*175)) / ((players.size()-1)+1); // calculates spacing between objects (width - (occurrences - 1) / occurrences)
        int middle = (players.size()-1)/2;
        positions.add(new Rectangle((Renderer.width() - 175) / 2, 150, 175, 100)); // dealers position
        for(int i = 0; i < (players.size()-1); i++) {
            positions.add(new Rectangle(space+(i*175)+(i*space), (Renderer.height() - 250 - (players.size() % 2 == 0 ? (Math.abs(i - middle)*50) : 0)), 175, 100));
        }
        // subtract the total width of your objects from the total length available, and then divide that result by the number of spaces between the objects

        if(human != null) {
            Rectangle position = positions.get(players.indexOf(human));
            buttons.put("hit", new Rectangle(position.x, position.y + position.height + 10, 85, 40));
            buttons.put("stand", new Rectangle(position.x + (position.width / 2) + 3, position.y + position.height + 10, 85, 40));
            buttons.put("10", new Rectangle(position.x, position.y + position.height + 10, 40, 40));
            buttons.put("25", new Rectangle(position.x + 45, position.y + position.height + 10, 40, 40));
            buttons.put("50", new Rectangle(position.x + 90, position.y + position.height + 10, 40, 40));
            buttons.put("100", new Rectangle(position.x + 135, position.y + position.height + 10, 40, 40));
            buttons.put("sans", new Rectangle(position.x, position.y + position.height + 55, 175, 40));
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
                if(!player.isOut() && (player.hand().isBlackjack() || player.hand().isBust())) {
                    player.setOut(true);
                    player.setAction(Action.STAND);
                    continue;
                }

                // set playing players actions from deal to hit
                if(player.isHuman() && player.action() == Action.DEAL && !player.hand().isEmpty()) {
                    player.setAction(Action.WAITING);
                }

                // wait for player input
                while(player.isHuman() && player.action() == Action.WAITING) {
                    Thread.onSpinWait();
                }

                // decide actions for dealer and npcs
                if(!player.isHuman()) {
                    if(player.hand().cards().isEmpty()) {
                        player.setAction(Action.DEAL);
                    } else {
                        if(player.hand().value() < 17) {
                            player.setAction(Action.HIT);
                        } else {
                            player.setAction(Action.STAND);
                        }
                    }
                }

                // resolve actions for all players
                switch(player.action()) {
                    case Action.STAND -> player.setOut(true);
                    case Action.DEAL -> {
                        player.hand().addCard(deck.draw(1));
                        AudioLibrary.effect("deal1").play(-15);
                        Thread.sleep(cardDrawDelayMs);
                        player.hand().addCard(deck.draw(1));
                        AudioLibrary.effect("deal1").play(-15);
                        Thread.sleep(cardDrawDelayMs);
                        player.setAction(Action.WAITING);
                    }
                    case Action.HIT -> {
                        player.hand().addCard(deck.draw(1));
                        AudioLibrary.effect("deal1").play(-15);
                        Thread.sleep(cardDrawDelayMs);
                        player.setAction(Action.WAITING);
                    }
                }
            }

            if(dealer.hand().isBlackjack() || dealer.hand().isBust()) {
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
        int dealerHandValue = dealer.hand().value();
        boolean dealerBlackJack = dealer.hand().isBlackjack();
        boolean dealerBust = dealer.hand().isBust();
        players.stream().filter(player -> !player.equals(dealer) && !player.hands().isEmpty()).forEach(player -> player.hands().forEach(hand -> {
            if(hand.isBust()) {
                return;
            }
            if(dealerBlackJack && hand.isBlackjack()) {
                player.addCurrency(hand.bet());
                return;
            }
            if(hand.value() == dealerHandValue) {
                player.addCurrency(hand.bet());
                return;
            }
            if(hand.isBlackjack() && !dealerBlackJack) {
                player.addCurrency((int) (hand.bet()*1.5));
                return;
            }
            if(hand.value() > dealerHandValue) {
                player.addCurrency(hand.bet()*2);
                return;
            }
            if(dealerBust) {
                player.addCurrency(hand.bet()*2);
                return;
            }
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

                buttons.forEach((name, button) -> {
                    if(placingBets && button.intersects(point) && (!name.equals("hit") && !name.equals("stand"))) {
                        selectedButton = button;
                    }
                    if(!placingBets && button.intersects(point) && (name.equals("hit") || name.equals("stand"))) {
                        selectedButton = button;
                    }
                });
                return;
            }

            if(id[0] == Mouse.LEFT_CLICK) {
                pointers.get("mouse").setLocation(id[1], id[2]);
                Rectangle point = pointers.get("mouse");

                buttons.forEach((name, button) -> {
                    if(placingBets && button.intersects(point) && (!name.equals("hit") && !name.equals("stand"))) {
                        switch(name) {
                            case "10", "25", "50", "100" -> {
                                if(human.placeBet(Integer.parseInt(name))) {
                                    human.setAction(Action.DEAL);
                                }
                            }
                            case "sans" -> {
                                human.setOut(true);
                                human.setAction(Action.STAND);
                            }
                        }
                        return;
                    }
                    if(!placingBets && button.intersects(point) && (name.equals("hit") || name.equals("stand"))) {
                        if(human.hand().cards().size() > 1 && !human.isOut()) {
                            human.setAction(name.equals("stand") ? Action.STAND : Action.HIT);
                            if(human.action() == Action.STAND) {
                                human.setOut(true);
                            }
                        }
                    }
                });
            }
            return;
        }

        if(type == Input.KEYBOARD) {
            if(id[0] == Keyboard.ENTER) {
                // if(!turnInProgress && !roundFinished && !dealer.hand().isBust() && !players.stream().allMatch(Player::isOut)) {
                //     simulateTurn();
                // }
                if(selectedButton.equals(buttons.get("hit"))) {
                    human.setAction(Action.HIT);
                }
                if(selectedButton.equals(buttons.get("stand"))) {
                    human.setAction(Action.STAND);
                    human.setOut(true);
                }
            }
            if(id[0] == Keyboard.LEFT_ARROW || id[0] == Keyboard.RIGHT_ARROW) {
                selectedButton = (selectedButton == buttons.get("hit")) ? buttons.get("stand") : buttons.get("hit");
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
        if(dealer.hand().isBust() || players.stream().allMatch(Player::isOut)) {
            handFinished = true;
        }
        if(handFinished && !betsSettled) {
            calculatePayout();
            betsSettled = true;
        }
        if(Time.ticks() % 10 == 0 && !turnInProgress && !handFinished && !placingBets) {
            simulateTurn();
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
                graphics.drawString(player.hands().isEmpty() ? "" : player.hand().bet() + "", r.x + 1, r.y - 38);
                graphics.drawString(player.name() + " - $" + player.currency(), r.x, r.y - 10);
            }

            if(player.hands().isEmpty()) {
                continue;
            }

            // player cards
            for(int i = 0; i < player.hand().cards().size(); i++) {
                if(player instanceof Dealer && i == 0 && player.hand().cards().size() < 3 && !player.isOut()) {
                    graphics.drawImage(SpriteLibrary.instance("reverse"), r.x + 2, r.y + 2 + (int) (Math.sin(Time.slowTicks() + i) * 2), null);
                } else {
                    player.hand().cards().get(i).render(r.x + 2 + (25 * i), r.y + 2 + (int) (Math.sin(Time.slowTicks() + i) * 2));
                }
            }

            // bet value
            if(!player.equals(dealer)) {

            }

            // if blackjack
            if(player.hand().isBlackjack()) {
                graphics.setColor(Colours.WHITE);
                int[] c = Fonts.centered(graphics, "Blackjack", r, Fonts.default20);
                graphics.drawString("Blackjack", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            }

            // if bust or stand
            graphics.setColor(Colours.WHITE);
            if(player.hand().isBust()) {
                int[] c = Fonts.centered(graphics, "Bust", r, Fonts.default20);
                graphics.drawString("Bust", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            } else if(!player.equals(dealer) && !player.hand().isBlackjack() && player.action() == Action.STAND) {
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
        if(!handFinished && playersTurn != null && playersTurn.equals(human) && !human.isOut() && !dealer.hand().isBust()) {
            buttons.forEach((name, button) -> {
                // hit & stand buttons
                if(!placingBets && turnInProgress && dealer.hand().cards().size() >= 2 && (name.equals("hit") || name.equals("stand"))) {
                    graphics.setColor(name.equals("hit") ? Colours.BLUE : Colours.CRIMSON_75);
                    graphics.fill(button);

                    int[] c = Fonts.centered(graphics, name, button, Fonts.default20);
                    graphics.setColor(Colours.WHITE);
                    graphics.drawString(name, c[0], c[1] + (int) (Math.sin(Time.slowTicks()) * 2));

                    if(button.equals(selectedButton)) {
                        pointers.get("pointer").setLocation(button.x + (button.width / 2) - (pointers.get("pointer").width / 2), c[1] + 15);
                        graphics.fill(pointers.get("pointer"));
                    }
                }
                // bet buttons
                if(placingBets && human.action() == Action.WAITING && (!name.equals("hit") && !name.equals("stand"))) {
                    if(name.equals("sans")) {
                        graphics.setColor(button.equals(selectedButton) ? Colours.BLUE : Colours.CRIMSON_75);
                    } else {
                        graphics.setColor(human.currency() < Integer.parseInt(name) ? Colours.DARK_GREY_75 : button.equals(selectedButton) ? Colours.BLUE : Colours.CRIMSON_75);
                    }
                    graphics.fill(button);
                    int[] c = Fonts.centered(graphics, name, button, Fonts.default20);
                    graphics.setColor(Colours.WHITE);
                    graphics.drawString(name, c[0], c[1] + (int) (Math.sin(Time.slowTicks()) * 2));
                }
            });
        }

        if(players.stream().allMatch(Player::isOut)) {
            graphics.drawString("Press 'E' to start the next hand!", 20, Renderer.height() - 20 - ((int) (Math.sin(Time.slowTicks()) * 2)));
        }
    }
}
