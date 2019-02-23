package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

/*** Area Game ***/

public class GamePlayArea extends GamePlayBase implements IScene {
    private ObstacleManager obstacleManager;
    private Levels.Level lvl;

    private final float bFirstY = Constants.SCREEN_HEIGHT/2f;
    private final float bGap = Constants.BTN_HEIGHT;

    private RectF bStartLevel = new RectF(Constants.BTN_LEFT, (Constants.SCREEN_HEIGHT / 2f) + bGap, Constants.BTN_RIGHT, (Constants.SCREEN_HEIGHT / 2f) + bGap + (Constants.BTN_HEIGHT * 3));
    private RectF bLevelGS = new RectF(Constants.BTN_LEFT, bStartLevel.bottom + bGap, Constants.BTN_RIGHT, bStartLevel.bottom + bGap + (Constants.BTN_HEIGHT * 3));

    private RectF bReplay = new RectF(Constants.BTN_LEFT, bFirstY, Constants.BTN_RIGHT, bFirstY + (Constants.BTN_HEIGHT * 3));
    private RectF bNext = new RectF(Constants.BTN_LEFT, bReplay.bottom + bGap, Constants.BTN_RIGHT, bReplay.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bLevelGO = new RectF(Constants.BTN_LEFT, bNext.bottom + bGap, Constants.BTN_RIGHT, bNext.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bMenu = new RectF(Constants.BTN_LEFT, bLevelGO.bottom + bGap, Constants.BTN_RIGHT, bLevelGO.bottom + (Constants.BTN_HEIGHT * 3) + bGap);

    public GamePlayArea(Levels.Level level) {
        lvl = level;
        playerPoint = new PointF(0, 0);
        movePoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();

        // Setup objects from level
        if (SceneManager.level.getNumber() == 0) {
            // Level 0 - Random
            obstacleManager = new ObstacleManager( null , Color.parseColor( Common.getPreferenceString("color_Obj") ));
            obstacleQueue = new ObstacleQueue(Common.randomInt(5,10));
        } else {
            obstacleManager = new ObstacleManager(lvl.getLevelObjects(),Color.parseColor( Common.getPreferenceString("color_Obj") ));
            obstacleQueue = new ObstacleQueue(0);
            obstacleQueue.addItem(lvl.getGameObjects());
        }

        SelectedObject = obstacleQueue.getItem();
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
        //Header Paint
        hPaint = new Paint();
        hPaint.setTextSize(Constants.TXT_MD);
        hPaint.setStyle(Paint.Style.FILL);
        hPaint.setColor(Color.WHITE);
        //Text Paint
        txtPaint = new Paint();
        txtPaint.setTextAlign(Paint.Align.LEFT);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(Constants.TXT_SM);

        //Background
        bg = new Background(0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);

        overlayPaint = new Paint();
        overlayPaint.setDither(true);
        overlayPaint.setAntiAlias(true);
        overlayPaint.setARGB(75,0,0,0);

        //Game Sounds
        SoundManager.setGameMusic("AREA");
    }

    private void reset(Levels.Level level) {
        lvl = level;
        gameStart = false;
        gameOver = false;
        objectPop = false;
        addingShape = false;
        addShape = false;
        objectMoving = false;
        playerPoint = new PointF(0, 0);
        movePoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        // Setup objects from level
        if (lvl.getNumber() == 0) {
            // Level 0 - Random
            obstacleManager = new ObstacleManager( null , Color.parseColor( Common.getPreferenceString("color_Obj") ));
            obstacleQueue = new ObstacleQueue(Common.randomInt(5,10));
        } else {
            obstacleManager = new ObstacleManager(lvl.getLevelObjects(),Color.parseColor( Common.getPreferenceString("color_Obj") ));
            obstacleQueue = new ObstacleQueue(0);
            obstacleQueue.addItem(lvl.getGameObjects());
        }
        SelectedObject = obstacleQueue.getItem();
        score = 0;
        speed = 1;
        partCount = Common.getPreferenceInteger("particles");
        bHighScore = false;
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
                    SelectedObject = obstacleQueue.getItem();
                    break;
                }

                //Header selection
                if (point.y <= Constants.HEADER_HEIGHT) {
                    return;
                }

                // Queue selection
                SelectedObject = obstacleQueue.getItem();

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
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            objectTime = System.currentTimeMillis();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(!gameOver && addingShape) {
                    //move shape in update method
                    movePoint = new PointF( event.getX(), event.getY() );
                    if ((gameObjects.size() > 0) && (movePoint.x > 0) && (movePoint.x < Constants.SCREEN_WIDTH) && (movePoint.y > Constants.HEADER_HEIGHT) && (movePoint.y < Constants.SCREEN_HEIGHT)){
                        objectMoving = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                PointF upPoint = new PointF( event.getX(), event.getY() );
                if(!gameStart) {
                    //Button: Start Level
                    if( bStartLevel.contains(upPoint.x, upPoint.y) ) {
                        gameStart = true;
                        SoundManager.playMusic();
                    }
                    //Button: Level Select
                    if( bLevelGS.contains(upPoint.x, upPoint.y) ) {
                        SceneManager.changeScene("AREA_LEVELS");
                    }
                    return;
                }
                if (gameOver) {
                    if(System.currentTimeMillis() - gameOverTime >= 500) {
                        //Button: Replay Level
                        if( bReplay.contains(upPoint.x, upPoint.y) ) {
                            reset(SceneManager.level);
                            return;
                        }
                        //Button: Next Level
                        if( bNext.contains(upPoint.x, upPoint.y) ) {
                            SceneManager.changeLevel( SceneManager.ACTIVE_LEVEL + 1, true );
                            reset(SceneManager.level);
                            return;
                        }
                        //Button: Level Select
                        if( bLevelGO.contains(upPoint.x, upPoint.y) ) {
                            SceneManager.changeScene("AREA_LEVELS");
                            return;
                        }
                        //Button: Exit to Main
                        if( bMenu.contains(upPoint.x, upPoint.y) ) {
                            SceneManager.changeScene("MENU");
                            return;
                        }
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
        canvas.drawRect(0,0, Constants.SCREEN_WIDTH, Constants.HEADER_HEIGHT,paint);

        // Header Queue
        obstacleQueue.draw(canvas);

        // Score
        canvas.drawText("" + Math.round(score), Constants.BTN_HEIGHT, (Constants.BTN_HEIGHT * 3), hPaint );

        // Obstacles
        obstacleManager.draw(canvas);

        if (!gameStart) {
            // Display Level Details and button to start game
            canvas.drawRect(gameStartBack, paint);
            canvas.drawRect(gameOverlay, overlayPaint);

            // Level Details
            drawCenterText(canvas, Color.WHITE, Constants.TXT_MD,(int)gameStartBack.top + (int)(Constants.BTN_HEIGHT * 3), lvl.getName());
            drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)gameStartBack.top + (int)(Constants.BTN_HEIGHT * 6), lvl.getDesc());
            drawCenterText(canvas, Color.WHITE, Constants.TXT_XS,(int)gameStartBack.top + (int)(Constants.BTN_HEIGHT * 8),"Shapes in Queue: " + obstacleQueue.getCount() ) ;
            drawCenterText(canvas, Color.WHITE, Constants.TXT_XS,(int)gameStartBack.top + (int)(Constants.BTN_HEIGHT * 10),"Shapes to Avoid: " + obstacleManager.getCount() );
            //Button: Start Level
            canvas.drawRoundRect( bStartLevel, 25,25, paint);
            canvas.drawRoundRect( bStartLevel, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bStartLevel.centerY(), "Start Level");
            //Button: Level Select
            canvas.drawRoundRect( bLevelGS, 25,25, paint);
            canvas.drawRoundRect( bLevelGS, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bLevelGS.centerY(), "Level Select");
        } else {
            // game started
            //Game Objects
            for(IGameObject obj : gameObjects){
                if ( obj.InGameArea() ) { obj.draw(canvas); }
            }

            //Explosions
            for(ParticleExplosion pe : explosions) {
                if (pe.getState() == 0) { pe.draw(canvas); }
            }

            // 'Game over' OR 'Out of shapes'
            if(gameOver) {
                canvas.drawRect(gameOverlay, overlayPaint);

                int vHeight = Constants.HEADER_HEIGHT/2;
                drawCenterText(canvas, Color.WHITE, Constants.TXT_MD, vHeight, "Game Over!");

                // High scores
                vHeight = Constants.HEADER_HEIGHT + (int)(Constants.BTN_HEIGHT * 2);
                drawCenterText(canvas, Color.WHITE, Constants.TXT_MD, vHeight,"Level " + SceneManager.ACTIVE_LEVEL + " High Scores:");
                vHeight += Constants.BTN_HEIGHT;
                for (Highscores.Highscore hs: SceneManager.highscores.getHighscores()) {
                    vHeight += (Constants.BTN_HEIGHT * 2);
                    canvas.drawText(hs.getNumber() + ": " + hs.getScore() + " - " + hs.getName(), Constants.SCREEN_WIDTH/3f, vHeight, txtPaint);
                }

                //Button: Replay Level
                canvas.drawRoundRect( bReplay, 25,25, paint);
                canvas.drawRoundRect( bReplay, 25,25, bPaint);
                drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bReplay.centerY(),"Replay Level");

                //Button: Next Level
                canvas.drawRoundRect( bNext, 25,25, paint);
                canvas.drawRoundRect( bNext, 25,25, bPaint);
                drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bNext.centerY(),"Next Level");

                //Button: Level Select
                canvas.drawRoundRect( bLevelGO, 25,25, paint);
                canvas.drawRoundRect( bLevelGO, 25,25, bPaint);
                drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bLevelGO.centerY(),"Level Select");

                //Button: Back to Menu
                canvas.drawRoundRect( bMenu, 25,25, paint);
                canvas.drawRoundRect( bMenu, 25,25, bPaint);
                drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bMenu.centerY(),"Back to Menu");
            }
        }
    }

    @Override
    public void update() {
        if (Constants.GAME_STATUS.equals("GAMELOOP")) {
            IGameObject currentObject = new Obstacle(0,0,0,0,0);

            if(gameObjects.size() > 0){
                currentObject = gameObjects.get(gameObjects.size() - 1);
            }

            if(!gameOver) {
                //Update obstacles

                // If addingShape -> Grow object
                if (addingShape){
                    if (addShape) {
                        explosions.add(new ParticleExplosion( 5, 75.0f, new PointF(Constants.SCREEN_WIDTH - 100f, Constants.HEADER_HEIGHT / 2f), currentObject.getType(), false ));
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

                    // If hit obstacle -> game over
                    if(obstacleManager.obstacleManagerCollide(currentObject)) {
                        bHighScore = true;
                    }

                    // If current hits another game object -> pop object
                    boolean currPop = false;
                    IGameObject gobPop = new Obstacle(0, 0,0,0,0);

                    for(IGameObject gob: gameObjects) {
                        if( !gob.equals(currentObject) ) {
                            if (CollisionManager.GameObjectCollide(currentObject,gob)) {
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
                        SoundManager.playSound("POP");

                        gobPop.pop();
                        explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                        gameObjects.remove(gobPop);
                        SoundManager.playSound("POP");
                        addingShape = false;
                        objectPop = true;
                        objectTime = System.currentTimeMillis();
                    } else {
                        if (!currentObject.InGameArea()) {
                            currentObject.pop();
                            explosions.add(new ParticleExplosion( (int)currentObject.getSize()/partCount, currentObject.getSize(), currentObject.getCenter(), currentObject.getType(), true ));
                            gameObjects.remove(currentObject);
                            SoundManager.playSound("POP");
                            addingShape = false;
                            objectPop = true;
                            objectTime = System.currentTimeMillis();
                        }
                    }

                    // Calculate score
                    calcScore();
                } else {
                    // Not GameOver. Not Adding Shape. Out of Shapes
                    if(SelectedObject == null) {
                        bHighScore = true;
                    }
                }

                if(objectPop){
                    if(System.currentTimeMillis() - objectTime >= 250) {
                        objectPop = false;
                    }
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

        if (bHighScore) {
            bHighScore = false;
            gameOver = true;
            gameOverTime = System.currentTimeMillis();
            calcScore();
            //Set High Score
            if (Math.round(score) > SceneManager.highscores.getMinHighscore() ) {
                SceneManager.highscores.updateHighscores(Math.round(score), Common.getPreferenceString("username"));
                SceneManager.saveScores();
            }
        }
    }

    private void calcScore() {
        float grayArea = (Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT)) - obstacleManager.getArea();
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
}