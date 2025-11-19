package com.basketbandit.state;

import com.basketbandit.Renderer;
import com.basketbandit.game.Blackjack;
import com.basketbandit.io.Keyboard;
import com.basketbandit.io.audio.AudioLibrary;
import com.basketbandit.io.audio.AudioManager;
import com.basketbandit.util.Colours;
import com.basketbandit.util.Fonts;

import java.awt.*;
import java.util.HashMap;

public class MainMenu extends State {
    private final HashMap<String, Rectangle> boundingBoxes = new HashMap<>();
    private final HashMap<String, int[]> textCoordinates = new HashMap<>();
    private final String[] options = new String[]{"BLACKJACK", "QUIT"};
    private int optionSelected = 0;

    public MainMenu() {
        boundingBoxes.put("fullscreen", new Rectangle(0, 0, Renderer.width(), Renderer.height()));
        for(String option : options) {
            textCoordinates.put(option, Fonts.centered(Renderer.graphics(), option, boundingBoxes.get("fullscreen"), Fonts.default24));
        }
        AudioManager.background.load(AudioLibrary.audioFile("Mistadobalina"));
    }

    @Override
    public void input(int key) {
        if(key == Keyboard.UP_ARROW) {
            if(--optionSelected < 0) {
                optionSelected = options.length-1;
            }
        }
        if(key == Keyboard.DOWN_ARROW) {
            if(++optionSelected > options.length-1) {
                optionSelected = 0;
            }
        }
        if(key == Keyboard.ENTER) {
            if(optionSelected == 0) {
                if(!StateManager.hasState("blackJack")) {
                    StateManager.addAndChangeState("blackjack", new Blackjack());
                    return;
                }
                StateManager.changeState("blackjack");
                return;
            }
            if(optionSelected == 2) {
                System.exit(0);
            }
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
        for(int i = 0; i < options.length; i++) {
            int[] tc = textCoordinates.get(options[i]);
            graphics.setColor(i == optionSelected ? Colours.GOLD : Colours.WHITE);
            graphics.drawString(options[i], tc[0], tc[1] + (i*30));
        }
    }
}
