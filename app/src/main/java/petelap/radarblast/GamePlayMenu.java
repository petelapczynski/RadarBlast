package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pete on 3/25/2018. */

public class GamePlayScene implements IScene {
    private ArrayList<IGameObject> gameObjects;
    private Point playerPoint;
    private ObstacleManager obstacleManager;
    private ObstacleQueue obstacleQueue;
    private IGameObject SelectedObject;

    private boolean addingShape = false;
    private boolean gameOver = false;
    private long gameOverTime;
    private int score;
    private int speed = 6;

    public GamePlayScene() {
        Constants.HEADER_HEIGHT = 200;
        playerPoint = new Point(0, 0);
        gameObjects = new ArrayList<>();
        obstacleManager = new ObstacleManager( randomBetween(0,3), (float)randomBetween(100, 200), Color.GREEN);
        obstacleQueue = new ObstacleQueue(randomBetween(4,8));
        SelectedObject = obstacleQueue.getItem();
        score = 0;
    }

    private void reset() {
        gameOver = false;
        addingShape = false;
        playerPoint = new Point(0, 0);
        gameObjects = new ArrayList<>();

        obstacleManager = new ObstacleManager( randomBetween(0,3), (float)randomBetween(100, 200), Color.GREEN);
        //obstacleManager = new ObstacleManager(1, 100, Color.GREEN);

        obstacleQueue = new ObstacleQueue(randomBetween(4,8));
        //obstacleQueue = new ObstacleQueue(0);
        //debug();

        SelectedObject = obstacleQueue.getItem();
        score = 0;
    }

    @Override
    public void terminate() {
        SceneManager.ACTIVE_SCENE = 0;
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                System.out.println("MouseDown X:" + event.getX() + ", Y:" + event.getY() );
                Point point = new Point( (int)event.getX(), (int)event.getY() );
                if(addingShape) {
                    addingShape = false;
                    SelectedObject = obstacleQueue.getItem();
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
                        playerPoint = point;
                        addingShape = true;
                        IGameObject newObject = SelectedObject.NewInstance();
                        newObject.update(playerPoint);
                        gameObjects.add(newObject);
                        obstacleQueue.removeItem();
                        SelectedObject = obstacleQueue.getItem();
                    }
                } else {
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
        canvas.drawRect(0,0,Constants.SCREEN_WIDTH,200,paint);
        // Header Queue
        obstacleQueue.draw(canvas);

        // Score
        paint.setTextSize(100);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawText("" + score, 50,150, paint );

        // Obstacles
        obstacleManager.draw(canvas);

        for(IGameObject obj : gameObjects){
            obj.draw(canvas);
        }

        // Game over
        if(gameOver) {
            drawCenterText(canvas, Color.BLACK, 100, "Game Over");
        }

        // Out of shapes
        if(SelectedObject == null && !addingShape && !gameOver){
            drawCenterText(canvas, Color.BLACK, 100, "Click score to reset");
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
                        }
                    }
                }

                // If hit other object or hit edge -> pop object
                if (currPop) {
                    gameObjects.remove(currentObject);
                    gameObjects.remove(gobPop);
                    addingShape = false;
                } else {
                    if (!currentObject.InGameArea()) {
                        gameObjects.remove(currentObject);
                        addingShape = false;
                    }
                }

                // Calculate score
                calcScore();
            }
        }
    }

    private void drawCenterText(Canvas canvas, int color, float size, String text) {
        Rect r = new Rect();
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(color);
        paint.setTextSize(size);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    private void calcScore() {
        float grayArea = (Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT)) - obstacleManager.getArea();
        float coveredArea = 0.0f;
        for (IGameObject gob : gameObjects ) {
            coveredArea += gob.getArea();
        }
        score = (int)( (coveredArea / grayArea ) * 100.0f );
    }

    private int randomBetween(int low, int high) {
        // Random between = random.nextInt(high - low) + low;
        Random random = new Random();
        return random.nextInt(high + low) + low;
    }

    private void debug(){
        obstacleQueue.addItem("ISCTISCTISCTISCTISCTISCTISCT");
    }
}


