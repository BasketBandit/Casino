package com.basketbandit;

import javax.swing.*;
import java.awt.*;

public class Application extends Frame {
    static void main(String[] args) {
        System.setProperty("sun.java2d.d3d", "True");
        //System.setProperty("sun.java2d.opengl", "True");
        new Application();
    }

    public Application() {
        this.setTitle("Casino");
        this.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
        this.setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setIgnoreRepaint(true);
        this.setResizable(false);
        this.setLayout(null);
        this.add(Renderer.canvas());
        this.setVisible(true);

        Thread thread = new Thread(new Engine());
        thread.setName("Engine-Thread");
        thread.start();
    }
}