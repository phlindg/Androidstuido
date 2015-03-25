package com.first.philip.firstgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by philip on 2015-03-05.
 */
public class MainThread extends Thread {
    //här skapar jag loopen.

    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    private static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {

        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;

    }

    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            //try att låsa canvas
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    //dessa två linjer är de som får allt att fungera
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }

            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //här är hur många millisec de tog att updatera o skriva ut spelet
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println("FPS: " + averageFPS);
            }
        }

    }

    public void setRunning(boolean b) {
        running = b;
    }


}
