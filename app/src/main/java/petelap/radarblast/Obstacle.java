package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 3/23/2018.
 */

public class Obstacle implements IGameObject {
    private RectF rectangle;
    private int color;
    private Point center;
    private float size;
    private String type;

    public Obstacle(float rectHeight, int startX, int startY, int color) {
        this.size = rectHeight / 2.0f;
        this.center = new Point(startX, startY);
        this.rectangle = new RectF( startX - size, startY - size, startX + size, startY + size);
        this.color = color;
        this.type = "Obstacle";
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        // Rect
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
        // Border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rectangle, paint);
    }

    @Override
    public void update(Point point) {
        center = point;
    }

    @Override
    public void grow(float speed) {
        rectangle.left -= speed;
        rectangle.top -= speed;
        rectangle.bottom += speed;
        rectangle.right += speed;
        size += speed;
    }

    @Override
    public IGameObject NewInstance() {
        return new Obstacle(0,0,0, Color.GREEN);
    }

    @Override
    public boolean InGameArea() {
        return true;
    }

    @Override
    public boolean pointInside(Point point) {
        if(point.x >= rectangle.left && point.x <= rectangle.right && point.y >= rectangle.top && point.y <= rectangle.bottom) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void pop() {}

    @Override
    public float getArea() {
        return (rectangle.left - rectangle.right) * (rectangle.bottom - rectangle.top);
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
    public boolean CollideCircle(Point obCenter, float obSize) {
        // point (x,y) on the path of the circle is  x = r*sin(angle), y = r*cos(angle)
        // check bounding box collision first, then if inside create a list of points, every degree from 0 -> 360
        RectF cRect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        if ( cRect.left <= rectangle.right && cRect.right >= rectangle.left && cRect.top <= rectangle.bottom && cRect.bottom >= rectangle.top) {
            for(int i = 0; i <= 360; i++ ) {
                // convert angle to radian
                double a = Math.toRadians((double)i);
                float x = obCenter.x + (float)( obSize * Math.sin(a) );
                float y = obCenter.y + (float)( obSize * Math.cos(a) );
                // if point is inside rect
                if( rectangle.contains(x,y) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean CollideSquare(Point obCenter, float obSize) {
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        return rect.left <= rectangle.right && rect.right >= rectangle.left && rect.top <= rectangle.bottom && rect.bottom >= rectangle.top;
    }

    @Override
    public boolean CollideTriangleUp(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF tRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        // First check triangle points and center
        if (rectangle.contains(obCenter.x, tRect.top) ) {
            return true;
        }
        if (rectangle.contains(obCenter.x, tRect.bottom) ) {
            return true;
        }
        if (rectangle.contains(tRect.left, tRect.bottom) ) {
            return true;
        }
        if (rectangle.contains(tRect.right, tRect.bottom) ) {
            return true;
        }
        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( tRect.left <= rectangle.right && tRect.right >= rectangle.left && tRect.top <= rectangle.bottom && tRect.bottom >= rectangle.top) {
            float m;
            float b;
            float x;
            float y;

            //bottom
            if(obCenter.y <= rectangle.top) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top left
            if (obCenter.x >= rectangle.left) {
                m = (tRect.bottom - tRect.top) / (tRect.left - obCenter.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x <= rectangle.right) {
                m = (tRect.top - tRect.bottom) / (obCenter.x - tRect.right);
                b = tRect.top - obCenter.x * m;
                for (int i = obCenter.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean CollideTriangleDown(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF tRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        // First check triangle points and center
        if (rectangle.contains(obCenter.x, tRect.top) ) {
            return true;
        }
        if (rectangle.contains(obCenter.x, tRect.bottom) ) {
            return true;
        }
        if (rectangle.contains(tRect.left, tRect.top) ) {
            return true;
        }
        if (rectangle.contains(tRect.right, tRect.top) ) {
            return true;
        }
        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( tRect.left <= rectangle.right && tRect.right >= rectangle.left && tRect.top <= rectangle.bottom && tRect.bottom >= rectangle.top) {
            float m;
            float b;
            float x;
            float y;

            //Top
            if(obCenter.y >= rectangle.bottom) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.top;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x >= rectangle.left) {
                m = (tRect.top - tRect.bottom) / (tRect.left - obCenter.x);
                b = tRect.top - tRect.left * m;
                for (int i = (int) tRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= rectangle.right) {
                m = (tRect.bottom - tRect.top) / (obCenter.x - tRect.right);
                b = tRect.bottom - obCenter.x * m;
                for (int i = obCenter.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean CollideRhombus(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        //float height = width * 1.732f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f))) * 2.0f;

        RectF rRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        // First check triangle points and center
        if (rectangle.contains(obCenter.x, rRect.top) ) {
            return true;
        }
        if (rectangle.contains(obCenter.x, rRect.bottom) ) {
            return true;
        }
        if (rectangle.contains(rRect.left, obCenter.y) ) {
            return true;
        }
        if (rectangle.contains(rRect.right, obCenter.y) ) {
            return true;
        }
        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( rRect.left <= rectangle.right && rRect.right >= rectangle.left && rRect.top <= rectangle.bottom && rRect.bottom >= rectangle.top) {
            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x >= rectangle.left && obCenter.y >= center.y) {
                m = (obCenter.y - rRect.top) / (rRect.left - obCenter.x);
                b = obCenter.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x <= rectangle.right && obCenter.y >= center.y) {
                m = (rRect.top - obCenter.y) / (obCenter.x - rRect.right);
                b = rRect.top - obCenter.x * m;
                for (int i = obCenter.x; i <= rRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x >= rectangle.left && obCenter.y <= center.y) {
                m = (obCenter.y - rRect.bottom) / (rRect.left - obCenter.x);
                b = obCenter.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= rectangle.right && obCenter.y <= center.y) {
                m = (rRect.bottom - obCenter.y) / (obCenter.x - rRect.right);
                b = rRect.bottom - obCenter.x * m;
                for (int i = obCenter.x; i <= rRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (rectangle.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
