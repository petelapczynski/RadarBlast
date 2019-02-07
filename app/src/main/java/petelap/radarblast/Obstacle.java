package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

public class Obstacle extends ObstacleShapes implements IGameObject {
    private float height;
    private float width;

    public Obstacle(float rectHeight, float rectWidth, float startX, float startY, int color) {
        height = rectHeight / 2.0f;
        width = rectWidth / 2.0f;
        center = new PointF(startX, startY);
        rect = new RectF( startX - width, startY - height, startX + width, startY + height);

        //screen bounds
        if (rect.left < 0) {
            rect.left = 0;
        }
        if (rect.right > Constants.SCREEN_WIDTH) {
            rect.right = Constants.SCREEN_WIDTH;
        }
        if (rect.top < Constants.HEADER_HEIGHT) {
            rect.top = Constants.HEADER_HEIGHT;
        }
        if (rect.bottom > Constants.SCREEN_HEIGHT) {
            rect.bottom = Constants.SCREEN_HEIGHT;
        }

        this.color = color;
        this.color = Color.parseColor( Common.getPreferenceString("color_Obj") );
        type = "Obstacle";

        // Rect
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        // Border
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
        canvas.drawRect(rect, bPaint);
    }

    @Override
    public void update(PointF point) {
        center = point;
    }

    @Override
    public void grow(float speed) {
        rect.left -= speed;
        rect.top -= speed;
        rect.bottom += speed;
        rect.right += speed;
        height += speed;
        width += speed;
    }

    @Override
    public void setPaint(Paint ovrPaint) {
        paint = ovrPaint;
    }

    @Override
    public IGameObject NewInstance() {
        return new Obstacle(0,0, 0,0, Color.rgb(0,128,0));
    }

    @Override
    public boolean InGameArea() {
        return true;
    }

    @Override
    public boolean pointInside(PointF point) {
        return rect.contains(point.x,point.y);
    }

    @Override
    public void pop() {}

    @Override
    public float getArea() {
        return (rect.left - rect.right) * (rect.bottom - rect.top);
    }

    @Override
    public PointF getCenter() {
        return center;
    }

    @Override
    public float getSize() {
        // rect size will be height/width ratio
        return height/width;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public RectF getBoundsRect() {
        return rect;
    }

    @Override
    public ArrayList<PointF> getPoints() {
        ArrayList<PointF> points = new ArrayList<>();
        points.add(new PointF(rect.left,rect.top));
        points.add(new PointF(rect.right,rect.top));
        points.add(new PointF(rect.right,rect.bottom));
        points.add(new PointF(rect.left,rect.bottom));
        return points;
    }
}
