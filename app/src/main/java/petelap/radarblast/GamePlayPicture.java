package petelap.radarblast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

/*** Picture Game ***/

public class GamePlayPicture extends GamePlayBase implements IScene {
    private ArrayList<IGameObject> queueObjects;

    private boolean isLayerRefresh;
    private Bitmap bitmapLayer;

    private final float bFirstY = Constants.SCREEN_HEIGHT/2 - 50;
    private final float bGap = 50f;

    private Paint scorePaint;

    private RectF bStartLevel = new RectF(bLeft, bFirstY, bRight, bFirstY + bHeight);
    private RectF bQueueType = new RectF(bLeft, bStartLevel.bottom + bGap, bRight, bStartLevel.bottom + bGap + bHeight);
    private RectF bMenuGS = new RectF(bLeft, bQueueType.bottom + bGap, bRight, bQueueType.bottom + bGap + bHeight);

    private String QueueType;
    private RectF SelectedRect;
    private float selectedRectWidth;
    private RectF ScoreRect;

    public GamePlayPicture() {
        playerPoint = new PointF(0, 0);
        movePoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();

        // Setup Queue Objects
        QueueType = "RANDOM";
        obstacleQueue = new ObstacleQueue(10);
        SelectedObject = obstacleQueue.getItem();

        SelectedObject = null;
        score = 0;
        bHighScore = false;
        speed = 1;
        partCount = Common.getPreferenceInteger("particles");
        gameStart = false;
        addingShape = false;
        addShape = false;
        gameOver = false;
        objectPop = false;
        objectMoving = false;

        //Button paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        //Border paint
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        //Score paint
        scorePaint = new Paint();
        scorePaint.setTextSize(100);
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setColor(Color.WHITE);

        //Background
        String sURL = Common.getPreferenceString("pictureURL");
        bg = new Background(sURL,0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        isLayerRefresh = true;

        overlayPaint = new Paint();
        overlayPaint.setDither(true);
        overlayPaint.setAntiAlias(true);
        overlayPaint.setARGB(75,0,0,0);

        //Game Sounds
        SoundManager.setGameMusic("AREA");

        SelectedRect = new RectF(0,0,0,0);
        ScoreRect = new RectF(0,Constants.HEADER_HEIGHT,125,Constants.HEADER_HEIGHT + 125);
    }

    private void reset() {
        //new background
        String sURL = Common.getPreferenceString("pictureURL");
        bg = new Background(sURL,0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);

        gameStart = false;
        gameOver = false;
        objectPop = false;
        addingShape = false;
        addShape = false;
        objectMoving = false;
        playerPoint = new PointF(0, 0);
        movePoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        if (QueueType.equals("SELECT")) {
            SelectedObject = null;
            SelectedRect = new RectF(0,0,0,0);
        } else if (QueueType.equals("RANDOM")) {
            obstacleQueue = new ObstacleQueue(10);
            SelectedObject = obstacleQueue.getItem();
        }

        score = 0;
        bHighScore = false;
        speed = 1;
        partCount = Common.getPreferenceInteger("particles");
        isLayerRefresh = true;
    }

    @Override
    public void terminate() {
        //Constants.SOUND_MANAGER.stop();
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PointF point = new PointF( event.getX(), event.getY() );
                if(addingShape) {
                    addingShape = false;
                    isLayerRefresh = true;
                    break;
                }

                //Header selection
                if (QueueType.equals("SELECT")) {
                    if (point.y <= Constants.HEADER_HEIGHT) {
                        for (IGameObject gob: queueObjects) {
                            if (gob.pointInside(point)) {
                                SelectedObject = gob.NewInstance();
                                PointF sp = gob.getCenter();
                                SelectedRect = new RectF(sp.x - selectedRectWidth, sp.y - (Constants.HEADER_HEIGHT/2), sp.x + selectedRectWidth, sp.y + (Constants.HEADER_HEIGHT/2));
                                return;
                            }
                        }
                        return;
                    }
                } else if (QueueType.equals("RANDOM")) {
                    // Queue selection
                    SelectedObject = obstacleQueue.getItem();
                }

                if(!gameOver && gameStart) {
                    if(!addingShape && SelectedObject != null) {
                        // Object Pop delay
                        if(!objectPop) {
                            playerPoint = point;
                            addingShape = true;
                            addShape = true;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(playerPoint);
                            gameObjects.add(newObject);
                            if (QueueType.equals("SELECT")) {
                                SelectedObject = SelectedObject.NewInstance();
                            } else if (QueueType.equals("RANDOM")) {
                                obstacleQueue.removeItem();
                                SelectedObject = obstacleQueue.getItem();
                            }
                            speed = 1;
                            objectTime = System.currentTimeMillis();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(!gameOver && addingShape) {
                    movePoint = new PointF( event.getX(), event.getY() );
                    if ((gameObjects.size() > 0) && (movePoint.x > 0) && (movePoint.x < Constants.SCREEN_WIDTH) && (movePoint.y > Constants.HEADER_HEIGHT) && (movePoint.y < Constants.SCREEN_HEIGHT)){
                        objectMoving = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                PointF upPoint = new PointF( event.getX(), event.getY() );
                if(!gameStart || gameOver) {
                    //Button: Start Level
                    if( bStartLevel.contains(upPoint.x, upPoint.y) ) {
                        if (gameOver) {
                            reset();
                        }
                        gameStart = true;
                        SoundManager.playMusic();
                    }
                    //Toggle Queue Type
                    if( bQueueType.contains(upPoint.x, upPoint.y) ) {
                        if (QueueType.equals("SELECT")) {
                            QueueType = "RANDOM";
                            obstacleQueue = new ObstacleQueue(10);
                            SelectedObject = obstacleQueue.getItem();
                        } else {
                            QueueType = "SELECT";
                            setupQueueObjects();
                        }

                    }
                    //Button: Exit to Main
                    if( bMenuGS.contains(upPoint.x, upPoint.y) ) {
                        SceneManager.changeScene("MENU");
                    }
                    return;
                } else {
                    if (ScoreRect.contains(upPoint.x, upPoint.y)) {
                        gameOver = true;
                    }
                }
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Canvas
        bg.draw(canvas);

        // Header
        canvas.drawRect(0,0,Constants.SCREEN_WIDTH,Constants.HEADER_HEIGHT,paint);

        // Header Queue
        if (QueueType.equals("SELECT")) {
            canvas.drawRect(SelectedRect,overlayPaint);
            for(IGameObject obj : queueObjects) {
                obj.draw(canvas);
            }
        } else if (QueueType.equals("RANDOM")) {
            obstacleQueue.draw(canvas);
        }

        //Transparent shapes
        if (isLayerRefresh) {
            refreshLayer(canvas);
            isLayerRefresh = false;
        }

        if (bitmapLayer != null) {
            canvas.drawBitmap(bitmapLayer, 0, 0, null);
        }

        // Score
        canvas.drawText("" + Math.round(score), 25,Constants.HEADER_HEIGHT + 100, scorePaint );

        if (!gameStart || gameOver) {
            // Display Level Details and button to start game
            canvas.drawRect(gameStartBack, paint);
            canvas.drawRect(gameOverlay, overlayPaint);

            // Level Details
            drawCenterText(canvas, Color.WHITE, 100,(int)gameStartBack.top + 150, "Hidden Picture");
            drawCenterText(canvas, Color.WHITE, 75, (int)gameStartBack.top + 300, "Uncover the picture!");
            drawCenterText(canvas, Color.WHITE, 75, (int)gameStartBack.top + 375, "Click the score to finish.");
            //Button: Start Level
            canvas.drawRoundRect( bStartLevel, 25,25, paint);
            canvas.drawRoundRect( bStartLevel, 25,25, bPaint);
            if (gameOver) {
                drawCenterText(canvas, Color.WHITE, 75,(int)bStartLevel.centerY(), "Next Level");
            } else {
                drawCenterText(canvas, Color.WHITE, 75,(int)bStartLevel.centerY(), "Start Level");
            }
            //Button: Queue Type
            canvas.drawRoundRect( bQueueType, 25,25, paint);
            canvas.drawRoundRect( bQueueType, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bQueueType.centerY(), "Toggle Queue");
            //Button: Exit to Main
            canvas.drawRoundRect( bMenuGS, 25,25, paint);
            canvas.drawRoundRect( bMenuGS, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bMenuGS.centerY(), "Back to Menu");
        } else {
            // game started
            //Game Objects
            if (addingShape) {
                for(IGameObject obj : gameObjects){
                    if ( obj.InGameArea() && obj.equals(gameObjects.get(gameObjects.size() - 1))) {
                        obj.draw(canvas);
                    }
                }
            }

            //Explosions
            for(ParticleExplosion pe : explosions) {
                if (pe.getState() == 0) { pe.draw(canvas); }
            }
        }
    }

    @Override
    public void update() {
        IGameObject currentObject = new Obstacle(0,0,0,0,0);

        if (QueueType.equals("RANDOM")) {
            if(obstacleQueue.getCount() <= 5) {
                    obstacleQueue.addItem();
            }
        }

        if(gameObjects.size() > 0){
            currentObject = gameObjects.get(gameObjects.size() - 1);
        }

        if(!gameOver) {
            //Update obstacles

            // If addingShape -> Grow object
            if (addingShape){
                if (addShape) {
                    if (QueueType.equals("SELECT")) {
                        for (IGameObject gob: queueObjects) {
                            if ( gob.getType().equals(currentObject.getType()) ) {
                                explosions.add(new ParticleExplosion( 5, 75.0f, gob.getCenter(), gob.getType(), false ));
                            }
                        }
                    } else if (QueueType.equals("RANDOM")) {
                        explosions.add(new ParticleExplosion( 5, 75.0f, new PointF(Constants.SCREEN_WIDTH - 100f,Constants.HEADER_HEIGHT/2f), currentObject.getType(), false ));
                    }
                    addShape = false;
                }
                //handle moving shape
                if(objectMoving) {
                    currentObject.update(movePoint);
                    objectMoving = false;
                    movePoint = new PointF(0,0);
                }

                currentObject.grow(speed);
                if(System.currentTimeMillis() - objectTime >= 300) {
                    speed += 1;
                    objectTime = System.currentTimeMillis();
                }

                // If current hits another game object -> pop object
                boolean currPop = false;
                IGameObject gobPop = new Obstacle(0, 0,0,0,0);

                for(IGameObject gob: gameObjects) {
                    if( !gob.equals(currentObject) ) {
                        if (CollisionManager.GameObjectCollide(currentObject, gob)) {
                            currPop = true;
                            gobPop = gob;
                        }
                    }
                }

                // If hit other object or hit edge -> pop object
                if (currPop) {
                    currentObject.pop();
                    explosions.add(new ParticleExplosion( (int)currentObject.getSize()/partCount, currentObject.getSize(), currentObject.getCenter(), currentObject.getType(), true ));
                    gameObjects.remove(currentObject);
                    //gameSounds.playSound("POP");
                    SoundManager.playSound("POP");
                    isLayerRefresh = true;

                    gobPop.pop();
                    explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                    gameObjects.remove(gobPop);
                    //gameSounds.playSound("POP");
                    SoundManager.playSound("POP");
                    addingShape = false;
                    objectPop = true;
                    objectTime = System.currentTimeMillis();
                } else {
                    if (!currentObject.InGameArea()) {
                        currentObject.pop();
                        explosions.add(new ParticleExplosion( (int)currentObject.getSize()/partCount, currentObject.getSize(), currentObject.getCenter(), currentObject.getType(), true ));
                        gameObjects.remove(currentObject);
                        //gameSounds.playSound("POP");
                        SoundManager.playSound("POP");
                        isLayerRefresh = true;
                        addingShape = false;
                        objectPop = true;
                        objectTime = System.currentTimeMillis();
                    }
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

    private void setupQueueObjects() {
        queueObjects = new ArrayList<>();
        IGameObject qob;
        int y = Constants.HEADER_HEIGHT/2;
        float sizePercent = Constants.SCREEN_WIDTH / 7f;
        selectedRectWidth = sizePercent / 2.0f;
        float size = sizePercent / 2 * 0.85f;

        qob = ObstacleCircle.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 0.5f),y));
        qob.grow(size);
        queueObjects.add(qob);
        qob = ObstacleSquare.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 1.5f),y));
        qob.grow(size);
        queueObjects.add(qob);
        qob = ObstacleRectangle.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 2.5f),y));
        qob.grow(size);
        queueObjects.add(qob);
        qob = ObstacleTriangleUp.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 3.5f),y));
        qob.grow(size);
        queueObjects.add(qob);
        qob = ObstacleTriangleDown.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 4.5f),y));
        qob.grow(size);
        queueObjects.add(qob);
        qob = ObstacleRhombus.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 5.5f),y));
        qob.grow(size*.7f);
        queueObjects.add(qob);
        qob = ObstacleHexagon.GetInstance();
        qob.update(new PointF(Constants.SCREEN_WIDTH - (sizePercent * 6.5f),y));
        qob.grow(size);
        queueObjects.add(qob);
    }

    private void calcScore() {
        float grayArea = (Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT));
        float coveredArea = 0.0f;
        for (IGameObject gob : gameObjects ) {
            coveredArea += gob.getArea();
        }
        score = (coveredArea / grayArea ) * 100.0f;
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

    private void refreshLayer(Canvas canvas) {
        bitmapLayer = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmapLayer);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(240);
        temp.drawRect(0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, paint);

        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        for(IGameObject gob: gameObjects){
            gob.setPaint(paint);
            gob.draw(temp);
        }
    }

}