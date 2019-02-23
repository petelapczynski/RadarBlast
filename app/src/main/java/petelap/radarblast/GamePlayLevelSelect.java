package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

/*** Level Select - used by Area and Laser***/

public class GamePlayLevelSelect extends GamePlayBase implements IScene {
    private boolean bSelectedLevel;
    private boolean bMoving;
    private int iLevel;
    private long startTime;
    private ArrayList<RectF> gameLevelButtons;
    private ArrayList<Integer> gameLevelIntegers;
    private float scroll;
    private int iAnimation;
    private RectF bMenu = new RectF(Constants.BTN_LEFT, Constants.SCREEN_HEIGHT - (Constants.BTN_HEIGHT * 5), Constants.BTN_RIGHT, Constants.SCREEN_HEIGHT - (Constants.BTN_HEIGHT * 2));
    private String gameMode;

    public GamePlayLevelSelect(String GameMode) {
        gameMode = GameMode;
        playerPoint = new PointF(0, 0);
        movePoint = new PointF(0, 0);
        startTime = System.currentTimeMillis();
        bSelectedLevel = false;
        iLevel = -1;
        scroll = 0;
        speed = 0;
        iAnimation = 0;
        explosions = new ArrayList<>();

        //Button paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        //Border paint
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        bPaint.setAntiAlias(true);

        //Background
        bg = new Background(0,Constants.HEADER_HEIGHT,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);

        overlayPaint = new Paint();
        overlayPaint.setDither(true);
        overlayPaint.setAntiAlias(true);
        overlayPaint.setARGB(75,0,0,0);

        //Level Buttons
        gameLevelButtons = new ArrayList<>();
        gameLevelIntegers = new ArrayList<>();
        float bLeft = Constants.SCREEN_WIDTH * 0.25f;
        float bMid = Constants.SCREEN_WIDTH * 0.5f;
        float bRight = Constants.SCREEN_WIDTH * 0.75f;
        int c = 1;
        float y = gameStartBack.top + (Constants.BTN_HEIGHT * 3);
        for (Levels.Level lvl: SceneManager.levels.getLevels()) {
            switch (c) {
                case 1:
                    gameLevelButtons.add(new RectF(bLeft - (Constants.BTN_HEIGHT * 2), y - (Constants.BTN_HEIGHT * 2), bLeft + (Constants.BTN_HEIGHT * 2), y + (Constants.BTN_HEIGHT * 2)));
                    break;
                case 2:
                    gameLevelButtons.add(new RectF(bMid - (Constants.BTN_HEIGHT * 2), y - (Constants.BTN_HEIGHT * 2), bMid + (Constants.BTN_HEIGHT * 2), y + (Constants.BTN_HEIGHT * 2)));
                    break;
                case 3:
                    gameLevelButtons.add(new RectF(bRight - (Constants.BTN_HEIGHT * 2), y - (Constants.BTN_HEIGHT * 2), bRight + (Constants.BTN_HEIGHT * 2), y + (Constants.BTN_HEIGHT * 2)));
                    break;
            }
            gameLevelIntegers.add( lvl.getNumber() );
            c++;
            if (c > 3) {
                c = 1;
                y += (Constants.BTN_HEIGHT * 5);
            }
        }
    }

