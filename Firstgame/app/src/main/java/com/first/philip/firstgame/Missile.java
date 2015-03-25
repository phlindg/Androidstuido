package com.first.philip.firstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by philip on 2015-03-11.
 */
public class Missile extends GameObject {

    private int score, speed;
    private Random random = new Random();
    private Animation animation = new Animation();
    private Bitmap spriteSheet;

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        height = h;
        width = w;
        score = s;

        speed = 7 + score/30;
        //capping missile speed
        if (speed > 60) speed = 60;

        Bitmap[] image = new Bitmap[numFrames];
        spriteSheet = res;

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spriteSheet, 0, i * height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100 - (speed));
    }

    public void draw(Canvas canvas) {
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch(Exception e){

        }
    }

    public void update() {
        x -= speed;
        animation.update();
    }
    public int getWidth(){
        //sätta mer offset för att få enklare collison detection
        return width-10;
    }
}
