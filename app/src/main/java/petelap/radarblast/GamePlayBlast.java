package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

/*** Blast Game ***/

public class GamePlayBlast extends GamePlayBase implements IScene  {
    private ArrayList<IGameObjectSpecial> gameSpecialObjects;
    private long newItemTime;
    private long newItemRate;
    private GameTimer gameTimer;
    private boolean isGamePaused;
    private boolean isDoublePoints;
    private long expireDoublePoints;
    private boolean isFrenzy;
    private long expireFrenzy;
    private boolean isFreeze;
    private long expireFreeze;

    private final float bFirstY = Constants.SCREEN_HEIGHT * 0.7f;
    private final float bGap = Constants.BTN_HEIGHT;

    private RectF bStartLevel = new RectF(Constants.BTN_LEFT, bFirstY, Constants.BTN_RIGHT, bFirstY + (Constants.BTN_HEIGHT * 3));
    private RectF bMenu = new RectF(Constants.BTN_LEFT, bStartLevel.bottom + bGap, Constants.BTN_RIGHT, bStartLevel.bottom + bGap + (Constants.BTN_HEIGHT * 3));

    public GamePlayBlast() {
        playerPoint = new PointF(0, 0);
        gameObjects = new ArrayList<>();
        gameSpecialObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleQueue = new ObstacleQueue(10);
        SelectedObject = obstacleQueue.getItem();
        newItemTime = System.currentTimeMillis();
        newItemRate = 1000;
        score = 0;
        speed = 2;
        partCount = Common.getPreferenceInteger("particles");
        gameTimer = new GameTimer(60000);
        isGamePaused = false;
        gameStart = false;
        gameOver = false;
        isDoublePoints = false;
        isFrenzy = false;
        expireFrenzy = 0;
        isFreeze = false;
        expireDoublePoints = 0;
        expireFreeze = 0;

        // Background
        bg = new Background(0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        overlayPaint = new Paint();
        overlayPaint.setDither(true);
        overlayPaint.setAntiAlias(true);
        overlayPaint.setARGB(75,0,0,0);
        gameOverlay= new RectF(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        //Button paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        //Border paint
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);

        //Game Sounds
        SoundManager.setGameMusic("BLAST");
    }

    private void reset() {
        playerPoint = new PointF(0, 0);
        gameObjects = new ArrayList<>();
        gameSpecialObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleQueue = new ObstacleQueue(25);
        SelectedObject = obstacleQueue.getItem();
        newItemTime = System.currentTimeMillis();
        newItemRate = 1000;
        score = 0;
        speed = 2;
        partCount = Common.getPreferenceInteger("particles");
        gameTimer = new GameTimer(60000);
        isGamePaused = false;
        gameStart = false;
        gameOver = false;
        isDoublePoints = false;
        expireDoublePoints = 0;
        isFrenzy = false;
        expireFrenzy = 0;
        isFreeze = false;
        expireFreeze = 0;
    }

    @Override
    public void terminate() {
        //Constants.SOUND_MANAGER.stop();
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                playerPoint.x = event.getX();
                playerPoint.y = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                if(!gameStart) {
                    //Button: Start Level
                    if (bStartLevel.contains(event.getX(), event.getY())) {
                        gameStart = true;
                        gameTimer.startTimer();
                        SoundManager.playMusic();
                    }
                    //Button: Menu
                    if (bMenu.contains(event.getX(), event.getY())) {
                        SceneManager.changeScene("MENU");
                    }
                }

                if(gameOver) {
                    if(((System.currentTimeMillis() - gameOverTime) >= 500)) {
                        //Button: Start Level
                        if (bStartLevel.contains(event.getX(), event.getY())){
                            reset();
                        }
                        //Button: Menu
                        if (bMenu.contains(event.getX(), event.getY())) {
                            SceneManager.changeScene("MENU");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Canvas / Background
        bg.draw(canvas);

        //Game Objects
        for(IGameObject obj : gameObjects){
            if ( obj.InGameArea() ) { obj.draw(canvas); }
        }

        //Game Special Objects
        for(IGameObjectSpecial obj : gameSpecialObjects){
            if ( obj.InGameArea() ) { obj.draw(canvas); }
        }

        //Explosions
        for(ParticleExplosion pe : explosions) {
           if (pe.getState() == 0) { pe.draw(canvas); }
        }

        // Score
        Paint scorePaint = new Paint();
        scorePaint.setTextSize(Constants.TXT_MD);
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setColor(Color.WHITE);
        canvas.drawText("" + Math.round(score), Constants.BTN_HEIGHT,(Constants.BTN_HEIGHT * 3), scorePaint );

        // Countdown Timer
        scorePaint.setColor(Color.GREEN);
        canvas.drawText("" + gameTimer.getTimeLeftInSeconds(), Constants.SCREEN_WIDTH - (Constants.BTN_HEIGHT * 3),(Constants.BTN_HEIGHT * 3), scorePaint );

        if(!gameStart || gameOver) {
            canvas.drawRect(gameOverlay, overlayPaint);

            int vHeight = Constants.SCREEN_HEIGHT/4;
            if (!gameStart) {
                drawCenterText(canvas, Color.WHITE, Constants.TXT_MD, vHeight,"Blast Game!");
            }
            if (gameOver) {
                drawCenterText(canvas, Color.WHITE, Constants.TXT_MD, vHeight,"Time's up!");
            }

            vHeight += (Constants.BTN_HEIGHT * 3);

            // High scores
            Paint txtPaint = new Paint();
            txtPaint.setTextAlign(Paint.Align.LEFT);
            txtPaint.setColor(Color.WHITE);
            txtPaint.setTextSize(Constants.TXT_SM);

            drawCenterText(canvas, Color.WHITE, Constants.TXT_MD, vHeight,"High Scores: ");
            vHeight += Constants.BTN_HEIGHT;
            for (Highscores.Highscore hs: SceneManager.highscores.getHighscores()) {
                vHeight += (Constants.BTN_HEIGHT * 2);
                canvas.drawText(hs.getNumber() + ": " + hs.getScore() + " - " + hs.getName(), Constants.SCREEN_WIDTH/3f, vHeight, txtPaint);
            }

            //Button: Start Level
            canvas.drawRoundRect( bStartLevel, 25,25, paint);
            canvas.drawRoundRect( bStartLevel, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bStartLevel.centerY(), "Start Level");
            //Button: Menu
            canvas.drawRoundRect( bMenu, 25,25, paint);
            canvas.drawRoundRect( bMenu, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, Constants.TXT_SM, (int)bMenu.centerY(), "Back to Menu");
        }

        // Special notifications
        if(!gameOver) {
            if (isDoublePoints) {
                drawCenterText(canvas, Color.WHITE, Constants.TXT_XS, Constants.SCREEN_HEIGHT/2 + (int)(Constants.BTN_HEIGHT * 2),"Double Points");
            }
            if (isFrenzy) {
                drawCenterText(canvas, Color.WHITE, Constants.TXT_XS, Constants.SCREEN_HEIGHT/2 + (int)(Constants.BTN_HEIGHT * 4),"Frenzy");
            }
            if (isFreeze) {
                drawCenterText(canvas, Color.WHITE, Constants.TXT_XS, Constants.SCREEN_HEIGHT/2 + (int)(Constants.BTN_HEIGHT * 6),"Freeze");
            }
        }
    }

    @Override
    public void update() {
        if (Constants.GAME_STATUS.equals("PAUSED")) {
            if (!isGamePaused) {
                gameTimer.pauseTimer();
                isGamePaused = true;
            }
        } else {
            if (gameStart && !gameOver) {
                if (isGamePaused) {
                    gameTimer.startTimer();
                    isGamePaused = false;
                }
                //IGameObject currentObject = new Obstacle(0,0,0,0);
                ArrayList<IGameObject> popped = new ArrayList<>();
                ArrayList<IGameObjectSpecial> poppedSpec = new ArrayList<>();
                ArrayList<ParticleExplosion> expDone = new ArrayList<>();

                // Handle player click
                if (playerPoint.x != 0 && playerPoint.y != 0){
                    boolean currPop = false;
                    IGameObjectSpecial gobPopSpec = new ObstacleSpecialDouble(0,0,0,0);
                    // If playerPoint is inside a game Special Object -> pop object and set Special Skill
                    for(int i = gameSpecialObjects.size() - 1; i >= 0; i--){
                        if ( gameSpecialObjects.get(i).pointInside(playerPoint)) {
                            currPop = true;
                            gameSpecialObjects.get(i).pop();
                            gobPopSpec = gameSpecialObjects.get(i);
                            playerPoint = new PointF(0,0);
                        }
                    }

                    if (currPop) {
                        explosions.add(new ParticleExplosion( 10, 10, gobPopSpec.getCenter(), gobPopSpec.getType(), true ));
                        // Set Special Skill
                        if (gobPopSpec.getType().equals("SpecialDouble")) {
                            isDoublePoints = true;
                            expireDoublePoints = System.currentTimeMillis() + 5000;
                        }
                        if (gobPopSpec.getType().equals("SpecialFrenzy")) {
                            isFrenzy = true;
                            expireFrenzy = System.currentTimeMillis() + 5000;
                        }
                        if (gobPopSpec.getType().equals("SpecialTime")) {
                            isFreeze = true;
                            expireFreeze = System.currentTimeMillis() + 5000;
                            gameTimer.pauseTimer();
                        }
                        if (gobPopSpec.getType().equals("SpecialSpike")) {
                            explosions.add(new ParticleExplosion( 20, 12, gobPopSpec.getCenter(), gobPopSpec.getType(), true ));
                            score += 5;
                            SoundManager.playSound("SPIKE");
                        }

                        gameSpecialObjects.remove(gobPopSpec);
                        playerPoint = new PointF(0,0);
                    }
                    // If playerPoint is inside a game object -> pop object
                    currPop = false;
                    IGameObject gobPop = new Obstacle(0,0,0,0,0);

                    for(int i = gameObjects.size() - 1; i >= 0; i--){
                        if ( gameObjects.get(i).pointInside(playerPoint)) {
                            currPop = true;
                            gameObjects.get(i).pop();
                            gobPop = gameObjects.get(i);
                            playerPoint = new PointF(0,0);
                        }
                    }

                    if (currPop) {
                        gobPop.pop();
                        calcScore(gobPop);
                        explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                        gameObjects.remove(gobPop);
                        speed += 1;
                        SoundManager.playSound("POP");
                    }

                    //Reset PlayerPoint
                    playerPoint = new PointF(0,0);
                }

                // If Special Spike hits object -> pop
                for(IGameObjectSpecial gob: gameSpecialObjects){
                    if(gob.getType().equals("SpecialSpike")) {
                        for(IGameObject ob: gameObjects) {
                            if (CollisionManager.GameObjectSpecialCollide(ob,gob)) {
                                explosions.add(new ParticleExplosion( (int)ob.getSize()/partCount, ob.getSize(), ob.getCenter(), ob.getType(), true ));
                                popped.add(ob);
                                SoundManager.playSound("POP");
                            }
                        }
                    }
                }
                if (!popped.isEmpty()){
                    gameObjects.removeAll(popped);
                    popped = new ArrayList<>();
                }

                // Update Special Objects -> If Special Objects hits edge -> pop -> else update
                for(IGameObjectSpecial gob: gameSpecialObjects){
                    if(!gob.InGameArea() ) {
                        poppedSpec.add(gob);
                    } else {
                        gob.update(playerPoint);
                    }
                }
                if (!poppedSpec.isEmpty()){
                    gameSpecialObjects.removeAll(poppedSpec);
                }

                // Update obstacles -> If objects current hits edge -> pop -> else grow
                for(IGameObject gob: gameObjects){
                    if(!gob.InGameArea()) {
                        popped.add(gob);
                        if(!isFrenzy) {
                            speed = 2;
                        }
                    } else {
                        gob.grow(speed);
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

                // Handle Specials
                if (isDoublePoints) {
                    if(System.currentTimeMillis() >= expireDoublePoints){
                        isDoublePoints = false;
                        expireDoublePoints = 0;
                    }
                }
                if (isFrenzy) {
                    newItemRate = 500;
                    if (System.currentTimeMillis() >= expireFrenzy){
                        newItemRate = 1000;
                        isFrenzy = false;
                        expireFrenzy = 0;
                    }
                }
                if (isFreeze) {
                    if (System.currentTimeMillis() >= expireFreeze) {
                        isFreeze = false;
                        expireFreeze = 0;
                        gameTimer.startTimer();
                    }
                }

                // Create next object in queue
                SelectedObject = obstacleQueue.getItem();
                if(SelectedObject == null) {
                    // Populate more items in queue
                    for(int i=0; i<10; i++) {
                        obstacleQueue.addItem();
                    }
                    SelectedObject = obstacleQueue.getItem();
                }
                if (System.currentTimeMillis() - newItemTime >= newItemRate) {
                    PointF newItemPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                    //addingShape = true;
                    IGameObject newObject = SelectedObject.NewInstance();
                    newObject.update(newItemPoint);
                    gameObjects.add(newObject);
                    obstacleQueue.removeItem();
                    SelectedObject = obstacleQueue.getItem();
                    newItemTime = System.currentTimeMillis();
                }

                // CountDownTimer
                gameTimer.update();
                if(gameTimer.IsFinished()) {
                    gameOver = true;
                    gameOverTime = System.currentTimeMillis();

                    //Set High Score
                    if (Math.round(score) > SceneManager.highscores.getMinHighscore() ) {
                        SceneManager.highscores.updateHighscores(Math.round(score), Common.getPreferenceString("username"));
                        SceneManager.saveScores();
                    }
                }
            }
        }
    }

    private void calcScore(IGameObject gob) {
        float grayArea = (Constants.SCREEN_WIDTH * Constants.SCREEN_HEIGHT );
        float coveredArea = gob.getArea();
        float add = (coveredArea / grayArea) * 100f;

        // Special objects
        // if object over 10% screen size, spawn special with 30% chance
        if (add > 5f) {
            float luck = Common.randomFlt(1.0f,100.0f);
            if (luck > 0 && luck <= 10f) {
                IGameObjectSpecial special = ObstacleSpecialDouble.GetInstance();
                special.update(gob.getCenter());
                gameSpecialObjects.add(special);
            }
            if (luck > 10f && luck <= 20f) {
                IGameObjectSpecial special = ObstacleSpecialFrenzy.GetInstance();
                special.update(gob.getCenter());
                gameSpecialObjects.add(special);
            }
            if (luck > 20f && luck <= 30f) {
                IGameObjectSpecial special = ObstacleSpecialTime.GetInstance();
                special.update(gob.getCenter());
                gameSpecialObjects.add(special);
            }
        }
        // if object less than 5% screen size, spawn spike with 50% chance
        if (add <= 5f) {
            float luck = Common.randomFlt(1.0f,100.0f);
            if (luck > 0 && luck <= 50f) {
                IGameObjectSpecial special = ObstacleSpecialSpike.GetInstance();
                special.update(gob.getCenter());
                gameSpecialObjects.add(special);
            }
        }

        if (add < 1f) {
            add = 1f;
        }
        if (isDoublePoints) {
            add *= 2f;
        }
        if (speed > 2f) {
            add += speed;
        }
        // Update score
        score += (int)add;

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