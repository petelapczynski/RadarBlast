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

/*** How to play ***/

public class GamePlayHowTo extends GamePlayBase implements IScene  {
    private ArrayList<IGameObjectSpecial> gameSpecialObjects;
    private PointF menuPoint;
    private ObstacleManager obstacleManager;
    private ObstacleSpecialLaser laserObject;
    private long newItemTime;
    private long startTime;
    private GameTimer gameTimer;

    private final float bFirstY = Constants.SCREEN_HEIGHT/2f - 200;
    private final float bGap = 35f;

    private RectF bArea = new RectF(bLeft, bFirstY, bRight, bFirstY + bHeight);
    private RectF bBlast = new RectF(bLeft, bArea.bottom + bGap, bRight,  bArea.bottom + bHeight + bGap);
    private RectF bPicture = new RectF(bLeft, bBlast.bottom + bGap, bRight, bBlast.bottom + bHeight + bGap);
    private RectF bLaser = new RectF(bLeft, bPicture.bottom + bGap, bRight, bPicture.bottom + bHeight + bGap);
    private RectF bMenu = new RectF(bLeft, bLaser.bottom + bGap, bRight, bLaser.bottom + bHeight + bGap);
    private RectF bNext = new RectF(Constants.SCREEN_WIDTH/2f - 125, Constants.SCREEN_HEIGHT - 250, Constants.SCREEN_WIDTH/2f + 125, Constants.SCREEN_HEIGHT - 250 + bHeight);
    private RectF bExit = new RectF(Constants.SCREEN_WIDTH - 300, Constants.SCREEN_HEIGHT - 250, Constants.SCREEN_WIDTH - 50, Constants.SCREEN_HEIGHT - 250 + bHeight);

    private boolean areaTutorial;
    private boolean blastTutorial;
    private boolean pictureTutorial;
    private boolean laserTutorial;
    private boolean bShowNext;
    private boolean bHeader;
    private boolean bAnimation;
    private int iScreen;
    private int iAnimation;

    private Bitmap bitmapLayer;
    private boolean isLayerRefresh;

