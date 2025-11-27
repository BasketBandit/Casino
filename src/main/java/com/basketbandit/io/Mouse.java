package com.basketbandit.io;

import com.basketbandit.Engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {
    public static final int LEFT_CLICK = MouseEvent.BUTTON1;
    public static final int RIGHT_CLICK = MouseEvent.BUTTON2;
    public static final int MOUSE_MOVED = MouseEvent.MOUSE_MOVED;

    public static boolean[] buttons = new boolean[1024];

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Engine.input(Input.MOUSE, new int[]{e.getButton(), e.getX(), e.getY()});
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Engine.input(Input.MOUSE, new int[]{MOUSE_MOVED, e.getX(), e.getY()});
    }
}
