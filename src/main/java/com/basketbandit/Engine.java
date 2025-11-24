package com.basketbandit;

import com.basketbandit.io.Input;
import com.basketbandit.state.State;
import com.basketbandit.state.StateManager;
import com.basketbandit.util.Time;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine implements Runnable {
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static State state;
    private static boolean running = true;
    private static final int targetUPS = 60;
    private static int targetFPS = 144;
    private static final AtomicInteger framesPerSecond = new AtomicInteger(0);

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
        return framesPerSecond.get();
    }

    private static final Clock clock = new Clock();
    private static final Update update = new Update();
    private static final Render render = new Render();

    @Override
    public void run() {
        new Thread(clock, "Clock").start();
        new Thread(update, "Update").start();
        new Thread(render, "Render").start();
    }

    private static class Clock implements Runnable {
        long timer = System.currentTimeMillis();

        @Override
        public void run() {
            final double updateInterval = 1_000_000_000.0 / targetUPS;
            long last = System.nanoTime();
            double delta = 0;

            while(running) {
                long now = System.nanoTime();
                delta += (now - last) / updateInterval;
                last = now;

                while (delta >= 1) {
                    Time.increment();
                    delta--;
                }

                if(System.currentTimeMillis() - timer > 1000) {
                    framesPerSecond.set(render.frames());
                    render.resetFrames();
                    timer += 1000;
                }

                Thread.onSpinWait();
            }
        }


    }

    private static class Update implements Runnable {
        @Override
        public void run() {
            final double updateInterval = 1_000_000_000.0 / targetUPS;
            long last = System.nanoTime();
            double delta = 0;

            while(running) {
                long now = System.nanoTime();
                delta += (now - last) / updateInterval;
                last = now;

                while (delta >= 1) {
                    state.update();
                    delta--;
                }

                Thread.onSpinWait();
            }
        }
    }

    private static class Render implements Runnable {
        private int frames = 0;

        @Override
        public void run() {
            final double frameInterval = 1_000_000_000.0 / targetFPS;
            long last = System.nanoTime();
            double delta = 0;

            while(running) {
                long now = System.nanoTime();
                delta += (now - last) / frameInterval;
                last = now;

                if (delta >= 1) {
                    state.render();
                    Renderer.render();
                    frames++;
                    delta--;
                }

                Thread.onSpinWait(); // keeps loop responsive without blocking
            }
        }

        public int frames() {
            return frames;
        }

        public void resetFrames() {
            this.frames = 0;
        }
    }
}
