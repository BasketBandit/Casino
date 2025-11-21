package com.basketbandit.util;

public class Time {
    public static int ticks = 0; // target 60tps
    public static int slowTicks = 0; // slow ticks are 1/8 the speed of regular ticks (60/8)

    public static void increment() {
        slowTicks = ticks % 8 == 0 ? (slowTicks + 1) : slowTicks;
        ticks++;
    }

    public static int ticks() {
        return ticks;
    }

    public static int slowTicks() {
        return slowTicks;
    }
}
