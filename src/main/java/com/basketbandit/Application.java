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
        this.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
        this.setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(false);
        this.setIgnoreRepaint(true);
        this.setResizable(true);
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