    public GamePlayHowTo() {
        menuPoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        gameSpecialObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleManager = new ObstacleManager();
        obstacleQueue = new ObstacleQueue(10);
        SelectedObject = obstacleQueue.getItem();
        speed = 2;
        gameTimer = new GameTimer(60000);
        newItemTime = System.currentTimeMillis();
        areaTutorial = false;
        blastTutorial = false;
        pictureTutorial = false;
        laserTutorial = false;
        bShowNext = false;
        bHeader = false;
        bAnimation = false;
        iScreen = 0;
        iAnimation = 0;
        partCount = Common.getPreferenceInteger("particles");

        //Button paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        //Border paint
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        //Text Paint
        txtPaint = new Paint();
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(75);

        //Background
        bg = new Background(Color.DKGRAY,0,0,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        isLayerRefresh = false;
        addingShape = false;

        //Game Sounds
        SoundManager.setGameMusic("MENU");
        SoundManager.playMusic();
    }

    public void reset() {
        menuPoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        gameSpecialObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleManager = new ObstacleManager();
        obstacleQueue = new ObstacleQueue(10);
        SelectedObject = obstacleQueue.getItem();
        speed = 2;
        newItemTime = System.currentTimeMillis();
        areaTutorial = false;
        blastTutorial = false;
        pictureTutorial = false;
        laserTutorial = false;
        bShowNext = false;
        bHeader = false;
        bAnimation = false;
        iScreen = 0;
        iAnimation = 0;
        partCount = Common.getPreferenceInteger("particles");
        gameTimer = new GameTimer(60000);
        isLayerRefresh = false;
        bg = new Background(Color.DKGRAY,0,0,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        addingShape = false;
    }

    @Override
    public void terminate() {
        //Constants.SOUND_MANAGER.stop();
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                menuPoint.x = event.getX();
                menuPoint.y = event.getY();

                if (!areaTutorial && !blastTutorial && !pictureTutorial && !laserTutorial) {
                    //Button: Start Area Tutorial
                    if( bArea.contains(menuPoint.x, menuPoint.y) ) {
                        Constants.HEADER_HEIGHT = 200;
                        iScreen = 1;
                        iAnimation = 1;
                        areaTutorial = true;
                        bShowNext = false;
                        bAnimation = false;
                        startTime = System.currentTimeMillis();
                        return;
                    }
                    //Button: Start Blast Tutorial
                    if( bBlast.contains(menuPoint.x, menuPoint.y) ) {
                        Constants.HEADER_HEIGHT = 0;
                        iScreen = 1;
                        iAnimation = 1;
                        blastTutorial = true;
                        bShowNext = false;
                        bAnimation = false;
                        startTime = System.currentTimeMillis();
                        return;
                    }
                    //Button: Start Picture Tutorial
                    if( bPicture.contains(menuPoint.x, menuPoint.y) ) {
                        Constants.HEADER_HEIGHT = 0;
                        iScreen = 1;
                        iAnimation = 1;
                        pictureTutorial = true;
                        bShowNext = false;
                        bAnimation = false;
                        startTime = System.currentTimeMillis();
                        return;
                    }
                    //Button: Start Laser Tutorial
                    if( bLaser.contains(menuPoint.x, menuPoint.y) ) {
                        Constants.HEADER_HEIGHT = 0;
                        iScreen = 1;
                        iAnimation = 1;
                        laserTutorial = true;
                        bShowNext = false;
                        bAnimation = false;
                        startTime = System.currentTimeMillis();
                        return;
                    }
                    //Button: Back to Menu
                    if( bMenu.contains(menuPoint.x, menuPoint.y) ) {
                        SceneManager.changeScene("MENU");
                        return;
                    }
                } else {
                    //Button: Exit Tutorial
                    if( bExit.contains(menuPoint.x, menuPoint.y) ) {
                        Constants.HEADER_HEIGHT = 0;
                        reset();
                        return;
                    }
                    //Button: Next
                    if(bShowNext) {
                        if( bNext.contains(menuPoint.x, menuPoint.y) ) {
                            iScreen += 1;
                            iAnimation = 1;
                            bShowNext = false;
                            bAnimation = false;
                            startTime = System.currentTimeMillis();
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

        if (pictureTutorial) {
            //Transparent shapes
            if (isLayerRefresh) {
                refreshLayer(canvas);
                isLayerRefresh = false;
            }
            if (bitmapLayer != null) {
                canvas.drawBitmap(bitmapLayer, 0, 0, null);
            }
        }

        // Obstacles
        obstacleManager.draw(canvas);

        //Game Objects
        if (!pictureTutorial) {
            for(IGameObject obj : gameObjects){
                if ( obj.InGameArea() ) { obj.draw(canvas); }
            }
        }

        //Game Special Objects
        for(IGameObjectSpecial obj : gameSpecialObjects){
            if ( obj.InGameArea() ) { obj.draw(canvas); }
        }

        //Explosions
        for(ParticleExplosion pe : explosions) {
           if (pe.getState() == 0) { pe.draw(canvas); }
        }

        if (!areaTutorial && !blastTutorial && !pictureTutorial && !laserTutorial) {
            //Logo
            drawCenterText(canvas, Color.WHITE, 150, Constants.SCREEN_HEIGHT/4, "How to Play", true);
            //Button: Start Area Tutorial
            canvas.drawRoundRect( bArea, 25,25, paint);
            canvas.drawRoundRect( bArea, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bArea.centerY(), "Area Game", false);

            //Button: Start Blast Tutorial
            canvas.drawRoundRect( bBlast, 25,25, paint);
            canvas.drawRoundRect( bBlast, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bBlast.centerY(), "Blast Game", false);

            //Button: Start Picture Tutorial
            canvas.drawRoundRect( bPicture, 25,25, paint);
            canvas.drawRoundRect( bPicture, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bPicture.centerY(), "Picture Game", false);

            //Button: Start Laser Tutorial
            canvas.drawRoundRect( bLaser, 25,25, paint);
            canvas.drawRoundRect( bLaser, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bLaser.centerY(), "Laser Game", false);

            //Button: Back to Menu
            canvas.drawRoundRect( bMenu, 25,25, paint);
            canvas.drawRoundRect( bMenu, 25,25, bPaint);
            drawCenterText(canvas, Color.WHITE, 75,(int)bMenu.centerY(), "Back to Menu", false);
        } else {
            //Button: Exit
            canvas.drawRoundRect( bExit, 25,25, paint);
            canvas.drawRoundRect( bExit, 25,25, bPaint);
            canvas.drawText("Exit", bExit.centerX(), bExit.bottom - 50, txtPaint);

            //Button: Next
            if(bShowNext) {
                canvas.drawRoundRect( bNext, 25,25, paint);
                canvas.drawRoundRect( bNext, 25,25, bPaint);
                drawCenterText(canvas, Color.WHITE, 75,(int)bNext.centerY(), "Next", false);
            }
        }

        if (areaTutorial) {
            /*
            How is the 'Area Game' played?

            Great question! I'm glad you asked!

            The goal is to cover the play area
            with shapes while avoiding obstacles.

            Let's learn about the game shapes.
            Each level you have a queue of shapes.
            You only see the next 5 shapes.
            The next shape it on the right.

            Tap the play area to start the shape.
            It grows from the center.

            Tap again to stop growing the shape.
            Size does matter in this game!

            Hit the play area edge...
            The shape will pop.

            Hit another shape...
            Both shapes pop.

            The level ends...
            when you run out of shapes
            or hit a [Green] box.

            Have Fun!
            */

            switch (iScreen) {
                case 1:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Area Game' played?", false);
                    break;
                case 2:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Area Game' played?", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Great question! I'm glad you asked!", false);
                    break;
                case 3:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The goal is to cover the play area", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"with shapes while avoiding obstacles.", false);
                    break;
                case 4:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Let's learn about the game shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Each level you have a queue of shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"You only see the next 5 shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 300,"The next shape is on the right =>", false);
                    break;
                case 5:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Tap the play area to start the shape.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"It grows from the center.", false);
                    break;
                case 6:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Tap again to stop growing the shape.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Size does matter in this game!", false);
                    break;
                case 7:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Hit the play area edge...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"The shape will pop.", false);
                    break;
                case 8:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Hit another shape...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Both shapes pop.", false);
                    break;
                case 9:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The level ends...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"when you are out of shapes", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"or hit a [Green] box.", false);
                    break;
                case 10:
                    drawCenterText(canvas, Color.WHITE, 125, (int)bFirstY,"Have Fun!", false);
                    break;
                default:
                    break;
            }

            if (bHeader) {
                // Header
                Paint hPaint = new Paint();
                hPaint.setColor(Color.GRAY);
                canvas.drawRect(0,0,Constants.SCREEN_WIDTH,Constants.HEADER_HEIGHT,paint);
                // Header Queue
                obstacleQueue.draw(canvas);
                // Score
                hPaint.setTextSize(100);
                hPaint.setStyle(Paint.Style.FILL);
                hPaint.setColor(Color.WHITE);
                canvas.drawText("" + Math.round(score), 50,150, hPaint );
            }
        }

        if (blastTutorial) {
            /*
            How is the 'Blast Game' played?

            Great question! I'm glad you asked!

            The goal is to pop shapes to get the
            highest score in 60 seconds.
            Size does matter in this game!

            Shapes will spawn and grow until
            they are tapped or hit the edge.
            If tapped... points are earned.
            If they hit the edge... no points.

            Keeping a combo streak alive
            earns faster shape growth
            and a higher point modifier

            If a large shape is tapped
            there is a chance for good things.

            Double Points: Earn double points
            Freeze: Timer stops for 5 seconds
            Frenzy: More shapes spawn

            If a small shape is popped
            there is a chance for bad things.

            Spike Ball: If it touches another
            shape it pops it for no points.
            The Spike Ball itself can be popped.

            The level ends...
            when the countdown is reached.

            Have Fun!

            */
            switch (iScreen) {
                case 1:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Blast Game' played?", false);
                    break;
                case 2:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Blast Game' played?", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Great question! I'm glad you asked!", false);
                    break;
                case 3:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The goal is to pop shapes to get the", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"highest score in 60 seconds.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"Size does matter in this game!", false);
                    break;
                case 4:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Shapes will spawn and grow until", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"they are tapped or hit the edge.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"If tapped... points are earned.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 300,"If they hit the edge... no points.", false);
                    break;
                case 5:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Keeping a combo streak alive", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"earns faster shape growth", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"and a higher point modifier.", false);
                    break;
                case 6:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"If a large shape is tapped", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"there is a chance for good things.", false);
                    break;
                case 7:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Double Points: Earn double points", false);
                    //
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"Freeze: Timer stops for 5 seconds", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 400,"Frenzy: More shapes spawn", false);
                    //add double pts, freeze, frenzy.
                    break;
                case 8:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"If a small shape is popped", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"there is a chance for bad things.", false);
                    break;
                case 9:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Spike Ball: If it touches another", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"shape it pops it for no points.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"The Spike Ball itself can be popped.", false);
                    break;
                case 10:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The level ends...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"when the countdown is reached.", false);
                    break;
                case 11:
                    drawCenterText(canvas, Color.WHITE, 125, (int)bFirstY,"Have Fun!", false);
                    break;
                default:
                    break;
            }
            if (bHeader) {
                // Score
                Paint hPaint = new Paint();
                hPaint.setTextSize(100);
                hPaint.setStyle(Paint.Style.FILL);
                hPaint.setColor(Color.WHITE);
                canvas.drawText("" + Math.round(score), 50,150, hPaint );

                // Countdown Timer
                hPaint.setColor(Color.GREEN);
                canvas.drawText("" + gameTimer.getTimeLeftInSeconds(), Constants.SCREEN_WIDTH - 150,150, hPaint );
            }
        }

        if (pictureTutorial) {
            /*
            How is the 'Picture Game' played?

            Great question! I'm glad you asked!

            The goal is to uncover the play area
            with shapes to reveal the picture.

            Let's learn about the game shapes.
            Each level you have a queue of shapes.

            Tap the play area to start the shape.
            It grows from the center.

            Tap again to stop growing the shape.
            A portion of the picture is revealed!

            Hit the play area edge...
            The shape will pop.

            Hit part that is uncovered...
            Both shapes pop.

            Have Fun!
            */
            if (addingShape) {
                for(IGameObject obj : gameObjects){
                    if ( obj.InGameArea() && obj.equals(gameObjects.get(gameObjects.size() - 1))) {
                        obj.draw(canvas);
                    }
                }
            }

            switch (iScreen) {
                case 1:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Picture Game' played?", false);
                    break;
                case 2:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Picture Game' played?", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Great question! I'm glad you asked!", false);
                    break;
                case 3:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The goal is to uncover the play area", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"with shapes to reveal the picture.", false);
                    break;
                case 4:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Let's learn about the game shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Each level you have a queue of shapes.", false);
                    break;
                case 5:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Tap the play area to start the shape.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"It grows from the center.", false);
                    break;
                case 6:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Tap again to stop growing the shape.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"A portion of the picture is revealed!", false);
                    break;
                case 7:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Hit the play area edge...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"The shape will pop.", false);
                    break;
                case 8:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Hit part that is uncovered...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Both shapes pop.", false);
                    break;
                case 9:
                    drawCenterText(canvas, Color.WHITE, 125, (int)bFirstY,"Have Fun!", false);
                    break;
                default:
                    break;
            }

            if (bHeader) {
                // Header
                Paint hPaint = new Paint();
                hPaint.setColor(Color.GRAY);
                canvas.drawRect(0,0,Constants.SCREEN_WIDTH,Constants.HEADER_HEIGHT,paint);
                // Header Queue
                obstacleQueue.draw(canvas);
                // Score
                hPaint.setTextSize(100);
                hPaint.setStyle(Paint.Style.FILL);
                hPaint.setColor(Color.WHITE);
                canvas.drawText("" + Math.round(score), 50,150, hPaint );
            }
        }

        if (laserTutorial) {
            /*
            How is the 'Laser Game' played?

            Great question! I'm glad you asked!

            The goal is to keep the laser
            inside of the play area.
            Bounce the laser off shapes
            to earn points.

            Let's learn about the game shapes.
            Each level you have a queue of shapes.
            You only see the next 5 shapes.
            The next shape it on the right.

            Tap the play area to start the shape.
            It grows from the center.

            Tap again to stop growing the shape.
            Size does matter in this game!

            Hit the play area edge...
            The shape will pop.

            Hit another shape...
            Both shapes pop.

            The level ends...
            when the laser hits the edge.

            Have Fun!
            */
            if (iScreen >= 3) {
                //Laser object
                laserObject.draw(canvas);
            }
            switch (iScreen) {
                case 1:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Laser Game' played?", false);
                    break;
                case 2:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"How is the 'Laser Game' played?", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Great question! I'm glad you asked!", false);
                    break;
                case 3:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The goal is to keep the laser", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"inside of the play area.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"Bounce the laser off shapes", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 300,"to earn points.", false);
                    break;
                case 4:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Let's learn about the game shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Each level you have a queue of shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 200,"You only see the next 5 shapes.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 300,"The next shape is on the right =>", false);
                    break;
                case 5:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Tap the play area to start the shape.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"It grows from the center.", false);
                    break;
                case 6:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Tap again to stop growing the shape.", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Size does matter in this game!", false);
                    break;
                case 7:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Hit the play area edge...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"The shape will pop.", false);
                    break;
                case 8:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"Hit another shape...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"Both shapes pop.", false);
                    break;
                case 9:
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY,"The level ends...", false);
                    drawCenterText(canvas, Color.WHITE, 60, (int)bFirstY + 100,"when the laser hits the edge.", false);
                    break;
                case 10:
                    drawCenterText(canvas, Color.WHITE, 125, (int)bFirstY,"Have Fun!", false);
                    break;
                default:
                    break;
            }

            if (bHeader) {
                // Header
                Paint hPaint = new Paint();
                hPaint.setColor(Color.GRAY);
                canvas.drawRect(0,0,Constants.SCREEN_WIDTH,Constants.HEADER_HEIGHT,paint);
                // Header Queue
                obstacleQueue.draw(canvas);
                // Score
                hPaint.setTextSize(100);
                hPaint.setStyle(Paint.Style.FILL);
                hPaint.setColor(Color.WHITE);
                canvas.drawText("" + Math.round(score), 50,150, hPaint );
            }
        }
    }

    @Override
    public void update() {
        ArrayList<IGameObject> popped = new ArrayList<>();
        ArrayList<ParticleExplosion> expDone = new ArrayList<>();

        // Handle PlayerPoint click
        if (menuPoint.x != 0 && menuPoint.y != 0) {
            if (blastTutorial) {
                // Allow interaction to pop objects
                boolean currPop = false;
                IGameObject gobPop = new Obstacle(0, 0, 0, 0, 0);
                IGameObjectSpecial gobPopSpec = new ObstacleSpecialDouble(0, 0, 0, 0);

                // Game objects
                for (IGameObject gob : gameObjects) {
                    if (gob.pointInside(menuPoint)) {
                        currPop = true;
                        gob.pop();
                        gobPop = gob;
                    }
                }

                if (currPop) {
                    gobPop.pop();
                    calcScore(gobPop);
                    explosions.add(new ParticleExplosion((int) gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true));
                    gameObjects.remove(gobPop);
                    speed += 1;
                    //gameSounds.playSound("POP");
                    SoundManager.playSound("POP");
                    currPop = false;
                }

                // Special objects
                for (IGameObjectSpecial gob : gameSpecialObjects) {
                    if (gob.pointInside(menuPoint)) {
                        currPop = true;
                        gob.pop();
                        gobPopSpec = gob;
                    }
                }

                if (currPop) {
                    gobPopSpec.pop();
                    if (gobPopSpec.getType().equals("SpecialSpike")) {
                        score += 5;
                        //gameSounds.playSound("SPIKE");
                        SoundManager.playSound("SPIKE");
                    }
                    explosions.add(new ParticleExplosion((int) gobPopSpec.getSize()/partCount, gobPopSpec.getSize(), gobPopSpec.getCenter(), gobPopSpec.getType(), true));
                    gameSpecialObjects.remove(gobPopSpec);
                }
            }
        }

        if (!areaTutorial && !blastTutorial && !pictureTutorial && !laserTutorial) {
            //Staging menu before selection is chosen
            //Update obstacles
            for(IGameObject gob: gameObjects){
                // Grow object
                gob.grow(speed);
            }
        }

        // If objects current hits edge -> pop
        for(IGameObject gob: gameObjects){
            if(!gob.InGameArea()) {
                popped.add(gob);
                explosions.add(new ParticleExplosion((int) gob.getSize()/partCount, gob.getSize(), gob.getCenter(), gob.getType(), true));
                speed = 2;
                //gameSounds.playSound("POP");
                SoundManager.playSound("POP");
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

        if (!areaTutorial && !blastTutorial && !pictureTutorial && !laserTutorial) {
            // Create next object in queue
            SelectedObject = obstacleQueue.getItem();
            if (SelectedObject == null) {
                // Populate more items in queue
                for (int i = 0; i < 10; i++) {
                    obstacleQueue.addItem();
                }
                SelectedObject = obstacleQueue.getItem();
            }
            if (System.currentTimeMillis() - newItemTime >= 2000) {
                menuPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                //addingShape = true;
                IGameObject newObject = SelectedObject.NewInstance();
                newObject.update(menuPoint);
                gameObjects.add(newObject);
                obstacleQueue.removeItem();
                SelectedObject = obstacleQueue.getItem();
                newItemTime = System.currentTimeMillis();
            }
        }

        if (areaTutorial) {
            switch (iScreen) {
                case 1:
                    bShowNext = true;
                    gameObjects = new ArrayList<>();
                    gameSpecialObjects = new ArrayList<>();
                    break;
                case 2:
                    bShowNext = true;
                    break;
                case 3:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 1000)) && (iAnimation == 1)) {
                            obstacleManager = new ObstacleManager();
                            obstacleManager.addObstacle(100f,100f, Constants.SCREEN_WIDTH /5,Constants.SCREEN_HEIGHT/4,  Color.parseColor( Common.getPreferenceString("color_Obj") ));
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 2000)) && (iAnimation == 2)) {
                            obstacleManager.addObstacle(100f,100f, Constants.SCREEN_WIDTH * 2/5,Constants.SCREEN_HEIGHT/4,  Color.parseColor( Common.getPreferenceString("color_Obj") ));
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 3000)) && (iAnimation == 3)) {
                            obstacleManager.addObstacle(100f,100f, Constants.SCREEN_WIDTH * 3/5,Constants.SCREEN_HEIGHT/4,  Color.parseColor( Common.getPreferenceString("color_Obj") ));
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 4000)) && (iAnimation == 4)) {
                            obstacleManager.addObstacle(100f,100f, Constants.SCREEN_WIDTH * 4/5,Constants.SCREEN_HEIGHT/4,  Color.parseColor( Common.getPreferenceString("color_Obj") ));
                            bShowNext = true;
                        }
                    }
                    break;
                case 4:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > startTime) && (iAnimation == 1)) {
                            Constants.HEADER_HEIGHT = 200;
                            obstacleQueue = null;
                            obstacleQueue = new ObstacleQueue();
                            bHeader = true;
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 1000)) && (iAnimation == 2)) {
                            obstacleQueue.addItem("C");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 2000)) && (iAnimation == 3)) {
                            obstacleQueue.addItem("S");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 3000)) && (iAnimation == 4)) {
                            obstacleQueue.addItem("T");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 4000)) && (iAnimation == 5)) {
                            obstacleQueue.addItem("R");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 5000)) && (iAnimation == 6)) {
                            obstacleQueue.addItem("HI");
                            SelectedObject = obstacleQueue.getItem();
                            bShowNext = true;
                        }
                    }
                    break;
                case 5:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH/2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT * 3f / 4f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) && (System.currentTimeMillis() < (startTime + 3000))) {
                            if(gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                currentObject.grow(speed);
                                if (speed < 3) {
                                    speed += 1;
                                }
                                calcScore();
                            }
                        } else if (System.currentTimeMillis() > (startTime + 3000)) {
                            bShowNext = true;
                            iScreen += 1;
                            speed = 1;
                        }
                    }
                    break;
                case 6:
                    bShowNext = true;
                    break;
                case 7:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH * 4f / 5f;
                            menuPoint.y = Constants.SCREEN_HEIGHT / 2f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) ) {
                            if (gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                if (currentObject.getType().equals("Square")) {
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
                                    }
                                } else {
                                    bShowNext = true;
                                    speed = 1;
                                }
                                calcScore();
                            }
                        }
                    }
                    break;
                case 8:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH / 2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT / 2f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) ) {
                            if (gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                if (currentObject.getType().equals("TriangleUp")) {
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
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

                                        gobPop.pop();
                                        explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                                        gameObjects.remove(gobPop);
                                        //gameSounds.playSound("POP");
                                        SoundManager.playSound("POP");
                                        bShowNext = true;
                                        speed = 1;
                                    }
                                }
                                calcScore();
                            }
                        }
                    }
                    break;
                case 9:
                    bShowNext = true;
                    break;
                case 10:
                    break;
                default:
                    break;
            }
        }

        if (blastTutorial) {
            switch (iScreen) {
                case 1:
                    bShowNext = true;
                    gameObjects = new ArrayList<>();
                    gameSpecialObjects = new ArrayList<>();
                    break;
                case 2:
                    bShowNext = true;
                    break;
                case 3:
                    if (!bShowNext) {
                        bShowNext = true;
                        bHeader = true;
                        gameTimer.startTimer();
                    } else {
                        if ((System.currentTimeMillis() > (startTime + 1000))) {
                            obstacleQueue.addItem();
                            SelectedObject = obstacleQueue.getItem();
                            menuPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            startTime = System.currentTimeMillis();
                        }
                    }
                    break;
                case 4:
                    bShowNext = true;
                    if ((System.currentTimeMillis() > (startTime + 1000))) {
                        obstacleQueue.addItem();
                        SelectedObject = obstacleQueue.getItem();
                        menuPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                        IGameObject newObject = SelectedObject.NewInstance();
                        newObject.update(menuPoint);
                        gameObjects.add(newObject);
                        obstacleQueue.removeItem();
                        startTime = System.currentTimeMillis();
                    }
                    break;
                case 5:
                    bShowNext = true;
                    if ((System.currentTimeMillis() > (startTime + 1000))) {
                        obstacleQueue.addItem();
                        SelectedObject = obstacleQueue.getItem();
                        menuPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                        IGameObject newObject = SelectedObject.NewInstance();
                        newObject.update(menuPoint);
                        gameObjects.add(newObject);
                        obstacleQueue.removeItem();
                        startTime = System.currentTimeMillis();
                    }
                    break;
                case 6:
                    bShowNext = true;
                    break;
                case 7:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 1000)) && (iAnimation == 1)) {
                            gameSpecialObjects.add(new ObstacleSpecialDouble(Constants.SCREEN_WIDTH/2f, bFirstY + 100f,85f,Color.WHITE));
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 2000)) && (iAnimation == 2)) {
                            gameSpecialObjects.add(new ObstacleSpecialTime(Constants.SCREEN_WIDTH/2f, bFirstY + 300f,85f,Color.WHITE));
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 3000)) && (iAnimation == 3)) {
                            gameSpecialObjects.add(new ObstacleSpecialFrenzy(Constants.SCREEN_WIDTH/2f, bFirstY + 500f,85f,Color.WHITE));
                            iAnimation += 1;
                            bAnimation = true;
                            bShowNext = true;
                        }
                    }
                    break;
                case 8:
                    if (!bShowNext) {
                        gameSpecialObjects = new ArrayList<>();
                        bAnimation = true;
                        bShowNext = true;
                    } else {
                        if ((System.currentTimeMillis() > (startTime + 1000) )) {
                            IGameObjectSpecial special = ObstacleSpecialSpike.GetInstance();
                            menuPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                            special.update(menuPoint);
                            gameSpecialObjects.add(special);
                            startTime = System.currentTimeMillis();
                        }
                    }
                    break;
                case 9:
                    bAnimation = true;
                    bShowNext = true;
                    break;
                case 10:
                    bAnimation = true;
                    bShowNext = true;
                    break;
                case 11:
                    bAnimation = true;
                    bHeader = false;
                    if ((System.currentTimeMillis() > (startTime + 1000))) {
                        obstacleQueue.addItem();
                        SelectedObject = obstacleQueue.getItem();
                        menuPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
                        IGameObject newObject = SelectedObject.NewInstance();
                        newObject.update(menuPoint);
                        gameObjects.add(newObject);
                        obstacleQueue.removeItem();
                        startTime = System.currentTimeMillis();
                    }
                    break;
                default:
                    break;
            }

            for(IGameObject gob: gameObjects){
                // Grow object
                gob.grow(speed);
            }

            if (bAnimation) {
                for(IGameObjectSpecial gob: gameSpecialObjects) {
                    gob.update(new PointF(0,0));
                }
            }

            // CountDownTimer
            if (bHeader) {
                gameTimer.update();
            }
        }

        if (pictureTutorial) {
            switch (iScreen) {
                case 1:
                    String sURL = Common.getPreferenceString("pictureURL");
                    bg = new Background(sURL,0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
                    isLayerRefresh = true;
                    bShowNext = true;
                    gameObjects = new ArrayList<>();
                    break;
                case 2:
                    bShowNext = true;
                    break;
                case 3:
                    bShowNext = true;
                    break;
                case 4:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > startTime) && (iAnimation == 1)) {
                            Constants.HEADER_HEIGHT = 200;
                            obstacleQueue = null;
                            obstacleQueue = new ObstacleQueue();
                            bHeader = true;
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 1000)) && (iAnimation == 2)) {
                            obstacleQueue.addItem("C");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 2000)) && (iAnimation == 3)) {
                            obstacleQueue.addItem("S");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 3000)) && (iAnimation == 4)) {
                            obstacleQueue.addItem("T");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 4000)) && (iAnimation == 5)) {
                            obstacleQueue.addItem("R");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 5000)) && (iAnimation == 6)) {
                            obstacleQueue.addItem("HICSTRHI");
                            SelectedObject = obstacleQueue.getItem();
                            bShowNext = true;
                        }
                    }
                    break;
                case 5:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH/2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT * 3f / 4f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            addingShape = true;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) && (System.currentTimeMillis() < (startTime + 4000))) {
                            if(gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                currentObject.grow(speed);
                                if (speed < 3) {
                                    speed += 1;
                                }
                                calcScore();
                            }
                        } else if (System.currentTimeMillis() > (startTime + 4000)) {
                            isLayerRefresh = true;
                            bShowNext = true;
                            iScreen += 1;
                            speed = 1;
                            addingShape = false;
                        }
                    }
                    break;
                case 6:
                    bShowNext = true;
                    break;
                case 7:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH * 4f / 5f;
                            menuPoint.y = Constants.SCREEN_HEIGHT / 4f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            addingShape = true;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) ) {
                            if (gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                if (currentObject.getType().equals("Square")) {
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
                                    }
                                } else {
                                    addingShape = false;
                                    isLayerRefresh = true;
                                    bShowNext = true;
                                    speed = 1;
                                }
                                calcScore();
                            }
                        }
                    }
                    break;
                case 8:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH / 2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT / 2f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            addingShape = true;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) ) {
                            if (gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                if (currentObject.getType().equals("TriangleUp")) {
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
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
                                        SoundManager.playSound("POP");
                                        gobPop.pop();
                                        explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                                        gameObjects.remove(gobPop);
                                        SoundManager.playSound("POP");
                                        bShowNext = true;
                                        speed = 1;
                                        isLayerRefresh = true;
                                        addingShape = false;
                                    }
                                }
                                calcScore();
                            }
                        }
                    }
                    break;
                case 9:
                    break;
                default:
                    break;
            }
        }

        if (laserTutorial) {
            if (iScreen >= 5) {
                // Update Laser Object
                boolean currPop = false;
                IGameObject gobPop = new Obstacle(0, 0,0,0,0);
                for(IGameObject gob: gameObjects) {
                    if (CollisionManager.GameObjectSpecialCollide(gob,laserObject)) {
                        laserObject.Collide(gob, false);
                        laserObject.changeSpeedValue(0.25f);
                        currPop = true;
                        gobPop = gob;
                        gob.pop();
                    }
                }
                if (currPop) {
                    calcScore(gobPop);
                    explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                    gameObjects.remove(gobPop);
                    SoundManager.playSound("POP");
                }

                laserObject.update();
            }
            switch (iScreen) {
                case 1:
                    bShowNext = true;
                    gameObjects = new ArrayList<>();
                    break;
                case 2:
                    bShowNext = true;
                    break;
                case 3:
                    laserObject = new ObstacleSpecialLaser(Constants.SCREEN_WIDTH/2f,255f, 25f, Color.YELLOW);
                    bShowNext = true;
                    break;
                case 4:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > startTime) && (iAnimation == 1)) {
                            Constants.HEADER_HEIGHT = 200;
                            obstacleQueue = null;
                            obstacleQueue = new ObstacleQueue();
                            bHeader = true;
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 1000)) && (iAnimation == 2)) {
                            obstacleQueue.addItem("E");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 2000)) && (iAnimation == 3)) {
                            obstacleQueue.addItem("C");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 3000)) && (iAnimation == 4)) {
                            obstacleQueue.addItem("S");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 4000)) && (iAnimation == 5)) {
                            obstacleQueue.addItem("T");
                            iAnimation += 1;
                        } else if ((System.currentTimeMillis() > (startTime + 5000)) && (iAnimation == 6)) {
                            obstacleQueue.addItem("RHICSTRH");
                            SelectedObject = obstacleQueue.getItem();
                            bShowNext = true;
                        }
                    }
                    break;
                case 5:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH/2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT * 1f / 2f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if (iAnimation == 2) {
                            if (System.currentTimeMillis() < (startTime + 1000)) {
                                if(gameObjects.size() > 0) {
                                    IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
                                    }
                                    calcScore();
                                }
                            } else {
                                iAnimation += 1;
                            }
                        } else if (iAnimation == 3) {
                            menuPoint.x = Constants.SCREEN_WIDTH/2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT * 3f / 4f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if (iAnimation == 4) {
                            if (System.currentTimeMillis() < (startTime + 4000)) {
                                if(gameObjects.size() > 0) {
                                    IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
                                    }
                                    calcScore();
                                }
                            } else {
                                iAnimation += 1;
                            }
                        } else if (iAnimation == 5) {
                            bShowNext = true;
                            iScreen += 1;
                            speed = 1;
                        }
                    }
                    break;
                case 6:
                    bShowNext = true;
                    break;
                case 7:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH * 4f / 5f;
                            menuPoint.y = Constants.SCREEN_HEIGHT / 2f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) ) {
                            if (gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                if (currentObject.getType().equals("Square")) {
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
                                    }
                                } else {
                                    bShowNext = true;
                                    speed = 1;
                                }
                                calcScore();
                            } else {
                                bShowNext = true;
                            }
                        }
                    }
                    break;
                case 8:
                    if (!bShowNext) {
                        if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 1)) {
                            menuPoint.x = Constants.SCREEN_WIDTH / 2f;
                            menuPoint.y = Constants.SCREEN_HEIGHT / 2f;
                            IGameObject newObject = SelectedObject.NewInstance();
                            newObject.update(menuPoint);
                            gameObjects.add(newObject);
                            obstacleQueue.removeItem();
                            SelectedObject = obstacleQueue.getItem();
                            speed = 1;
                            iAnimation += 1;
                            return;
                        } else if ((System.currentTimeMillis() > (startTime + 500)) ) {
                            if (gameObjects.size() > 0) {
                                IGameObject currentObject = gameObjects.get(gameObjects.size() - 1);
                                if (currentObject.getType().equals("TriangleUp")) {
                                    currentObject.grow(speed);
                                    if (speed < 3) {
                                        speed += 1;
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

                                        gobPop.pop();
                                        explosions.add(new ParticleExplosion( (int)gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true ));
                                        gameObjects.remove(gobPop);
                                        //gameSounds.playSound("POP");
                                        SoundManager.playSound("POP");
                                        bShowNext = true;
                                        speed = 1;
                                    }
                                }
                                calcScore();
                            } else {
                                bShowNext = true;
                            }
                        }
                    }
                    break;
                case 9:
                    bShowNext = true;
                    break;
                case 10:
                    break;
                default:
                    break;
            }
        }

        menuPoint = new PointF(0,0);
    }
    private void calcScore() {
        float grayArea = (Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT)) - obstacleManager.getArea();
        float coveredArea = 0.0f;
        for (IGameObject gob : gameObjects) {
            coveredArea += gob.getArea();
        }
        score = Math.round((coveredArea / grayArea) * 100.0f);
    }
    private void calcScore(IGameObject gob) {
        float grayArea = (Constants.SCREEN_WIDTH * Constants.SCREEN_HEIGHT );
        float coveredArea = gob.getArea();
        float add = (coveredArea / grayArea) * 100f;

        // Special objects
        // if object over 10% screen size, spawn special with 30% chance
        if (bAnimation){
            if (add > 5) {
                float luck = Common.randomFlt(1.0f,100.0f);
                if (luck > 0 && luck <= 10) {
                    IGameObjectSpecial special = ObstacleSpecialDouble.GetInstance();
                    special.update(gob.getCenter());
                    gameSpecialObjects.add(special);
                }
                if (luck > 10 && luck <= 20) {
                    IGameObjectSpecial special = ObstacleSpecialFrenzy.GetInstance();
                    special.update(gob.getCenter());
                    gameSpecialObjects.add(special);
                }
                if (luck > 20 && luck <= 30) {
                    IGameObjectSpecial special = ObstacleSpecialTime.GetInstance();
                    special.update(gob.getCenter());
                    gameSpecialObjects.add(special);
                }
            }
            // if object less than 5% screen size, spawn spike with 50% chance
            if (add <= 5) {
                float luck = Common.randomFlt(1.0f,100.0f);
                if (luck > 0 && luck <= 50) {
                    IGameObjectSpecial special = ObstacleSpecialSpike.GetInstance();
                    special.update(gob.getCenter());
                    gameSpecialObjects.add(special);
                }
            }
        }

        if (add < 1) {
            add = 1;
        }
        score += (int)add;
    }

    private void drawCenterText(Canvas canvas, int color, float size, int vHeight, String text, boolean outline) {
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
        if (outline) {
            txtPaint.setStyle(Paint.Style.STROKE);
            txtPaint.setStrokeWidth(5);
            txtPaint.setColor(Color.BLACK);
            canvas.drawText(text, x, y, txtPaint);
        }
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
