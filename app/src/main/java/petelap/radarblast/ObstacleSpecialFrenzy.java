package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 3/27/2018.
 */

public class ObstacleSpecialFrenzy implements IGameObject {
    private float radius;
    private int color;
    private Point center;
    private String type;
    private boolean InGameArea;
    private boolean pop;

    public ObstacleSpecialFrenzy(int startX, int startY, float radius, int color) {
        this.radius = radius;
        this.center = new Point(startX, startY);
        this.color = color;
        type = "Circle";
        InGameArea = true;
        pop = false;
    }

    @Override
    public void grow(float speed) {
        radius += speed;
        InGameArea = center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
    }

    @Override
    public IGameObject NewInstance() {
        return GetInstance();
    }

    @Override
    public boolean InGameArea() {
        if (!pop) {
            return InGameArea;
        }
        return true;
    }

    @Override
    public boolean pointInside(Point point) {
        if ((point.x - center.x) * (point.x - center.x) + (point.y - center.y) * (point.y - center.y) <= radius * radius) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void pop() {
        pop = true;
        InGameArea = false;
    }

    @Override
    public float getArea() {
        return (float)(Math.PI * (radius * radius) );
    }

    public static IGameObject GetInstance() {
        return new ObstacleSpecialFrenzy(0,0,0, Color.WHITE);
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

    public void update(Point point) {
        center = point;
    }

    @Override
    public boolean CollideCircle(Point obCenter, float obSize) {
        // if distance <= sum(radius) circles touch
        float distSq = (obCenter.x - center.x) * (obCenter.x - center.x) + (obCenter.y - center.y) * (obCenter.y - center.y);
        float radSumSq = (obSize + radius) * (obSize + radius);
        return distSq <= radSumSq;
    }

    @Override
    public boolean CollideSquare(Point obCenter, float obSize) {
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
    public boolean CollideTriangleUp(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF tRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        RectF cRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        float radSq = radius * radius;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( tRect.left <= cRect.right && tRect.right >= cRect.left && tRect.top <= cRect.bottom && tRect.bottom >= cRect.top) {
            // First check triangle points and center
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
            if (tRect.bottom >= center.y ) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //top left
            if (obCenter.x >= center.x) {
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
            if (obCenter.x <= center.x) {
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

    @Override
    public boolean CollideTriangleDown(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF tRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        RectF cRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        float radSq = radius * radius;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( tRect.left <= cRect.right && tRect.right >= cRect.left && tRect.top <= cRect.bottom && tRect.bottom >= cRect.top) {
            // First check triangle points and center
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (tRect.top - center.y)*(tRect.top - center.y) <= radSq ) {
                return true;
            }
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (tRect.bottom - center.y)*(tRect.bottom - center.y) <= radSq ) {
                return true;
            }
            if ( (tRect.left - center.x)*(tRect.left - center.x) + (tRect.top - center.y)*(tRect.top - center.y) <= radSq ) {
                return true;
            }
            if ( (tRect.right - center.x)*(tRect.right - center.x) + (tRect.top - center.y)*(tRect.top - center.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //Top
            if (tRect.top >= center.y ) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.top;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x >= center.x) {
                m = (tRect.top - tRect.bottom) / (tRect.left - obCenter.x);
                b = tRect.top - tRect.left * m;
                for (int i = (int) tRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= center.x) {
                m = (tRect.bottom - tRect.top) / (obCenter.x - tRect.right);
                b = tRect.bottom - obCenter.x * m;
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

    @Override
    public boolean CollideRhombus(Point obCenter, float obSize) {
        float width = obSize * 2.0f;
//        float height = width * 1.732f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f))) * 2.0f;

        RectF rRect = new RectF(obCenter.x - obSize, obCenter.y - (height / 2.0f), obCenter.x + obSize, obCenter.y + (height / 2.0f) );
        RectF cRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        float radSq = radius * radius;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( rRect.left <= cRect.right && rRect.right >= cRect.left && rRect.top <= cRect.bottom && rRect.bottom >= cRect.top) {
            // First check triangle points and center
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (rRect.top - center.y)*(rRect.top - center.y) <= radSq ) {
                return true;
            }
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (rRect.bottom - center.y)*(rRect.bottom - center.y) <= radSq ) {
                return true;
            }
            if ( (rRect.left - center.x)*(rRect.left - center.x) + (obCenter.y - center.y)*(obCenter.y - center.y) <= radSq ) {
                return true;
            }
            if ( (rRect.right - center.x)*(rRect.right - center.x) + (obCenter.y - center.y)*(obCenter.y - center.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x >= center.x && obCenter.y >= center.y) {
                m = (obCenter.y - rRect.top) / (rRect.left - obCenter.x);
                b = obCenter.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x <= center.x && obCenter.y >= center.y) {
                m = (rRect.top - obCenter.y) / (obCenter.x - rRect.right);
                b = rRect.top - obCenter.x * m;
                for (int i = obCenter.x; i <= rRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x >= center.x && obCenter.y <= center.y) {
                m = (obCenter.y - rRect.bottom) / (rRect.left - obCenter.x);
                b = obCenter.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= obCenter.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (rRect.bottom - obCenter.y) / (obCenter.x - rRect.right);
                b = rRect.bottom - obCenter.x * m;
                for (int i = obCenter.x; i <= rRect.right; i++) {
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

    @Override
    public boolean CollideHexagon(Point obCenter, float obSize) {
        float hexWidth = obSize * 2.0f;
        float hexHeight = (float)(Math.sqrt((hexWidth*hexWidth) - (hexWidth/2.0f)*(hexWidth/2.0f)));
        float hexSize = hexWidth / 2.0f;

        Point hexLeft = new Point((int)(obCenter.x - hexSize), obCenter.y );
        Point hexTopLeft = new Point(obCenter.x - (int)(0.5f * hexSize), (int)(obCenter.y - (hexHeight/2.0f)));
        Point hexTopRight = new Point(obCenter.x + (int)(0.5f * hexSize), (int)(obCenter.y - (hexHeight/2.0f)));
        Point hexRight = new Point((int)(obCenter.x + hexSize), obCenter.y );
        Point hexBottomRight = new Point(obCenter.x + (int)(0.5f * hexSize), (int)(obCenter.y + (hexHeight/2.0f)));
        Point hexBottomLeft = new Point(obCenter.x - (int)(0.5f * hexSize), (int)(obCenter.y + (hexHeight/2.0f)));

        RectF hRect = new RectF(obCenter.x - obSize, obCenter.y - (hexHeight / 2.0f), obCenter.x + obSize, obCenter.y + (hexHeight / 2.0f) );
        RectF cRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        float radSq = radius * radius;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( hRect.left <= cRect.right && hRect.right >= cRect.left && hRect.top <= cRect.bottom && hRect.bottom >= cRect.top) {
            // First check 6 points and top and bottom center
            if ( (hexLeft.x - center.x)*(hexLeft.x - center.x) + (hexLeft.y - center.y)*(hexLeft.y - center.y) <= radSq ) {
                return true;
            }
            if ( (hexTopLeft.x - center.x)*(hexTopLeft.x - center.x) + (hexTopLeft.y - center.y)*(hexTopLeft.y - center.y) <= radSq ) {
                return true;
            }
            if ( (hexTopRight.x - center.x)*(hexTopRight.x - center.x) + (hexTopRight.y - center.y)*(hexTopRight.y - center.y) <= radSq ) {
                return true;
            }
            if ( (hexRight.x - center.x)*(hexRight.x - center.x) + (hexRight.y - center.y)*(hexRight.y - center.y) <= radSq ) {
                return true;
            }
            if ( (hexBottomRight.x - center.x)*(hexBottomRight.x - center.x) + (hexBottomRight.y - center.y)*(hexBottomRight.y - center.y) <= radSq ) {
                return true;
            }
            if ( (hexBottomLeft.x - center.x)*(hexBottomLeft.x - center.x) + (hexBottomLeft.y - center.y)*(hexBottomLeft.y - center.y) <= radSq ) {
                return true;
            }
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (hexTopLeft.y - center.y)*(hexTopLeft.y - center.y) <= radSq ) {
                return true;
            }
            if ( (obCenter.x - center.x)*(obCenter.x - center.x) + (hexBottomLeft.y - center.y)*(hexBottomLeft.y - center.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x >= center.x && obCenter.y >= center.y) {
                m = (hexLeft.y - hexTopLeft.y) / (hexLeft.x - hexTopLeft.x);
                b = hexLeft.y - hexLeft.x * m;
                for (int i = hexLeft.x; i <= hexTopLeft.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x <= center.x && obCenter.y >= center.y) {
                m = (hexTopRight.y - hexRight.y) / (hexTopRight.x - hexRight.x);
                b = hexTopRight.y - hexTopRight.x * m;
                for (int i = hexTopRight.x; i <= hexRight.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x >= center.x && obCenter.y <= center.y) {
                m = (hexLeft.y - hexBottomLeft.y) / (hexLeft.x - hexBottomLeft.x);
                b = hexLeft.y - hexLeft.x * m;
                for (int i = hexLeft.x; i <= hexBottomLeft.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radSq) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (hexBottomRight.y - hexRight.y) / (hexBottomRight.x - hexRight.x);
                b = hexBottomRight.y - hexBottomRight.x * m;
                for (int i = hexBottomRight.x; i <= hexRight.x; i++) {
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
