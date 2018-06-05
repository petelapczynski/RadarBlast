package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Pete on 5/13/2018. */

public class GamePlayScene1 implements IScene  {
    private ArrayList<IGameObject> gameObjects;
    private ArrayList<ParticleExplosion> explosions;
    private Point playerPoint;
    private Point menuPoint;
    private ObstacleQueue obstacleQueue;
    private IGameObject SelectedObject;
    private long newItemTime;
    private int speed = 2;
    private int score;
    private Countdown countdown;
    private boolean gameStart;
    private boolean gameOver;
    private long gameOverTime;

    public GamePlayScene1() {
        playerPoint = new Point(0, 0);
        menuPoint = new Point(0,0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleQueue = new ObstacleQueue(25);
        SelectedObject = obstacleQueue.getItem();
        newItemTime = System.currentTimeMillis();
        score = 0;
        countdown = new Countdown();
        gameStart = false;
        gameOver = false;
    }

    private void reset() {
        playerPoint = new Point(0, 0);
        menuPoint = new Point(0,0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleQueue = new ObstacleQueue(25);
        SelectedObject = obstacleQueue.getItem();
        newItemTime = System.currentTimeMillis();
        score = 0;
        countdown = new Countdown();
        gameStart = false;
        gameOver = false;
    }

    @Override
    public void terminate() {
        SceneManager.changeScene("MENU");
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //System.out.println("MouseDown X:" + event.getX() + ", Y:" + event.getY() );

                playerPoint = new Point( (int)event.getX(), (int)event.getY() );

                if(!gameStart) {
                    gameStart = true;
                    countdown.startTimer();
                }

                if(gameOver) {
                    if(System.currentTimeMillis() - gameOverTime >= 500) {
                        reset();
                    }
                }

                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Canvas
        canvas.drawColor(Color.DKGRAY);

        //Game Objects
        for(IGameObject obj : gameObjects){
            if ( obj.InGameArea() ) { obj.draw(canvas); }
        }

        //Explosions
        for(ParticleExplosion pe : explosions) {
           if (pe.getState() == 0) { pe.draw(canvas); }
        }

        // Score
        Paint scorePaint = new Paint();
        scorePaint.setTextSize(100);
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setColor(Color.WHITE);
        canvas.drawText("" + score, 50,150, scorePaint );

        // CountDownTimer
        scorePaint.setColor(Color.GREEN);
        canvas.drawText("" + countdown.getTimeLeft(), Constants.SCREEN_WIDTH - 150,150, scorePaint );

        if(!gameStart) {
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2,"Click to Start!");
        }

        if(gameOver) {
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2,"Game Over!");
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2 + 100,"Click to Reset");
        }

    }

    @Override
    public void update() {

        if (gameStart && !gameOver) {
            //IGameObject currentObject = new Obstacle(0,0,0,0);
            ArrayList<IGameObject> popped = new ArrayList<>();
            ArrayList<ParticleExplosion> expDone = new ArrayList<>();

            //Update obstacles
            for(IGameObject gob: gameObjects){
                // Grow object
                gob.grow(speed);
            }


            // If playerPoint is inside a game object -> pop object
            if (playerPoint.x != 0 && playerPoint.y != 0){
                boolean currPop = false;
                IGameObject gobPop = new Obstacle(0,0,0,0);

                for(IGameObject gob: gameObjects) {
                    if ( gob.pointInside(playerPoint)) {
                        currPop = true;
                        gobPop = gob;
                    }
                }

                if (currPop) {
                    gobPop.pop();
                    calcScore(gobPop);
                    explosions.add(new ParticleExplosion( (int)gobPop.getSize(), gobPop.getSize(), gobPop.getCenter().x, gobPop.getCenter().y, gobPop.getType() ));
                    gameObjects.remove(gobPop);

                    //if (speed < 10) {
                    speed += 1;
                    //}
                }
                //Reset PlayerPoint
                playerPoint = new Point(0,0);
            }


            // If objects current hits edge -> pop
            for(IGameObject gob: gameObjects){
                if(!gob.InGameArea()) {
                    popped.add(gob);
                    //if (!menuGame) {
                    //    explosions.add(new ParticleExplosion((int) gob.getArea(), gob.getSize(), gob.getCenter().x, gob.getCenter().y, gob.getType()));
                    //}
                    speed = 2;
                }
            }
            if (!popped.isEmpty()){
                gameObjects.removeAll(popped);
            }

            //Explosions
            for(ParticleExplosion pe : explosions) {
                if (pe.getState() == 0) {
                    pe.update();
                } else {
                    expDone.add(pe);
                }
            }
            if (!expDone.isEmpty()) {
                explosions.removeAll(expDone);
            }

            // Create next object in queue
            SelectedObject = obstacleQueue.getItem();
            if(SelectedObject == null) {
                // Populate more items in queue
                for(int i=0; i<25; i++) {
                    obstacleQueue.addItem();
                }
                SelectedObject = obstacleQueue.getItem();
            }
            if (System.currentTimeMillis() - newItemTime >= 1000) {
                menuPoint = new Point(Common.randomInt(250, Constants.SCREEN_WIDTH - 250), Common.randomInt(250, Constants.SCREEN_HEIGHT - 250));
                //addingShape = true;
                IGameObject newObject = SelectedObject.NewInstance();
                newObject.update(menuPoint);
                gameObjects.add(newObject);
                obstacleQueue.removeItem();
                SelectedObject = obstacleQueue.getItem();
                newItemTime = System.currentTimeMillis();
            }

            // CountDownTimer
            if(!gameOver) {
                if(countdown.IsFinished()) {
                    gameOver = true;
                    gameOverTime = System.currentTimeMillis();
                }
            }
        }

    }

    private void calcScore(IGameObject gob) {
        float grayArea = (Constants.SCREEN_WIDTH * Constants.SCREEN_HEIGHT );
        float coveredArea = gob.getArea();
        int add = (int)( (coveredArea / grayArea ) * 100.0f );
        if(add < 1){
            add = 1;
        }
        score += add;
    }

    private void drawCenterText(Canvas canvas, int color, float size, int vHeight, String text) {
        Rect r = new Rect();
        Paint txtPaint = new Paint();
        txtPaint.setTextAlign(Paint.Align.LEFT);
        txtPaint.setColor(color);
        txtPaint.setTextSize(size);
        canvas.getClipBounds(r);
        //int cHeight = r.height();
        int cWidth = r.width();
        txtPaint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = vHeight + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, txtPaint);
    }

}




