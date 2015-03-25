package com.first.philip.firstgame;

import android.graphics.Bitmap;

/**
 * Created by philip on 2015-03-10.
 */
public class Animation {
    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;

    public void setFrames(Bitmap[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();

    }

    public void setDelay(long d) {
        delay = d;
    }

    public void setFrame(int i) {
        currentFrame = i;
    }

    public void update() {

        //detta sätter vilken bild i arrayn som den ska visa.
        long elapsed = (System.nanoTime()) / 1000000;

        if (elapsed > delay) {
            currentFrame++;
            startTime = System.nanoTime();

            if(currentFrame == frames.length){
                currentFrame = 0;
                playedOnce = true;
            }
        }
    }
    public Bitmap getImage(){
        return frames[currentFrame];
    }
    public int getFrame(){
        return currentFrame;
    }
    public boolean playedOnce(){
        return playedOnce;
    }
}
