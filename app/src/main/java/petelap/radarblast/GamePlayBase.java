package petelap.radarblast;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

public abstract class GamePlayBase {
    protected ArrayList<IGameObject> gameObjects;
    protected ArrayList<ParticleExplosion> explosions;
    protected PointF playerPoint;
    protected PointF movePoint;
    protected ObstacleQueue obstacleQueue;
    protected IGameObject SelectedObject;
    protected boolean gameStart;
    protected boolean addingShape;
    protected boolean addShape;
    protected boolean gameOver;
    protected boolean objectPop;
    protected boolean objectMoving;
    protected long gameOverTime;
    protected long objectTime;
    protected float score;
    protected boolean bHighScore;
    protected int speed;
    protected int partCount;

    protected Paint paint;
    protected Paint bPaint;
    protected Paint hPaint;
    protected Paint txtPaint;

    protected Background bg;
    protected Paint overlayPaint;
    protected RectF gameOverlay = new RectF(0 , 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    protected RectF gameStartBack = new RectF(Constants.SCREEN_WIDTH * 10f/100f, Constants.SCREEN_HEIGHT * 20f/100f, Constants.SCREEN_WIDTH * 90f/100f, Constants.SCREEN_HEIGHT * 80f/100f);

}