    @Override
    public void terminate() {
        //Constants.SOUND_MANAGER.stop();
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        if (bSelectedLevel) {
            //no user input allowed
            return;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                bMoving = false;
                playerPoint = new PointF( event.getX(), event.getY() );
                if (gameStartBack.contains(playerPoint.x, playerPoint.y)) {
                    for (int i = 0; i < gameLevelButtons.size(); i++) {
                        if (gameLevelButtons.get(i).contains(playerPoint.x, playerPoint.y) && (gameLevelButtons.get(i).top > gameStartBack.top && gameLevelButtons.get(i).bottom < gameStartBack.bottom)) {
                            // Level circle selected
                            if (gameLevelButtons.get(i).top >= gameStartBack.top || gameLevelButtons.get(i).bottom <= gameStartBack.bottom) {
                                // Level circle inside box
                                iLevel = gameLevelIntegers.get(i);
                                return;
                            }
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                bMoving = true;
                movePoint = new PointF( 0, event.getY() );
                scroll = (movePoint.y - playerPoint.y);
                playerPoint = new PointF( 0, event.getY() );
                break;

            case MotionEvent.ACTION_UP:
                playerPoint = new PointF( event.getX(), event.getY() );
                scroll = 0;
                bMoving = false;
                //Level Buttons
                if (!bSelectedLevel) {
                    if (gameStartBack.contains(playerPoint.x, playerPoint.y)) {
                        for (int i = 0; i < gameLevelButtons.size(); i++) {
                            if (gameLevelButtons.get(i).contains(playerPoint.x, playerPoint.y) && (gameLevelButtons.get(i).top > gameStartBack.top && gameLevelButtons.get(i).bottom < gameStartBack.bottom)) {
                                // Level circle selected
                                if (gameLevelButtons.get(i).top >= gameStartBack.top || gameLevelButtons.get(i).bottom <= gameStartBack.bottom) {
                                    // Level circle inside box
                                    // Does ACTION_DOWN match ACTION_UP circle
                                    if (iLevel == gameLevelIntegers.get(i)) {
                                        bSelectedLevel = true;
                                        startTime = System.currentTimeMillis();
                                        iAnimation = 1;
                                        return;
                                    }
                                }
                            }
                        }
                    }

                    //Button: Exit to Main
                    if (bMenu.contains(playerPoint.x, playerPoint.y)) {
                        SceneManager.changeScene("MENU");
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Canvas
        bg.draw(canvas);

        // Display Level Buttons
        canvas.drawRect(gameStartBack, paint);
        canvas.drawRect(gameOverlay, overlayPaint);
        drawCenterText(canvas, Color.WHITE, 255,Constants.TXT_MD, (int)(Constants.BTN_HEIGHT * 4),Constants.SCREEN_WIDTH/2,"Level Selection");
//        drawCenterText(canvas, Color.WHITE, 255,100, 200,Constants.SCREEN_WIDTH/2,"Level Selection");

        canvas.drawRoundRect( bMenu, 25,25, paint);
        canvas.drawRoundRect( bMenu, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, 255,Constants.TXT_SM, (int)bMenu.centerY(), (int)bMenu.centerX(),"Start Level");
//        drawCenterText(canvas, Color.WHITE, 255,70, (int)bMenu.centerY(), (int)bMenu.centerX(),"Start Level");

        // Levels
        for(int i = 0; i < gameLevelButtons.size(); i++){
            if(gameLevelButtons.get(i).top > gameStartBack.top && gameLevelButtons.get(i).bottom < gameStartBack.bottom) {
                if (!bSelectedLevel || (i != iLevel) || (iAnimation != 3)) {
                    canvas.drawCircle(gameLevelButtons.get(i).centerX(), gameLevelButtons.get(i).centerY(),(gameLevelButtons.get(i).right - gameLevelButtons.get(i).left)/2, paint);
                    canvas.drawCircle(gameLevelButtons.get(i).centerX(), gameLevelButtons.get(i).centerY(),(gameLevelButtons.get(i).right - gameLevelButtons.get(i).left)/2, bPaint);
                    drawCenterText(canvas,Color.WHITE, 255, Constants.TXT_SM, (int)gameLevelButtons.get(i).centerY(),(int)gameLevelButtons.get(i).centerX(),gameLevelIntegers.get(i).toString());
//                    drawCenterText(canvas,Color.WHITE, 255, 70, (int)gameLevelButtons.get(i).centerY(),(int)gameLevelButtons.get(i).centerX(),gameLevelIntegers.get(i).toString());
                }
            } else if ((gameLevelButtons.get(i).top <= gameStartBack.top && gameLevelButtons.get(i).centerY() >= gameStartBack.top )  || (gameLevelButtons.get(i).bottom >= gameStartBack.bottom && gameLevelButtons.get(i).centerY() <= gameStartBack.bottom)) {
                //Button paint
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.GRAY);
                p.setAntiAlias(true);
                //Border paint
                Paint b = new Paint();
                b.setStyle(Paint.Style.STROKE);
                b.setStrokeWidth(3);
                b.setColor(Color.BLACK);
                b.setAntiAlias(true);
                // Alpha from 1-0 middle to half out of gameStartBack
                int alpha = 255;
                if (gameLevelButtons.get(i).top <= gameStartBack.top) {
                    alpha = 255 * (int)(gameLevelButtons.get(i).centerY() - gameStartBack.top ) / (int)(gameLevelButtons.get(i).centerY() - gameLevelButtons.get(i).top);
                }
                if (gameLevelButtons.get(i).bottom >= gameStartBack.bottom) {
                    alpha = 255 * (int)(gameStartBack.bottom - gameLevelButtons.get(i).centerY()) / (int)(gameLevelButtons.get(i).centerY() - gameLevelButtons.get(i).top);
                }
                p.setAlpha(alpha);
                b.setAlpha(alpha);

                canvas.drawCircle(gameLevelButtons.get(i).centerX(), gameLevelButtons.get(i).centerY(),(gameLevelButtons.get(i).right - gameLevelButtons.get(i).left)/2, p);
                canvas.drawCircle(gameLevelButtons.get(i).centerX(), gameLevelButtons.get(i).centerY(),(gameLevelButtons.get(i).right - gameLevelButtons.get(i).left)/2, b);
                drawCenterText(canvas,Color.WHITE, alpha,Constants.TXT_SM, (int)gameLevelButtons.get(i).centerY(),(int)gameLevelButtons.get(i).centerX(),gameLevelIntegers.get(i).toString());
//                drawCenterText(canvas,Color.WHITE, alpha,70, (int)gameLevelButtons.get(i).centerY(),(int)gameLevelButtons.get(i).centerX(),gameLevelIntegers.get(i).toString());
            }
        }

        //Explosions
        for(ParticleExplosion pe : explosions) {
            if (pe.getState() == 0) { pe.draw(canvas); }
        }

        //Button: Back to Menu
        canvas.drawRoundRect( bMenu, 25,25, paint);
        canvas.drawRoundRect( bMenu, 25,25, bPaint);
        drawCenterText(canvas, Color.WHITE, 255, Constants.TXT_SM, (int)bMenu.centerY(),(int)bMenu.centerX(), "Back to Menu");
//        drawCenterText(canvas, Color.WHITE, 255, 70, (int)bMenu.centerY(),(int)bMenu.centerX(), "Back to Menu");
    }

    @Override
    public void update() {
        // Selected level animation
        if(bSelectedLevel) {
            if (iAnimation == 1) {
                speed += 1;
                gameLevelButtons.get(iLevel).left = gameLevelButtons.get(iLevel).left - speed;
                gameLevelButtons.get(iLevel).right = gameLevelButtons.get(iLevel).right + speed;
                if (System.currentTimeMillis() > (startTime + 500)) {
                    iAnimation ++;
                }
            } else if ((System.currentTimeMillis() > (startTime + 500)) && (iAnimation == 2)) {
                iAnimation ++;
                explosions.add(new ParticleExplosion( 20,gameLevelButtons.get(iLevel).right - gameLevelButtons.get(iLevel).centerX(), new PointF(gameLevelButtons.get(iLevel).centerX(),gameLevelButtons.get(iLevel).centerY()), "MenuButton", true ));
            } else if ((System.currentTimeMillis() > (startTime + 2000)) && (iAnimation == 3)) {
                SceneManager.changeLevel(iLevel, false);
                SceneManager.changeScene(gameMode);
            }
            //Explosions
            for(ParticleExplosion pe : explosions) {
                pe.update();
            }
        }
        // Touch scrolling
        if(bMoving){
            if(((gameLevelButtons.get(0).top + scroll) <= (gameStartBack.top + 50)) && ((gameLevelButtons.get(gameLevelButtons.size() - 1).bottom + scroll) >= (gameStartBack.bottom - 50))) {
                for(int i = 0; i < gameLevelButtons.size(); i++) {
                    gameLevelButtons.get(i).top = gameLevelButtons.get(i).top + scroll;
                    gameLevelButtons.get(i).bottom = gameLevelButtons.get(i).bottom + scroll;
                }
            }
        }
    }

    private void drawCenterText(Canvas canvas, int color, int alpha, float size, int vHeight, int vWidth, String text) {
        Rect r = new Rect();
        Paint txtPaint = new Paint();
        txtPaint.setTextAlign(Paint.Align.LEFT);
        txtPaint.setColor(color);
        txtPaint.setAlpha(alpha);
        txtPaint.setTextSize(size);
        canvas.getClipBounds(r);
        int cWidth = r.width();
        txtPaint.getTextBounds(text, 0, text.length(), r);
        float x = (cWidth / 2f - r.width() / 2f - r.left) - (Constants.SCREEN_WIDTH/2f) + vWidth ;
        float y = vHeight + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, txtPaint);
    }
}