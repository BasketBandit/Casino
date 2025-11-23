package com.basketbandit.io;

import com.basketbandit.Engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Mouse implements MouseListener {
    public static final int LEFT_CLICK = MouseEvent.BUTTON1;
    public static final int RIGHT_CLICK = MouseEvent.BUTTON2;

    public static boolean[] buttons = new boolean[1024];

    @Override
    public void mousePressed(MouseEvent e) {
        if(buttons[e.getButton()]) {
            return;
        }

        buttons[e.getButton()] = true;
        Engine.input(Input.MOUSE, new int[]{e.getButton(), e.getX(), e.getY()});
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        buttons[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
}
