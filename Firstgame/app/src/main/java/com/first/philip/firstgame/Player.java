package com.first.philip.firstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by philip on 2015-03-05.
 */
public class Player extends GameObject {
    private Bitmap spriteSheet;
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames) {
        x = 100;
        y = GamePanel.HEIGHT / 2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spriteSheet = res;

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spriteSheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean b) {
        up = b;
    }

    public void update() {
        //här ska vi lägga in poängen. 10 poäng i sekunden.
        long elapse = (System.nanoTime() - startTime) / 1000000;
        if (elapse > 100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if (up) {
            dy-=1;
        } else {
            dy+=1;

        }
        if (dy > 5) dy = 5;
        if (dy < -5) dy = -5;

        if(y>445)y=445;
        if(y<0)y=0;


        y += dy * 2;
        //dy = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);

    }

    public int getScore() {
        return score;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setPlaying(boolean b) {
        playing = b;
    }

    public void resetDY() {
        dy = 0;
    }

    public void resetScore() {
        score = 0;
    }

}
