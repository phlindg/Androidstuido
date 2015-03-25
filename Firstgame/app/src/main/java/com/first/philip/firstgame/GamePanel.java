package com.first.philip.firstgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by philip on 2015-03-05.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Smokepuff> smoke;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topBorder;
    private ArrayList<BotBorder> botBorder;
    private Random random = new Random();
    private boolean newGameCreated;

    public static final int WIDTH = 856, HEIGHT = 480, MOVESPEED = -5;
    private int maxBorderHeight;
    private int minBorderHeight;
    private int progressDenom = 20; //så att man kan ändra svårighetsgrad.Ju lägre destro svårare.
    private boolean topDown = true;
    private boolean botDown = true;
    private long smokeStartTime;
    private long missileStartTime;

    public GamePanel(Context context) {
        super(context);

        //lägga till callback til surfaecholder så att man kan se vad som klickats osv.
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //göra så att den är fokuserbar och han hantera events.
        setFocusable(true);

    }

    //nu overridar jag några surfaceholder methods.
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //ibland tar det flera försök att få den att stanna
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);

        smoke = new ArrayList<Smokepuff>();
        missiles = new ArrayList<Missile>();
        topBorder = new ArrayList<TopBorder>();
        botBorder = new ArrayList<BotBorder>();


        smokeStartTime = System.nanoTime();
        missileStartTime = System.nanoTime();
        //nu kan vi säkert starta game-loopen
        thread.setRunning(true);
        thread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                player.setPlaying(true);
                player.setUp(true);
            } else {
                player.setUp(true);
            }
            //mycket viktigt.
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;
        }


        return super.onTouchEvent(event); //varför är det super.blabla?
    }

    public void update() {


        if (player.getPlaying()) {
            bg.update();
            player.update();


            //kalkulera max höjden borders kan ha beroende på scoren.
            maxBorderHeight = 30 + player.getScore() / progressDenom;
            //cap border height so that they only can take up half of the screen
            if (maxBorderHeight > HEIGHT / 4)
                maxBorderHeight = HEIGHT / 4;
            minBorderHeight = 5 + player.getScore() / progressDenom;

            //top collision
            for (int i = 0; i < topBorder.size(); i++) {
                if (collision(topBorder.get(i), player)) {
                    player.setPlaying(false);
                }
            }
            //bot collision
            for (int i = 0; i < topBorder.size(); i++) {
                if (collision(botBorder.get(i), player)) {
                    player.setPlaying(false);
                }
            }

            //add topBorder
            this.updateTopBorder();

            //add botBorder
            this.updateBotBorder();

            //add misiles
            long missilesElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            if (missilesElapsed > (2000 - player.getScore() / 2)) {
                //ju högre score destro mer missiles


                //first missile go down the middle
                /*if (missiles.size() == 0) {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), WIDTH + 10, HEIGHT / 2, 45, 15, player.getScore(), 13));
                } else {}*/
                missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), WIDTH + 10, (int) (random.nextDouble() * (HEIGHT - maxBorderHeight * 2) + maxBorderHeight), 45, 15, player.getScore(), 13));


                //resettar timern
                missileStartTime = System.nanoTime();

            }
            for (int i = 0; i < missiles.size(); i++) {
                missiles.get(i).update();
                if (collision(missiles.get(i), player)) {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                if (missiles.get(i).getX() < -100) {
                    missiles.remove(i);
                    break;
                }
            }


            //add smokepuffs
            long elapsed = (System.nanoTime() - smokeStartTime) / 1000000;
            if (elapsed > 120) {
                smoke.add(new Smokepuff(player.getX(), player.getY() + 10));
                smokeStartTime = System.nanoTime();
            }
            for (int i = 0; i < smoke.size(); i++) {
                smoke.get(i).update();
                if (smoke.get(i).getX() < -10) {
                    smoke.remove(i);
                }
            }
        } else {
            newGameCreated = false;
            if (!newGameCreated) {
                newGame();
            }

        }
    }

    public boolean collision(GameObject a, GameObject b) {
        if (Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas) {

        final float scaleFactorX = getWidth() / WIDTH;
        final float scaleFactorY = getHeight() / HEIGHT;

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            //för score
         /*   Paint textpaint = new Paint();
            textpaint.setTextSize(32);

            canvas.drawText("Score: " + player.getScore(), 10, 40, textpaint);*/


            for (Smokepuff sp : smoke) {
                sp.draw(canvas);

            }
            for (Missile m : missiles) {
                m.draw(canvas);
            }
            //man måste returna, annars kommer den att fortsätta scalea.
            canvas.restoreToCount(savedState);

            //draw topBorders
            for (TopBorder tb : topBorder) {
                tb.draw(canvas);
            }
            //draw botBorders
            for (BotBorder bb : botBorder) {
                bb.draw(canvas);
            }

        }
    }

    public void updateTopBorder() {
        //var 50e poäng lägger man in en randomly placed block för att göra spelet mer intressant.
        if (player.getScore() % 50 == 0) {
            topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), topBorder.get(topBorder.size() - 1).getX() + 20, 0, (int) ((random.nextDouble() * (maxBorderHeight)) + 1)));
        }
        for (int i = 0; i < topBorder.size(); i++) {
            topBorder.get(i).update();
            if (topBorder.get(i).getX() < -20) {
                //remove
                topBorder.remove(i);
                //replace
                if (topBorder.get(topBorder.size() - 1).getHeight() >= maxBorderHeight) {
                    topDown = false;
                }
                if (topBorder.get(topBorder.size() - 1).getHeight() <= minBorderHeight) {
                    topDown = true;
                }
                if (topDown) {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), topBorder.get(topBorder.size() - 1).getX() + 20, 0, topBorder.get(topBorder.size() - 1).getHeight() + 1));
                } else {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), topBorder.get(topBorder.size() - 1).getX() + 20, 0, topBorder.get(topBorder.size() - 1).getHeight() - 1));

                }
            }
        }

    }

    public void updateBotBorder() {
        //samma fast varje 40e poäng.
        if (player.getScore() % 40 == 0) {
            botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), botBorder.get(botBorder.size() - 1).getX() + 20, (int) ((random.nextDouble() * maxBorderHeight) + (HEIGHT - maxBorderHeight))));
        }
        for (int i = 0; i < botBorder.size(); i++) {
            botBorder.get(i).update();

            if (botBorder.get(i).getX() < -20) {
                botBorder.remove(i);

                if (botBorder.get(botBorder.size() - 1).getY() <= HEIGHT - maxBorderHeight) {
                    botDown = true;
                }
                if (botBorder.get(botBorder.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }
                if (botDown) {
                    botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), botBorder.get(botBorder.size() - 1).getX() + 20, botBorder.get(botBorder.size() - 1).getY() + 1));
                } else {
                    botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), botBorder.get(botBorder.size() - 1).getX() + 20, botBorder.get(botBorder.size() - 1).getY() - 1));
                }
            }


        }

    }

    public void newGame() {
        //RESSETING FINALLY OAWLDMAWDAWDIAJWD
        botBorder.clear();
        topBorder.clear();
        missiles.clear();
        smoke.clear();



        minBorderHeight = 5;
        maxBorderHeight = 30;


        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT / 2);




        //create initial borders

        //top
        for (int i = 0; i * 20 < WIDTH + 40; i++) {
            if (i == 0) {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, 0, 10));
            } else {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, 0, topBorder.get(i - 1).getHeight() + 1));
            }

        }
        //bot
        for (int i = 0; i * 20 < WIDTH + 40; i++) {
            if (i == 0) {
                botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, HEIGHT - minBorderHeight));
            } else {
                botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, botBorder.get(i - 1).getY() - 1));
            }

        }
        newGameCreated = true;

    }

}
