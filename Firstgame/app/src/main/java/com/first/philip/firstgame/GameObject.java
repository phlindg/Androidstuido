package com.first.philip.firstgame;

import android.graphics.Rect;

/**
 * Created by philip on 2015-03-05.
 */
public abstract class GameObject {
    protected int x, y;
    protected int dy, dx;
    protected int width, height;


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rect getRectangle() {
        return new Rect(x, y, x + width, y + height);
    }


}
