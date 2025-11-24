package com.basketbandit.state.game;

import com.basketbandit.Engine;
import com.basketbandit.Renderer;
import com.basketbandit.component.Action;
import com.basketbandit.component.Deck;
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
    private final HashMap<String, Rectangle> buttons = new HashMap<>();
    private Rectangle selectedButton;
    private final HashMap<String, Rectangle> pointers = new HashMap<>();
    private final LinkedList<Player> players = new LinkedList<>();
    private final Dealer dealer;
    private Player playersTurn = new Player("placeholder");
    private boolean roundFinished;
    private boolean turnInProgress;
    private int cardDrawDelayMs = 500;

    public Blackjack() {
        this.deck = new Deck();

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

        for(Player player: players) {
            if(player.isHuman()) {
                Rectangle position = positions.get(players.indexOf(player));
                buttons.put("hit", new Rectangle(position.x, position.y + position.height + 10, 85, 40));
                buttons.put("stand", new Rectangle(position.x + (position.width/2)+3, position.y + position.height + 10, 85, 40));
            }
        }

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
    }

    @Override
    public void simulateTurn() {
        turnInProgress = true;
        try {
            for(Player player: players.reversed()) {
                // if player is out, fail fast
                if(player.isOut()) {
                    continue;
                }

                // if player is dealer, their action isn't to deal (get 2 cards) and all other players aren't out --- dealer only draws after rest of players have concluded their hands
                if(player instanceof Dealer && player.action() != Action.DEAL && !players.stream().filter(p -> !(p instanceof Dealer)).allMatch(Player::isOut)) {
                    continue;
                }

                playersTurn = player;

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

                while(player.isHuman() && player.action() == Action.WAITING) {
                    Thread.onSpinWait();
                }

                if(!player.isHuman()) { // dealer and npcs
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

                switch(player.action()) {
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
                    case Action.STAND -> player.setOut(true);
                }
            }

            if(dealer.hand().isBlackjack() || dealer.hand().isBust()) {
                dealer.setOut(true);
                this.roundFinished = true;
            }
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        playersTurn = new Player("placeholder");
        turnInProgress = false;
    }

    public void reset() {
        this.deck = new Deck();
        this.players.forEach(player -> {
            player.hand().reset();
            player.setAction(Action.DEAL);
            player.setOut(false);
        });
        roundFinished = false;
        turnInProgress = false;
        playersTurn = new Player("placeholder");
        simulateTurn();
    }

    @Override
    public void input(Input type, int[] id) {
        if(type == Input.MOUSE) {
            if(id[0] == Mouse.MOUSE_MOVED) {
                Rectangle point = new Rectangle(id[1], id[2], 1, 1);
                buttons.keySet().forEach(button -> {
                    if(buttons.get(button).intersects(point)) {
                        selectedButton = buttons.get(button);
                    }
                });
                return;
            }

            if(id[0] == Mouse.LEFT_CLICK) {
                Rectangle point = new Rectangle(id[1], id[2], 1, 1);
                buttons.keySet().forEach(button -> {
                    if(buttons.get(button).intersects(point)) {
                        for(Player player : players) {
                            if(player.isHuman() && player.hand().cards().size() > 1 && !player.isOut()) {
                                player.setAction(button.equals("stand") ? Action.STAND : Action.HIT);
                                if(!turnInProgress && !roundFinished && !dealer.hand().isBust() && !players.stream().allMatch(Player::isOut)) {
                                    simulateTurn();
                                    player.setAction(Action.WAITING);
                                }
                                if(player.action() == Action.STAND) {
                                    player.setOut(true);
                                }
                                return;
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
                for(Player player : players) {
                    if(player.isHuman()) {
                        if(selectedButton.equals(buttons.get("hit"))) {
                            player.setAction(Action.HIT);
                        }
                        if(selectedButton.equals(buttons.get("stand"))) {
                            player.setAction(Action.STAND);
                            player.setOut(true);
                        }
                    }
                }
            }
            if(dealer.hand().cards().size() > 1 && (id[0] == Keyboard.LEFT_ARROW || id[0] == Keyboard.RIGHT_ARROW)) {
                for(Player player : players) {
                    if(player.isHuman() && player.hand().cards().size() > 1 && !player.isOut()) {
                        selectedButton = (selectedButton == buttons.get("hit")) ? buttons.get("stand") : buttons.get("hit");
                        return;
                    }
                }
            }
            if(id[0] == Keyboard.E) {
                reset();
            }
        }
    }

    @Override
    public void update() {
        if(dealer.hand().isBust() || players.stream().allMatch(Player::isOut)) {
            roundFinished = true;
            return;
        }

        // simulate next turn
        if(Time.ticks() % 90 == 0) {
            if(!turnInProgress && !roundFinished) {
                simulateTurn();
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

            graphics.setColor(Colours.WHITE);
            graphics.draw(r);
            graphics.drawString(player.name(), r.x, r.y-10);
            for(int i = 0; i < player.hand().cards().size(); i++) {
                if(player instanceof Dealer && i == 0 && player.hand().cards().size() < 3 && !player.isOut()) {
                    graphics.drawImage(SpriteLibrary.instance("reverse"), r.x + 2, r.y + 2 + (int) (Math.sin(Time.slowTicks() + i) * 2), null);
                } else {
                    player.hand().cards().get(i).render(r.x + 2 + (25 * i), r.y + 2 + (int) (Math.sin(Time.slowTicks() + i) * 2));
                }
            }

            if(player.hand().isBlackjack()) {
                graphics.setColor(Colours.WHITE);
                int[] c = Fonts.centered(graphics, "Blackjack", r, Fonts.default20);
                graphics.drawString("Blackjack", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            }

            if(!player.equals(dealer) && !player.hand().isBlackjack() && player.action() == Action.STAND) {
                graphics.setColor(Colours.WHITE);
                int[] c = Fonts.centered(graphics, "Stand", r, Fonts.default20);
                graphics.drawString("Stand", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            }

            // players action buttons
            if(!roundFinished && playersTurn.equals(player) && player.isHuman() && player.action() != Action.DEAL && !player.isOut() && !dealer.hand().isBust() && dealer.hand().cards().size() > 1) {
                buttons.forEach((name, button) ->  {
                    graphics.setColor(name.equals("hit") ? Colours.BLUE : Colours.CRIMSON_75);
                    graphics.fill(button);

                    int[] c = Fonts.centered(graphics, name, button, Fonts.default20);
                    graphics.setColor(Colours.WHITE);
                    graphics.drawString(name, c[0], c[1] + (int) (Math.sin(Time.slowTicks()) * 2));

                    if(button.equals(selectedButton) && name.equals("hit")) {
                        pointers.get("pointer").setLocation(button.x + (button.width/2) - (pointers.get("pointer").width/2), c[1] + 15);
                        graphics.fill(pointers.get("pointer"));
                    }
                    if(button.equals(selectedButton) && name.equals("stand")) {
                        pointers.get("pointer").setLocation(button.x + (button.width/2) - (pointers.get("pointer").width/2), c[1] + 15);
                        graphics.fill(pointers.get("pointer"));
                    }
                });
            }

            if(playersTurn != null && playersTurn.equals(player)) {
                pointers.get("player-pointer").setLocation(r.x + r.width-10, r.y - 20);
                graphics.fill(pointers.get("player-pointer"));
            }
        }

        if(players.stream().allMatch(Player::isOut)) {
            graphics.drawString("Press 'E' to start the next hand!", 20, Renderer.height() - 20 - ((int) (Math.sin(Time.slowTicks()) * 2)));
        }
    }
}
