package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Pete on 5/13/2018. */

public class GamePlayMenu implements IScene  {
    private ArrayList<IGameObject> gameObjects;
    private ArrayList<ParticleExplosion> explosions;
    private Point playerPoint;
    private Point menuPoint;
    private ObstacleQueue obstacleQueue;
    private IGameObject SelectedObject;
    private long newItemTime;
    private int speed = 2;
    private boolean menuGame = false;
    private Paint paint;
    private Paint bpaint;
    private int score;

    private final float bLeft = Constants.SCREEN_WIDTH/2 - 275;
    private final float bRight = Constants.SCREEN_WIDTH/2 + 275;
    private final float bFirstY = Constants.SCREEN_HEIGHT/2 - 200;
    private final float bHeight = 150f;
    private final float bGap = 50f;

    private RectF bStart = new RectF(bLeft, bFirstY, bRight, bFirstY + bHeight);
    private RectF bLevel = new RectF(bLeft, bFirstY + (bHeight + bGap)*1, bRight, bFirstY + bHeight + (bHeight + bGap)*1);
    private RectF bTutorial = new RectF(bLeft, bFirstY + (bHeight + bGap)*2, bRight, bFirstY + bHeight + (bHeight + bGap)*2);
    private RectF bOptions = new RectF(bLeft, bFirstY + (bHeight + bGap)*3, bRight, bFirstY + bHeight + (bHeight + bGap)*3);
    private RectF bExit = new RectF(bLeft, bFirstY + (bHeight + bGap)*4, bRight, bFirstY + bHeight + (bHeight + bGap)*4);

    private RectF bMenuGame = new RectF(Constants.SCREEN_WIDTH - 150, Constants.SCREEN_HEIGHT - 150, Constants.SCREEN_WIDTH - 25, Constants.SCREEN_HEIGHT - 25);

    public GamePlayMenu() {
        playerPoint = new Point(0, 0);
        menuPoint = new Point(0,0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleQueue = new ObstacleQueue(25);
        SelectedObject = obstacleQueue.getItem();
        newItemTime = System.currentTimeMillis();
        score = 0;

        //Button paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        //Border paint
        bpaint = new Paint();
        bpaint.setStyle(Paint.Style.STROKE);
        bpaint.setColor(Color.BLACK);
    }

    @Override
    public void terminate() {
        SceneManager.ACTIVE_SCENE = 0;
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //System.out.println("MouseDown X:" + event.getX() + ", Y:" + event.getY() );

                //Button: Menu Game
                if( bMenuGame.contains(event.getX(), event.getY()) ) {
                    if(!menuGame) {
                        menuGame = true;
                    } else {
                        menuGame = false;
                    }
                    score = 0;
                    speed = 2;
                    return;
                }

                if (!menuGame) {
                    //Button: Start
                    if( bStart.contains(event.getX(), event.getY()) ) {
                        SceneManager.changeScene(1);
                        return;
                    }
                    //Button: Level Select
                    if( bLevel.contains(event.getX(), event.getY()) ) {
                        //SceneManager.changeScene(1);
                        return;
                    }
                    //Button: How to Play
                    if( bTutorial.contains(event.getX(), event.getY()) ) {
                        //SceneManager.changeScene(1);
                        return;
                    }
                    //Button: Options
                    if( bOptions.contains(event.getX(), event.getY()) ) {
                        //SceneManager.changeScene(1);
                        return;
                    }
                    //Button: Exit
                    if( bExit.contains(event.getX(), event.getY()) ) {
                        //SceneManager.changeScene(1);
                        return;
                    }
                } else {
                    playerPoint = new Point( (int)event.getX(), (int)event.getY() );
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

        if (!menuGame) {
            /* Draw Menu */

            //Logo
            drawCenterText(canvas, Color.WHITE, 150, Constants.SCREEN_HEIGHT/4, "Radar Blast");

            //Button: Start Game
            canvas.drawRoundRect( bStart, 25,25, paint);
            canvas.drawRoundRect( bStart, 25,25, bpaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bStart.centerY(), "Start Game");

            //Button: Level Select
            canvas.drawRoundRect( bLevel, 25,25, paint);
            canvas.drawRoundRect( bLevel, 25,25, bpaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bLevel.centerY(), "Level Select");

            //Button: How to Play
            canvas.drawRoundRect( bTutorial, 25,25, paint);
            canvas.drawRoundRect( bTutorial, 25,25, bpaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bTutorial.centerY(), "How to Play");

            //Button: Options
            canvas.drawRoundRect( bOptions, 25,25, paint);
            canvas.drawRoundRect( bOptions, 25,25, bpaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bOptions.centerY(), "Options");

            //Button: Exit
            canvas.drawRoundRect( bExit, 25,25, paint);
            canvas.drawRoundRect( bExit, 25,25, bpaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bExit.centerY(), "Exit");
        } else {
            // Score
            Paint scorePaint = new Paint();
            scorePaint.setTextSize(100);
            scorePaint.setStyle(Paint.Style.FILL);
            scorePaint.setColor(Color.WHITE);
            canvas.drawText("" + score, 50,150, scorePaint );
        }

        //Button: MenuGame
        canvas.drawRoundRect( bMenuGame, 25,25, paint);
        canvas.drawRoundRect( bMenuGame, 25,25, bpaint);

    }

    @Override
    public void update() {
        //IGameObject currentObject = new Obstacle(0,0,0,0);
        ArrayList<IGameObject> popped = new ArrayList<>();
        ArrayList<ParticleExplosion> expDone = new ArrayList<>();

        //Update obstacles
        for(IGameObject gob: gameObjects){
            // Grow object
            gob.grow(speed);
        }

        if (menuGame) {
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
                    explosions.add(new ParticleExplosion( (int)gobPop.getSize(), gobPop.getSize(), gobPop.getCenter().x, gobPop.getCenter().y, gobPop.getType() ));
                    gameObjects.remove(gobPop);
                    score += 1;
                    if (speed < 10) {
                        speed += 1;
                    }
                }
                //Reset PlayerPoint
                playerPoint = new Point(0,0);
            }
        }

        // If objects current hits edge -> pop
        for(IGameObject gob: gameObjects){
            if(!gob.InGameArea()) {
                popped.add(gob);
                if (!menuGame) {
                    explosions.add(new ParticleExplosion((int) gob.getSize(), gob.getSize(), gob.getCenter().x, gob.getCenter().y, gob.getType()));
                }
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
        float y =  vHeight + r.height() / 2f;
        canvas.drawText(text, x, y, txtPaint);
    }}




