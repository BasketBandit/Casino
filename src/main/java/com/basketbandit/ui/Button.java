package com.basketbandit.ui;

import com.basketbandit.Renderable;
import com.basketbandit.util.Colours;
import com.basketbandit.util.Fonts;
import com.basketbandit.util.Time;

import java.awt.*;

public class Button extends Rectangle implements Renderable {
    private int[] textCenter;
    private final String name;

    public Button(String name) {
        super(0, 0, 10, 10);
        this.name = name;
        setLocation(0, 0);
    }

    public Button(String name, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.name = name;
        setLocation(x, y);
    }

    @Override
    public String name() {
        return "";
    }

    public int[] textCenter() {
        return textCenter;
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        this.textCenter = Fonts.centered(graphics, name, this, Fonts.default20);
    }

    public void render(Color colour) {
        graphics.setColor(colour);
        graphics.fill(this);
        graphics.setColor(Colours.WHITE);
        graphics.drawString(name, textCenter[0], textCenter[1] + (int) (Math.sin(Time.slowTicks()) * 2));
    }
}
