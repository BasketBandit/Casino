package com.basketbandit;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {
    static void main(String[] args) {
        System.setProperty("sun.java2d.d3d", "True");
        //System.setProperty("sun.java2d.opengl", "True");
        new Application();
    }

    public Application() {
        this.setTitle("Casino");
        this.setSize(new Dimension(1280, 720));
        this.setPreferredSize(new Dimension(1280, 720));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(false);
        this.setIgnoreRepaint(true);
        this.setResizable(false);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.add(Renderer.canvas());
        this.setVisible(true);

        Renderer.setInsets(this.getInsets());

        Thread thread = new Thread(new Engine());
        thread.setName("Engine-Thread");
        thread.start();
    }
}