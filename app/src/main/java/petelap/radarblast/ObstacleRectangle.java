package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.ArrayList;

public class ObstacleRectangle extends ObstacleShapes implements IGameObject {

    public ObstacleRectangle(float rectHeight, float startX, float startY, int color) {
        size = rectHeight / 2.0f;
        center = new PointF(startX, startY);
        rect = new RectF(startX - rectHeight, startY - rectHeight, startX + rectHeight, startY + rectHeight);
        this.color = color;
        type = "Rectangle";
        InGameArea = true;
        pop = false;

        // Rectangle
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (size+1)*4, this.color, Color.BLACK, Shader.TileMode.MIRROR));
        }
        // Border
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
    }

    @Override
    public void grow(float speed) {
        rect.left -= speed;
        rect.top -= (speed * 0.618f);
        rect.bottom += (speed * 0.618f);
        rect.right += speed;
        size += speed;
        InGameArea = rect.left >= 0 && rect.right <= Constants.SCREEN_WIDTH && rect.bottom <= Constants.SCREEN_HEIGHT && rect.top >= Constants.HEADER_HEIGHT;
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (size+1)*4, color, Color.BLACK, Shader.TileMode.MIRROR));
        }
    }

    @Override
    public void setPaint(Paint ovrPaint) {
        paint = ovrPaint;
    }

    @Override
    public IGameObject NewInstance() { return GetInstance(); }

    @Override
    public boolean InGameArea() {
        return InGameArea;
    }

    @Override
    public boolean pointInside(PointF point) {
        return rect.contains(point.x,point.y);
    }

    @Override
    public void pop() {
        pop = true;
        InGameArea = false;
    }

    @Override
    public float getArea() {
        return (rect.right - rect.left) * (rect.bottom - rect.top);
    }

    public static IGameObject GetInstance() {
        return new ObstacleRectangle(1,0,0, Color.parseColor(Common.getPreferenceString("color_Rectangle")));
    }

    @Override
    public PointF getCenter() {
        return center;
    }

    @Override
    public float getSize() {
        return size;
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

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
        canvas.drawRect(rect, bPaint);
    }

    @Override
    public void update(PointF point) {
        center = point;

        rect.set(point.x - size, point.y - (size * 0.618f), point.x + size, point.y + (size * 0.618f) );
        InGameArea = rect.left >= 0 && rect.right <= Constants.SCREEN_WIDTH && rect.bottom <= Constants.SCREEN_HEIGHT && rect.top >= Constants.HEADER_HEIGHT;
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (size+1)*4, color, Color.BLACK, Shader.TileMode.MIRROR));
        }
    }
}