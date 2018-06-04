package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 3/27/2018.
 */

public class ObstacleCircle implements IGameObject {
    private float radius;
    private int color;
    private Point center;
    private String type;

    public ObstacleCircle(int startX, int startY, float radius, int color) {
        this.radius = radius;
        this.center = new Point(startX, startY);
        this.color = color;
        type = "Circle";
    }

    @Override
    public void grow(float speed) {
        radius += speed;
    }

    @Override
    public IGameObject NewInstance() {
        return GetInstance();
    }

    @Override
    public boolean InGameArea() {
        return center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
    }

    @Override
    public float getArea() {
        return (float)(Math.PI * (radius * radius) );
    }

    public static IGameObject GetInstance() {
        return new ObstacleCircle(0,0,0, Color.BLUE);
    }

    @Override
    public Point getCenter() {
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
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        // Circle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(center.x, center.y, radius, paint);
        // Border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    @Override
    public void update() {}

    public void update(Point point) {
        center = point;
    }

    @Override
    public boolean obstacleCollideCircle(Point obCenter, float obSize) {
        // if distance <= sum(radius) circles touch
        float distSq = (obCenter.x - center.x) * (obCenter.x - center.x) + (obCenter.y - center.y) * (obCenter.y - center.y);
        float radSumSq = (obSize + radius) * (obSize + radius);
        if (distSq <= radSumSq) {
            return true;
        }
        return false;
    }

    @Override
    public boolean obstacleCollideSquare(Point obCenter, float obSize) {
        // point (x,y) on the path of the circle is  x = r*sin(angle), y = r*cos(angle)
        // check bounding box collision first, then if inside create a list of points, every degree from 0 -> 360
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        RectF cRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        if ( rect.left <= cRect.right && rect.right >= cRect.left && rect.top <= cRect.bottom && rect.bottom >= cRect.top) {
            for(int i = 0; i <= 360; i++ ) {
                // convert angle to radian
                double a = Math.toRadians((double)i);
                float x = center.x + (float)( radius * Math.sin(a) );
                float y = center.y + (float)( radius * Math.cos(a) );
                // if point is inside rect
                if( rect.contains(x,y) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean obstacleCollideTriangle(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF tRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        RectF cRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        float radSq = radius * radius;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( tRect.left <= cRect.right && tRect.right >= cRect.left && tRect.top <= cRect.bottom && tRect.bottom >= cRect.top) {
            // First check triangle points and bottom center
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (tRect.top - center.y)*(tRect.top - center.y) <= radSq ) {
                return true;
            }
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (tRect.bottom - center.y)*(tRect.bottom - center.y) <= radSq ) {
                return true;
            }
            if ( (tRect.left - center.x)*(tRect.left - center.x) + (tRect.bottom - center.y)*(tRect.bottom - center.y) <= radSq ) {
                return true;
            }
            if ( (tRect.right - center.x)*(tRect.right - center.x) + (tRect.bottom - center.y)*(tRect.bottom - center.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //bottom
            if (tRect.bottom > center.y ) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //top left
            if (obCenter.x > center.x) {
                m = (tRect.bottom - tRect.top) / (tRect.left - obCenter.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x < center.x) {
                m = (tRect.top - tRect.bottom) / (obCenter.x - tRect.right);
                b = tRect.top - obCenter.x * m;
                for (int i = obCenter.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
