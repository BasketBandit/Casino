package com.basketbandit.io.image;

import com.basketbandit.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteLibrary {
    private static final Logger log = LoggerFactory.getLogger(SpriteLibrary.class);
    private static final ClassLoader classLoader = State.class.getClassLoader();

    private static final HashMap<String, BufferedImage> spriteSheets;
    static {
        try {
            spriteSheets = new HashMap<>(Map.ofEntries(
                    Map.entry("deck-number", ImageIO.read(classLoader.getResourceAsStream("./assets/img/8BitDeck.png"))),
                    Map.entry("deck-background", ImageIO.read(classLoader.getResourceAsStream("./assets/img/Enhancers.png")))
            ));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final HashMap<String, BufferedImage> sprites;
    static {
        sprites = new HashMap<>(Map.ofEntries(
                Map.entry("missing", spriteSheets.get("deck-background").getSubimage(71, 0, 71, 95)),
                Map.entry("2-HEARTS", spriteSheets.get("deck-number").getSubimage(0, 0, 71, 95)),
                Map.entry("3-HEARTS", spriteSheets.get("deck-number").getSubimage(71, 0, 71, 95)),
                Map.entry("4-HEARTS", spriteSheets.get("deck-number").getSubimage(71*2, 0, 71, 95)),
                Map.entry("5-HEARTS", spriteSheets.get("deck-number").getSubimage(71*3, 0, 71, 95)),
                Map.entry("6-HEARTS", spriteSheets.get("deck-number").getSubimage(71*4, 0, 71, 95)),
                Map.entry("7-HEARTS", spriteSheets.get("deck-number").getSubimage(71*5, 0, 71, 95)),
                Map.entry("8-HEARTS", spriteSheets.get("deck-number").getSubimage(71*6, 0, 71, 95)),
                Map.entry("9-HEARTS", spriteSheets.get("deck-number").getSubimage(71*7, 0, 71, 95)),
                Map.entry("10-HEARTS", spriteSheets.get("deck-number").getSubimage(71*8, 0, 71, 95)),
                Map.entry("Jack-HEARTS", spriteSheets.get("deck-number").getSubimage(71*9, 0, 71, 95)),
                Map.entry("Queen-HEARTS", spriteSheets.get("deck-number").getSubimage(71*10, 0, 71, 95)),
                Map.entry("King-HEARTS", spriteSheets.get("deck-number").getSubimage(71*11, 0, 71, 95)),
                Map.entry("Ace-HEARTS", spriteSheets.get("deck-number").getSubimage(71*12, 0, 71, 95)),
                Map.entry("2-CLUBS", spriteSheets.get("deck-number").getSubimage(0, 95, 71, 95)),
                Map.entry("3-CLUBS", spriteSheets.get("deck-number").getSubimage(71, 95, 71, 95)),
                Map.entry("4-CLUBS", spriteSheets.get("deck-number").getSubimage(71*2, 95, 71, 95)),
                Map.entry("5-CLUBS", spriteSheets.get("deck-number").getSubimage(71*3, 95, 71, 95)),
                Map.entry("6-CLUBS", spriteSheets.get("deck-number").getSubimage(71*4, 95, 71, 95)),
                Map.entry("7-CLUBS", spriteSheets.get("deck-number").getSubimage(71*5, 95, 71, 95)),
                Map.entry("8-CLUBS", spriteSheets.get("deck-number").getSubimage(71*6, 95, 71, 95)),
                Map.entry("9-CLUBS", spriteSheets.get("deck-number").getSubimage(71*7, 95, 71, 95)),
                Map.entry("10-CLUBS", spriteSheets.get("deck-number").getSubimage(71*8, 95, 71, 95)),
                Map.entry("Jack-CLUBS", spriteSheets.get("deck-number").getSubimage(71*9, 95, 71, 95)),
                Map.entry("Queen-CLUBS", spriteSheets.get("deck-number").getSubimage(71*10, 95, 71, 95)),
                Map.entry("King-CLUBS", spriteSheets.get("deck-number").getSubimage(71*11, 95, 71, 95)),
                Map.entry("Ace-CLUBS", spriteSheets.get("deck-number").getSubimage(71*12, 95, 71, 95)),
                Map.entry("2-DIAMONDS", spriteSheets.get("deck-number").getSubimage(0, 95*2, 71, 95)),
                Map.entry("3-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71, 95*2, 71, 95)),
                Map.entry("4-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*2, 95*2, 71, 95)),
                Map.entry("5-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*3, 95*2, 71, 95)),
                Map.entry("6-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*4, 95*2, 71, 95)),
                Map.entry("7-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*5, 95*2, 71, 95)),
                Map.entry("8-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*6, 95*2, 71, 95)),
                Map.entry("9-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*7, 95*2, 71, 95)),
                Map.entry("10-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*8, 95*2, 71, 95)),
                Map.entry("Jack-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*9, 95*2, 71, 95)),
                Map.entry("Queen-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*10, 95*2, 71, 95)),
                Map.entry("King-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*11, 95*2, 71, 95)),
                Map.entry("Ace-DIAMONDS", spriteSheets.get("deck-number").getSubimage(71*12, 95*2, 71, 95)),
                Map.entry("2-SPADES", spriteSheets.get("deck-number").getSubimage(0, 95*3, 71, 95)),
                Map.entry("3-SPADES", spriteSheets.get("deck-number").getSubimage(71, 95*3, 71, 95)),
                Map.entry("4-SPADES", spriteSheets.get("deck-number").getSubimage(71*2, 95*3, 71, 95)),
                Map.entry("5-SPADES", spriteSheets.get("deck-number").getSubimage(71*3, 95*3, 71, 95)),
                Map.entry("6-SPADES", spriteSheets.get("deck-number").getSubimage(71*4, 95*3, 71, 95)),
                Map.entry("7-SPADES", spriteSheets.get("deck-number").getSubimage(71*5, 95*3, 71, 95)),
                Map.entry("8-SPADES", spriteSheets.get("deck-number").getSubimage(71*6, 95*3, 71, 95)),
                Map.entry("9-SPADES", spriteSheets.get("deck-number").getSubimage(71*7, 95*3, 71, 95)),
                Map.entry("10-SPADES", spriteSheets.get("deck-number").getSubimage(71*8, 95*3, 71, 95)),
                Map.entry("Jack-SPADES", spriteSheets.get("deck-number").getSubimage(71*9, 95*3, 71, 95)),
                Map.entry("Queen-SPADES", spriteSheets.get("deck-number").getSubimage(71*10, 95*3, 71, 95)),
                Map.entry("King-SPADES", spriteSheets.get("deck-number").getSubimage(71*11, 95*3, 71, 95)),
                Map.entry("Ace-SPADES", spriteSheets.get("deck-number").getSubimage(71*12, 95*3, 71, 95))
        ));
    }

    private static final HashMap<String, HashMap<Integer, BufferedImage>> animatedSprites = new HashMap<>(Map.ofEntries(
    ));

    private static final HashMap<String, BufferedImage> hud = new HashMap<>(Map.ofEntries(
    ));

    public SpriteLibrary() {}

    public static BufferedImage instance(String name) {
        return sprites.getOrDefault(name, sprites.get("missing"));
    }

    public static HashMap<Integer, BufferedImage> animatedInstance(String name) {
        return animatedSprites.getOrDefault(name, new HashMap<>());
    }

    public static BufferedImage hud(String name) {
        return hud.getOrDefault(name, sprites.get("missing"));
    }
}
