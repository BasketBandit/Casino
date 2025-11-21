package com.basketbandit.game;

import com.basketbandit.Renderer;
import com.basketbandit.component.Action;
import com.basketbandit.component.Deck;
import com.basketbandit.io.Keyboard;
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
    private final HashMap<String, Rectangle> pointers = new HashMap<>();
    private final LinkedList<Player> players = new LinkedList<>();
    private final Dealer dealer;
    private boolean roundFinished;
    private boolean turnInProgress;

    public Blackjack() {
        this.deck = new Deck();

        addPlayer(this.dealer = new Dealer());
        addPlayer(new Player("Julian"));
        addPlayer(new Player("Jacob"));
        addPlayer(new Player("Josh", true));
        addPlayer(new Player("Ant"));
        addPlayer(new Player("Mark"));

        int dealer = (Renderer.width() - 175) / 2; // calculates spacing between objects
        int space = (Renderer.width() - ((players.size()-1)*175)) / ((players.size()-1)+1); // calculates spacing between objects
        positions.add(new Rectangle(dealer, 150, 175, 100)); // dealers position
        for(int i = 0; i < (players.size()-1); i++) {
            positions.add(new Rectangle(space+(i*175)+(i*space), (Renderer.height() - 250), 175, 100));
        }
        // subtract the total width of your objects from the total length available, and then divide that result by the number of spaces between the objects

        for(Player player: players) {
            if(player.isPlaying()) {
                int index = players.indexOf(player)-1;
                buttons.put("hit", new Rectangle( space+(index*175)+(index*space), (Renderer.height() - 145), 85, 40));
                buttons.put("stand", new Rectangle(space+(index*175)+(index*space) + (175/2)+3, (Renderer.height() - 145), 85, 40));
            }
        }
        pointers.put("pointer", new Rectangle(0, 0, 10, 10));
    }

    // TODO:
    // WIN CONDITIONS, CHECK STATE OF DEALER COMPARED TO OTHERS

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
                if(dealer.hand().isBust()) {
                    break;
                }
                if(player instanceof Dealer && player.action() != Action.DEAL && !players.stream().filter(p -> !(p instanceof Dealer)).allMatch(Player::isOut)) {
                    // dealer doesn't reveal card or start drawing until rest of the players have completed their hands
                    continue;
                }
                if(player.isOut()) {
                    continue;
                }
                switch(player.action()) {
                    case Action.DEAL -> {
                        player.hand().addCard(deck.draw(1));
                        Thread.sleep(250);
                        player.hand().addCard(deck.draw(1));
                        Thread.sleep(250);
                    }
                    case Action.HIT -> {
                        player.hand().addCard(deck.draw(1));
                        Thread.sleep(250);
                    }
                    case Action.STAND -> player.setOut(true);
                }
            }
            if(dealer.hand().isBlackjack()) {
                this.roundFinished = true;
            }
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        simulateTurn();
    }

    @Override
    public void input(int key) {
        if(key == Keyboard.ENTER) {
            if(!turnInProgress && !roundFinished && !dealer.hand().isBust() && !players.stream().allMatch(Player::isOut)) {
                simulateTurn();
            }
            for(Player player: players) {
                if(player.isPlaying()) {
                    if(player.action() == Action.STAND) {
                        player.setOut(true);
                    }
                }
            }
        }
        if(key == Keyboard.LEFT_ARROW || key == Keyboard.RIGHT_ARROW) {
            for(Player player: players) {
                if(player.isPlaying() && player.hand().cards().size() > 1 && !player.isOut()) {
                    player.setAction(player.action() == Action.HIT ? Action.STAND : Action.HIT);
                }
            }
        }
        if(key == Keyboard.E) {
            reset();
        }
    }

    @Override
    public void update() {
        Time.increment();

        if(dealer.hand().isBust() || players.stream().allMatch(Player::isOut)) {
            roundFinished = true;
            return;
        }

        for(Player player: players) {
            // set playing players actions from deal to hit
            if(player.isPlaying() && player.action() == Action.DEAL && !player.hand().isEmpty()) {
                player.setAction(Action.HIT);
                continue;
            }

            // figure out if players are out
            if(!player.isOut() && (player.hand().isBlackjack() || player.hand().isBust())) {
                player.setOut(true);
            }

            if(!player.isPlaying()) { // dealer and npcs
                if(player.hand().cards().isEmpty()) {
                    player.setAction(Action.DEAL);
                    continue;
                }
                if(player.hand().value() < 17) {
                    player.setAction(Action.HIT);
                    continue;
                }
                player.setAction(Action.STAND);
            }
        }

        // simulate rest of game if player is out
        if(Time.ticks() % 180 == 0) {
            for(Player player: players) {
                if(!turnInProgress && !roundFinished && player.isPlaying() && player.isOut()) {
                    simulateTurn();
                    return;
                }
            }
        }
    }

    @Override
    public void render() {
        Graphics2D graphics = Renderer.graphics();
        graphics.setColor(Colours.GREEN_75);
        graphics.fillRect(0, 0, Renderer.width(), Renderer.height());
        graphics.setFont(Fonts.default24);
        graphics.setColor(Colours.WHITE);
        graphics.drawString("Blackjack", 50, 50);

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
                int[] c = Fonts.centered(graphics, "Blackjack", r, Fonts.default24);
                graphics.drawString("Blackjack", c[0], r.y + 125 + (int) (Math.sin(Time.slowTicks()) * 2));
            }

            // players action buttons
            if(!roundFinished && player.isPlaying() && player.action() != Action.DEAL && !player.isOut() && !dealer.hand().isBust()) {
                buttons.forEach((name, button) ->  {
                    graphics.setColor(name.equals("hit") ? Colours.BLUE : Colours.CRIMSON_75);
                    graphics.fill(button);

                    int[] c = Fonts.centered(graphics, name, button, Fonts.default24);
                    graphics.setColor(Colours.WHITE);
                    graphics.drawString(name, c[0], c[1] + (int) (Math.sin(Time.slowTicks()) * 2));

                    if(player.action() == Action.HIT && name.equals("hit")) {
                        pointers.get("pointer").setLocation(button.x + (button.width/2) - (pointers.get("pointer").width/2), c[1] + 15);
                        graphics.fill(pointers.get("pointer"));
                    }
                    if(player.action() == Action.STAND && name.equals("stand")) {
                        pointers.get("pointer").setLocation(button.x + (button.width/2) - (pointers.get("pointer").width/2), c[1] + 15);
                        graphics.fill(pointers.get("pointer"));
                    }
                });
            }
        }
    }
}
