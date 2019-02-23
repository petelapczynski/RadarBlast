package petelap.radarblast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.ArrayList;

/*** Main Menu ***/

public class GamePlayMenu extends GamePlayBase implements IScene  {
    private long newItemTime;
    private final float bFirstY = Constants.SCREEN_HEIGHT/4f;
    private final float bGap = Constants.BTN_HEIGHT * 0.7f;

    private RectF bArea = new RectF(Constants.BTN_LEFT, bFirstY, Constants.BTN_RIGHT, bFirstY + (Constants.BTN_HEIGHT * 3));
    private RectF bBlast = new RectF(Constants.BTN_LEFT, bArea.bottom + bGap, Constants.BTN_RIGHT, bArea.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bPicture = new RectF(Constants.BTN_LEFT, bBlast.bottom + bGap, Constants.BTN_RIGHT, bBlast.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bLaser = new RectF(Constants.BTN_LEFT, bPicture.bottom + bGap, Constants.BTN_RIGHT, bPicture.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bTutorial = new RectF(Constants.BTN_LEFT, bLaser.bottom + bGap, Constants.BTN_RIGHT, bLaser.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bOptions = new RectF(Constants.BTN_LEFT, bTutorial.bottom + bGap, Constants.BTN_RIGHT, bTutorial.bottom + (Constants.BTN_HEIGHT * 3) + bGap);
    private RectF bExit = new RectF(Constants.BTN_LEFT, bOptions.bottom + bGap, Constants.BTN_RIGHT, bOptions.bottom + (Constants.BTN_HEIGHT * 3) + bGap);

    public GamePlayMenu() {
        playerPoint = new PointF(0,0);
        gameObjects = new ArrayList<>();
        explosions = new ArrayList<>();
        obstacleQueue = new ObstacleQueue(10);
        SelectedObject = obstacleQueue.getItem();
        newItemTime = System.currentTimeMillis();
        partCount = Common.getPreferenceInteger("particles");
        speed = 1;

        //Button paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        //Border paint
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);

        //Background
        bg = new Background(0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        //Game Sounds
        SoundManager.setGameMusic("MENU");
        SoundManager.playMusic();
    }

    @Override
    public void terminate() {
        //Constants.SOUND_MANAGER.stop();
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                playerPoint.x = event.getX();
                playerPoint.y = event.getY();

                //Change Name
                if(playerPoint.y < (Constants.BTN_HEIGHT * 4f)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Constants.CONTEXT);
                    builder.setTitle("Welcome to Radar Blast!");
                    builder.setMessage("Enter your name or initials.");
                    builder.setCancelable(true);
                    final EditText input = new EditText(Constants.CONTEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String txt = input.getText().toString();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("username", txt.trim());
                            editor.apply();
                            dialogInterface.cancel();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Do nothing
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                //Button: Start Area - Level Select
                if( bArea.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("AREA_LEVELS");
                    return;
                }
                //Button: Start Blast
                if( bBlast.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("BLAST");
                    return;
                }
                //Button: Start Picture
                if( bPicture.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("PICTURE");
                    return;
                }
                //Button: Start Laser - Level Select
                if( bLaser.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("LASER_LEVELS");
                    return;
                }
                //Button: How to Play
                if( bTutorial.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("HOW_TO");
                    return;
                }
                //Button: Options
                if( bOptions.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("OPTIONS");
                    return;
                }
                //Button: Exit
                if( bExit.contains(event.getX(), event.getY()) ) {
                    SceneManager.changeScene("EXIT");
                    return;
                }

                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Canvas
        bg.draw(canvas);

        //Game Objects
        for(IGameObject obj : gameObjects){
            if ( obj.InGameArea() ) { obj.draw(canvas); }
        }

        //Explosions
        for(ParticleExplosion pe : explosions) {
           if (pe.getState() == 0) { pe.draw(canvas); }
        }

        // User Name
        if (!Common.getPreferenceString("username").equals("_") && Common.getPreferenceString("username").length() > 0) {
            drawCenterText(canvas, Color.WHITE, Constants.TXT_XS, (int)(Constants.BTN_HEIGHT * 2f), "Welcome " + Common.getPreferenceString("username"), false);
        }

        //Logo
        drawCenterText(canvas, Color.WHITE, Constants.TXT_LG, Constants.SCREEN_HEIGHT/4 - (int)(Constants.BTN_HEIGHT * 3), "Radar Blast", true);

        //Button: Start Area
        canvas.drawRoundRect( bArea, 25,25, paint);
        canvas.drawRoundRect( bArea, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bArea.centerY(), "Area Game", false);

        //Button: Start Blast
        canvas.drawRoundRect( bBlast, 25,25, paint);
        canvas.drawRoundRect( bBlast, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bBlast.centerY(), "Blast Game", false);

        //Button: Start Picture
        canvas.drawRoundRect( bPicture, 25,25, paint);
        canvas.drawRoundRect( bPicture, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bPicture.centerY(), "Picture Game", false);

        //Button: Start Laser
        canvas.drawRoundRect( bLaser, 25,25, paint);
        canvas.drawRoundRect( bLaser, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bLaser.centerY(), "Laser Game", false);

        //Button: How to Play
        canvas.drawRoundRect( bTutorial, 25,25, paint);
        canvas.drawRoundRect( bTutorial, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bTutorial.centerY(), "How to Play", false);

        //Button: Options
        canvas.drawRoundRect( bOptions, 25,25, paint);
        canvas.drawRoundRect( bOptions, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bOptions.centerY(), "Options", false);

        //Button: Exit
        canvas.drawRoundRect( bExit, 25,25, paint);
        canvas.drawRoundRect( bExit, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, Constants.TXT_SM,(int)bExit.centerY(), "Exit", false);
    }

    @Override
    public void update() {
        //IGameObject currentObject = new Obstacle(0,0,0,0);
        ArrayList<IGameObject> popped = new ArrayList<>();
        ArrayList<ParticleExplosion> expDone = new ArrayList<>();

        // Handle PlayerPoint click
        if (playerPoint.x != 0 && playerPoint.y != 0) {
            boolean currPop = false;
            IGameObject gobPop = new Obstacle(0, 0, 0, 0, 0);

            for (IGameObject gob : gameObjects) {
                if (gob.pointInside(playerPoint)) {
                    currPop = true;
                    gob.pop();
                    gobPop = gob;
                }
            }

            if (currPop) {
                gobPop.pop();
                explosions.add(new ParticleExplosion((int) gobPop.getSize()/partCount, gobPop.getSize(), gobPop.getCenter(), gobPop.getType(), true));
                gameObjects.remove(gobPop);
                speed += 1;
                SoundManager.playSound("POP");
            }
        }

        //Update obstacles
        for(IGameObject gob: gameObjects){
            // Grow object
            gob.grow(speed);
        }

        // If objects current hits edge -> pop
        for(IGameObject gob: gameObjects){
            if(!gob.InGameArea()) {
                popped.add(gob);
                explosions.add(new ParticleExplosion((int) gob.getSize()/partCount, gob.getSize(), gob.getCenter(), gob.getType(), true));
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

        // Create next object in queue
        SelectedObject = obstacleQueue.getItem();
        if(SelectedObject == null) {
            // Populate more items in queue
            for(int i=0; i<10; i++) {
                obstacleQueue.addItem();
            }
            SelectedObject = obstacleQueue.getItem();
        }
        if (System.currentTimeMillis() - newItemTime >= 2000) {
            playerPoint = new PointF(Common.randomFlt(250f, Constants.SCREEN_WIDTH - 250f), Common.randomFlt(250f, Constants.SCREEN_HEIGHT - 250f));
            //addingShape = true;
            IGameObject newObject = SelectedObject.NewInstance();
            newObject.update(playerPoint);
            gameObjects.add(newObject);
            obstacleQueue.removeItem();
            SelectedObject = obstacleQueue.getItem();
            newItemTime = System.currentTimeMillis();
        }
        playerPoint = new PointF(0,0);
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
}