package com.basketbandit.component;

import com.basketbandit.Renderable;
import com.basketbandit.io.image.SpriteManager;

import java.awt.*;

public class Card extends Rectangle implements Renderable {
    public SpriteManager<Card> spriteManager;
    private final int id;
    private final String name;
    private final int value;
    private final Suit suit;

    public Card(int id, int value, Suit suit) {
        super(71, 95);
        this.id = id;
        this.value = value;
        this.suit = suit;
        switch(id) {
            case 1 -> name = "Ace";
            case 11 -> name = "Jack";
            case 12 -> name = "Queen";
            case 13 -> name = "King";
            default -> name = id + "";
        }
        this.spriteManager = new SpriteManager<>(this);
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int value() {
        return value;
    }

    public Suit suit() {
        return suit;
    }

    @Override
    public String toString() {
        return name() + "-" + suit.name();
    }

    public void render(int x, int y) {
        graphics.drawImage(this.spriteManager.override("missing"), x, y, null);
        graphics.drawImage(this.spriteManager.sprite(), x, y, null);
    }
}
