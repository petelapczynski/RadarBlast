package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 3/27/2018.
 */

public class ObstacleSquare implements IGameObject {
    private RectF square;
    private int color;
    private Point center;
    private float size;
    private String type;

    public ObstacleSquare(float rectHeight, int startX, int startY, int color) {
        this.size = rectHeight / 2.0f;
        this.center = new Point(startX, startY);
        this.square = new RectF(startX - rectHeight, startX + rectHeight, startX + rectHeight, startY-rectHeight);
        this.color = color;
        this.type = "Square";
    }

    @Override
    public void grow(float speed) {
        square.left -= speed;
        square.top -= speed;
        square.bottom += speed;
        square.right += speed;
        size += speed;
    }

    @Override
    public IGameObject NewInstance() { return GetInstance(); }

    @Override
    public boolean InGameArea() {
        return square.left >= 0 && square.right <= Constants.SCREEN_WIDTH && square.bottom <= Constants.SCREEN_HEIGHT && square.top >= Constants.HEADER_HEIGHT;
    }

    @Override
    public float getArea() {
        return (square.right - square.left) * (square.bottom - square.top);
    }

    public static IGameObject GetInstance() {
        return new ObstacleSquare(0,0,0, Color.RED);
    }

    @Override
    public Point getCenter() {
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
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        // Square
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(square, paint);
        // Border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawRect(square, paint);
    }

    @Override
    public void update() {}

    public void update(Point point) {
        center = point;
        square.set(point.x - size, point.y - size, point.x + size, point.y + size );
    }

    @Override
    public boolean obstacleCollideCircle(Point obCenter, float obSize) {
        // point (x,y) on the path of the circle is  x = r*sin(angle), y = r*cos(angle)
        // check bounding box collision first, then if inside create a list of points, every degree from 0 -> 360
        RectF cRect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        if ( cRect.left <= square.right && cRect.right >= square.left && cRect.top <= square.bottom && cRect.bottom >= square.top) {
            for(int i = 0; i <= 360; i++ ) {
                // convert angle to radian
                double a = Math.toRadians((double)i);
                float x = obCenter.x + (float)( obSize * Math.sin(a) );
                float y = obCenter.y + (float)( obSize * Math.cos(a) );
                // if point is inside rect
                if( square.contains(x,y) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean obstacleCollideSquare(Point obCenter, float obSize) {
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        if ( rect.left <= square.right && rect.right >= square.left && rect.top <= square.bottom && rect.bottom >= square.top) {
            return true;
        }
        return false;
    }

    @Override
    public boolean obstacleCollideTriangle(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF tRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        // First check triangle points and bottom center
        if (square.contains(obCenter.x, tRect.top) ) {return true;}
        if (square.contains(obCenter.x, tRect.bottom) ) {return true;}
        if (square.contains(tRect.left, tRect.bottom) ) {return true;}
        if (square.contains(tRect.right, tRect.bottom) ) {return true;}
        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( tRect.left <= square.right && tRect.right >= square.left && tRect.top <= square.bottom && tRect.bottom >= square.top) {
            float m;
            float b;
            float x;
            float y;

            //bottom
            if (tRect.bottom > center.y) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if (square.contains(x, y)) {return true;}
                }
            }
            //top left
            if (obCenter.x > center.x) {
                m = (tRect.bottom - tRect.top) / (tRect.left - obCenter.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {return true;}
                }
            }
            //top right
            if (obCenter.x < center.x) {
                m = (tRect.top - tRect.bottom) / (obCenter.x - tRect.right);
                b = tRect.top - obCenter.x * m;
                for (int i = obCenter.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {return true;}
                }
            }
        }
        return false;
    }

}
