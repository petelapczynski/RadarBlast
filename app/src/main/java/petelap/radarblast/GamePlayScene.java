package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;

/**
 * Created by Pete on 3/25/2018. */

public class GamePlayScene implements IScene {
    private ArrayList<IGameObject> gameObjects;
    private ArrayList<ParticleExplosion> explosions;
    private Point playerPoint;
    private ObstacleManager obstacleManager;
    private ObstacleQueue obstacleQueue;
    private IGameObject SelectedObject;

    private boolean addingShape = false;
    private boolean gameOver = false;
    private boolean objectPop = false;
    private long gameOverTime;
    private long objectTime;
    private int score;
    private int speed;

    public GamePlayScene() {
        playerPoint = new Point(0, 0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleManager = new ObstacleManager( Common.randomInt(0,3), Common.randomInt(100, 200), Color.GREEN);
        obstacleQueue = new ObstacleQueue(Common.randomInt(5,10));
        SelectedObject = obstacleQueue.getItem();
        score = 0;
        speed = 1;
    }

    private void reset() {
        gameOver = false;
        objectPop = false;
        addingShape = false;
        playerPoint = new Point(0, 0);
        gameObjects = new ArrayList<>();

        obstacleManager = new ObstacleManager( Common.randomInt(0,3), Common.randomInt(100, 200), Color.GREEN);
        //obstacleManager = new ObstacleManager(1, 100, Color.GREEN);

        obstacleQueue = new ObstacleQueue(Common.randomInt(5,10));

        //debug();

        SelectedObject = obstacleQueue.getItem();
        score = 0;
        speed = 1;
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
                Point point = new Point( (int)event.getX(), (int)event.getY() );
                if(addingShape) {
                    addingShape = false;
                    SelectedObject = obstacleQueue.getItem();
                    if (SelectedObject == null) {
                        gameOverTime = System.currentTimeMillis();
                    }
                    break;
                }

                //Header selection
                if (point.y <= 200) {
                    /* //Select First Item:
                    if (point.x < Constants.SCREEN_WIDTH && point.x > Constants.SCREEN_WIDTH - 225) {
                        SelectedObject = ObstacleSquare.GetInstance();
                        return;
                    }
                    //Select Second Item:
                    if (point.x < Constants.SCREEN_WIDTH - 225 && point.x > Constants.SCREEN_WIDTH - 450) {
                        SelectedObject = ObstacleSquare.GetInstance();
                        return;
                    }
                    //Select Third Item:
                    if (point.x < Constants.SCREEN_WIDTH - 450 && point.x > Constants.SCREEN_WIDTH - 675) {
                        SelectedObject = ObstacleTriangleUp.GetInstance();
                        return;
                    } */

                    //Click Score - Reset
                    if (point.x <  Constants.SCREEN_WIDTH - 675) {
                        reset();
                    }
                    return;
                }

                // Queue selection
                SelectedObject = obstacleQueue.getItem();

                if(!gameOver) {
                    if(!addingShape && SelectedObject != null) {
                        // Object Pop delay
                        if(!objectPop) {
                            playerPoint = point;
                            addingShape = true;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(playerPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            objectTime = System.currentTimeMillis();
                        }
                    } else if (SelectedObject == null) {
                        if(System.currentTimeMillis() - gameOverTime >= 500) {
                            reset();
                        }
                    }
                }

                if (gameOver) {
                    if(System.currentTimeMillis() - gameOverTime >= 500) {
                        reset();
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if(!gameOver && addingShape) {
                    gameObjects.get(gameObjects.size() - 1).update( new Point((int)event.getX(), (int)event.getY() ) );
                }
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Canvas
        canvas.drawColor(Color.DKGRAY);

        // Header
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawRect(0,0,Constants.SCREEN_WIDTH,Constants.HEADER_HEIGHT,paint);
        // Header Queue
        obstacleQueue.draw(canvas);

        // Score
        paint.setTextSize(100);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawText("" + score, 50,150, paint );

        // Obstacles
        obstacleManager.draw(canvas);

        //Game Objects
        for(IGameObject obj : gameObjects){
            if ( obj.InGameArea() ) { obj.draw(canvas); }
        }

        //Explosions
        for(ParticleExplosion pe : explosions) {
            if (pe.getState() == 0) { pe.draw(canvas); }
        }

        // Game over
        if(gameOver) {
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2,"Game Over!");
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2 + 100,"Click to Reset");
        }

        // Out of shapes
        if(SelectedObject == null && !addingShape && !gameOver){
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2,"Out of Shapes!");
            drawCenterText(canvas, Color.WHITE, 100, Constants.SCREEN_HEIGHT/2 + 100,"Click to Reset");
        }
    }

    @Override
    public void update() {
        IGameObject currentObject = new Obstacle(0,0,0,0);

        if(gameObjects.size() > 0){
            currentObject = gameObjects.get(gameObjects.size() - 1);
        }

        if(!gameOver) {
            //Update obstacles

            // If addingShape -> Grow object
            if (addingShape){
                currentObject.grow(speed);
                if(System.currentTimeMillis() - objectTime >= 300) {
                    speed = speed + 1;
                    objectTime = System.currentTimeMillis();
                }

                // If hit obstacle -> game over
                if(obstacleManager.obstacleManagerCollide(currentObject)) {
                    gameOver = true;
                    gameOverTime = System.currentTimeMillis();
                }

                // If current hits another game object -> pop object
                boolean currPop = false;
                IGameObject gobPop = new Obstacle(0,0,0,0);

                for(IGameObject gob: gameObjects) {
                    if( !gob.equals(currentObject) ) {
                        String obType = currentObject.getType();
                        switch( obType ){
                            case "Circle":
                                if ( gob.CollideCircle(currentObject.getCenter(), currentObject.getSize() )) {
                                    currPop = true;
                                    gobPop = gob;
                                }
                                break;
                            case "Square":
                                if ( gob.CollideSquare(currentObject.getCenter(), currentObject.getSize() )) {
                                    currPop = true;
                                    gobPop = gob;
                                }
                                break;
                            case "TriangleUp":
                                if ( gob.CollideTriangleUp(currentObject.getCenter(), currentObject.getSize() )) {
                                    currPop = true;
                                    gobPop = gob;
                                }
                                break;
                            case "TriangleDown":
                                if ( gob.CollideTriangleDown(currentObject.getCenter(), currentObject.getSize() )) {
                                    currPop = true;
                                    gobPop = gob;
                                }
                                break;
                            case "Rhombus":
                                if ( gob.CollideRhombus(currentObject.getCenter(), currentObject.getSize() )) {
                                    currPop = true;
                                    gobPop = gob;
                                }
                                break;
                        }
                    }
                }

                // If hit other object or hit edge -> pop object
                if (currPop) {
                    currentObject.pop();
                    explosions.add(new ParticleExplosion( (int)currentObject.getSize(), currentObject.getSize(), currentObject.getCenter().x, currentObject.getCenter().y, currentObject.getType() ));
                    gameObjects.remove(currentObject);

                    gobPop.pop();
                    explosions.add(new ParticleExplosion( (int)gobPop.getSize(), gobPop.getSize(), gobPop.getCenter().x, gobPop.getCenter().y, gobPop.getType() ));
                    gameObjects.remove(gobPop);

                    addingShape = false;
                    objectPop = true;
                    objectTime = System.currentTimeMillis();
                } else {
                    if (!currentObject.InGameArea()) {
                        currentObject.pop();
                        explosions.add(new ParticleExplosion( (int)currentObject.getSize(), currentObject.getSize(), currentObject.getCenter().x, currentObject.getCenter().y, currentObject.getType() ));
                        gameObjects.remove(currentObject);

                        addingShape = false;
                        objectPop = true;
                        objectTime = System.currentTimeMillis();
                    }
                }

                // Out of shapes
                if(SelectedObject == null) {
                    gameOverTime = System.currentTimeMillis();
                }

                // Calculate score
                calcScore();
            }

            if(objectPop){
                if(System.currentTimeMillis() - objectTime >= 250) {
                    objectPop = false;
                }
            }


        }

        //Explosions
        ArrayList<ParticleExplosion> expDone = new ArrayList<>();
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
    }

    private void calcScore() {
        float grayArea = (Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT)) - obstacleManager.getArea();
        float coveredArea = 0.0f;
        for (IGameObject gob : gameObjects ) {
            coveredArea += gob.getArea();
        }
        score = (int)( (coveredArea / grayArea ) * 100.0f );
    }

    private void debug(){
//        obstacleQueue = new ObstacleQueue(0);
//        obstacleQueue.addItem("RCRCRCRCRC");
//        obstacleQueue.addItem("RSRSRSRSRS");
//        obstacleQueue.addItem("RTRTRTRTRT");
//        obstacleQueue.addItem("RIRIRIRIRI");
//        obstacleQueue.addItem("RRRRRRRRRR");
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


