package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.ArrayList;

public class ObstacleCircle extends ObstacleShapes implements IGameObject {
    private float radius;

    public ObstacleCircle(int startX, int startY, float radius, int color) {
        this.radius = radius;
        this.center = new PointF(startX, startY);
        rect = new RectF(startX - radius, startY - radius, startX + radius, startY + radius);
        this.color = color;
        this.color = Color.parseColor( Common.getPreferenceString("color_Circle") );
        InGameArea = true;
        pop = false;
        type = "Circle";

        // Circle
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (radius + 1)*4, this.color, Color.BLACK, Shader.TileMode.MIRROR));
            paint.setAntiAlias(true);
        }

        // Border
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        bPaint.setAntiAlias(true);
    }

    @Override
    public void grow(float speed) {
        radius += speed;
        InGameArea = center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
        rect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (radius + 1)*4, color, Color.BLACK, Shader.TileMode.MIRROR));

        }
    }

    @Override
    public void setPaint(Paint ovrPaint) {
        paint = ovrPaint;
    }

    @Override
    public IGameObject NewInstance() {
        return GetInstance();
    }

    @Override
    public boolean InGameArea() {
        return InGameArea;
    }

    @Override
    public boolean pointInside(PointF point) {
        return ((point.x - center.x) * (point.x - center.x) + (point.y - center.y) * (point.y - center.y) <= radius * radius);
    }

    @Override
    public void pop() {
        pop = true;
        InGameArea = false;
    }

    @Override
    public float getArea() {
        return ((float)Math.PI * radius * radius);
    }

    public static IGameObject GetInstance() {
        return new ObstacleCircle(0,0,1, Color.BLUE);
    }

    @Override
    public PointF getCenter() {
        return center;
    }

    @Override
    public float getSize() {
        return radius;
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
        points.add(new PointF(rect.left,center.y));
        points.add(new PointF(center.x,rect.top));
        points.add(new PointF(rect.right,center.y));
        points.add(new PointF(center.x,rect.bottom));
        return points;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, radius, paint);
        canvas.drawCircle(center.x, center.y, radius, bPaint);
    }

    @Override
    public void update(PointF point) {
        center = point;

        InGameArea = center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
        rect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (radius + 1)*4, color, Color.BLACK, Shader.TileMode.MIRROR));
        }
    }
}
