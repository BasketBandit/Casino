package com.basketbandit;

import com.basketbandit.io.Input;
import com.basketbandit.state.State;
import com.basketbandit.state.StateManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Engine implements Runnable {
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static State state;
    private static boolean running = true;
    private static int targetUPS = 60;
    private static int targetFPS = 100000;
    private static int lastTicks = 0;
    private static int lastFrames = 0;

    public Engine() {
        StateManager.changeState("main_menu");
    }

    public static Future<?> submitTask(Callable<?> task) {
        return executor.submit(task);
    }

    public static void submitTask(Runnable task) {
        executor.submit(task);
    }

    public static void input(Input type, int[] id) {
        state.input(type, id);
    }

    public static State state() {
        return state;
    }

    public static void setState(State s) {
        state = s;
    }

    public static void setTargetFPS(int fps) {
        targetFPS = fps;
    }

    public static void stop() {
        running = false;
    }

    public static int framerate() {
        return lastFrames;
    }

    public static int tickrate() {
        return lastTicks;
    }

    @Override
    public void run() {
        long initialTime = System.nanoTime();
        double timeU = 1000000000.0 / targetUPS; // tickrate
        double timeF = 1000000000.0 / targetFPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();

        while(running) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;

            if(deltaU >= 1) {
                state.update();
                ticks++;
                deltaU--;
            }

            if(deltaF >= 1 && ticks > 0) {
                state.render();
                Renderer.render();
                frames++;
                deltaF--;
            }

            if(System.currentTimeMillis() - timer > 1000) {
                lastFrames = frames;
                lastTicks = ticks;
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
        }
    }
}
