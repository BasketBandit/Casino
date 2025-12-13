package com.basketbandit.io;

import com.basketbandit.Engine;
import com.basketbandit.Renderer;

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
        int[] mouse = translate(e.getX(), e.getY());
        Engine.input(Input.MOUSE, new int[]{e.getButton(), mouse[0], mouse[1]});
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int[] mouse = translate(e.getX(), e.getY());
        Engine.input(Input.MOUSE, new int[]{MOUSE_MOVED, mouse[0], mouse[1]});
    }

    private int[] translate(int x, int y) {
        int a = Math.round((float) (x * Renderer.width()) / Renderer.windowWidth());
        int b = Math.round((float) (y * Renderer.height()) / Renderer.windowHeight());
        return new int[]{a, b};
    }
}
