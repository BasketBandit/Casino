package com.basketbandit.io.image;

import com.basketbandit.Renderable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpriteManager<Object extends Rectangle & Renderable> {
    public final Object object;
    public int[] offset = new int[]{0, 0};
    public int totalAnimationFrames = 0;
    public int framesPerSecond = 1;
    public int currentAnimationFrame = 1;

    public SpriteManager(Object object, int totalAnimationFrames, int framesPerSecond, int[] offset) {
        this.object = object;
        this.totalAnimationFrames = totalAnimationFrames;
        this.framesPerSecond = framesPerSecond;
        this.offset = offset;
    }

    public SpriteManager(Object object, int totalAnimationFrames, int framesPerSecond) {
        this.object = object;
        this.totalAnimationFrames = totalAnimationFrames;
        this.framesPerSecond = framesPerSecond;
    }

    public SpriteManager(Object object, int[] offset) {
        this.object = object;
        this.offset = offset;
    }

    public SpriteManager(Object object) {
        this.object = object;
    }

    public int[] offset() {
        return offset;
    }

//    public void update() {
//        if(totalAnimationFrames != 0 && Time.ticks % (60/framesPerSecond) == 0) {
//            currentAnimationFrame = currentAnimationFrame < totalAnimationFrames ? currentAnimationFrame + 1 : 1;
//        }
//    }

    public BufferedImage sprite() {
        return totalAnimationFrames == 0 ? SpriteLibrary.instance(object.toString()) : SpriteLibrary.animatedInstance(object.name()).get(currentAnimationFrame);
    }

    public BufferedImage override(String name) {
        return SpriteLibrary.instance(name);
    }
}

