package com.basketbandit.io;

import com.basketbandit.Engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {
    public static final int UP_ARROW = KeyEvent.VK_UP;
    public static final int DOWN_ARROW = KeyEvent.VK_DOWN;
    public static final int LEFT_ARROW = KeyEvent.VK_LEFT;
    public static final int RIGHT_ARROW = KeyEvent.VK_RIGHT;
    public static final int ENTER = KeyEvent.VK_ENTER;
    public static final int ESCAPE = KeyEvent.VK_ESCAPE;
    public static final int W = KeyEvent.VK_W;
    public static final int A = KeyEvent.VK_A;
    public static final int S = KeyEvent.VK_S;
    public static final int D = KeyEvent.VK_D;
    public static final int Q = KeyEvent.VK_Q;
    public static final int C = KeyEvent.VK_C;
    public static final int E = KeyEvent.VK_E;
    public static final int P = KeyEvent.VK_P;
    public static final int T = KeyEvent.VK_T;
    public static final int Y = KeyEvent.VK_Y;
    public static final int I = KeyEvent.VK_I;
    public static final int K = KeyEvent.VK_K;
    public static final int L = KeyEvent.VK_L;
    public static final int ONE = KeyEvent.VK_1;
    public static final int TWO = KeyEvent.VK_2;
    public static final int THREE = KeyEvent.VK_3;
    public static final int FOUR = KeyEvent.VK_4;
    public static final int FIVE = KeyEvent.VK_5;
    public static final int SIX = KeyEvent.VK_6;
    public static final int SEVEN = KeyEvent.VK_7;
    public static final int EIGHT = KeyEvent.VK_8;

    public static boolean[] keys = new boolean[1024];

    @Override
    public void keyPressed(KeyEvent e) {
        // effectively locks input until a keyRelease event has happened
        // read from `keys` directly for tick-frequent input e.g movement
        if(keys[e.getKeyCode()]) {
            return;
        }

        keys[e.getKeyCode()] = true;
        Engine.input(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}