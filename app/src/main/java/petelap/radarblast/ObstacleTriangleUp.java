package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 3/27/2018.
 */

public class ObstacleTriangle implements IGameObject {
    private float width;
    private float height;
    private boolean inverted;
    private int color;
    private Path path;
    private Point center;
    private float size;
    private String type;
    private boolean InGameArea;

    public ObstacleTriangle(float width, float height, boolean inverted, int startX, int startY, int color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.inverted = inverted;

        center = new Point(startX, startY);
        size = width / 2.0f;
        type = "Triangle";

        Point Left = new Point(startX, startY);
        Point Center = new Point(startX,startY);
        Point Right = new Point(startX, startY);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Center.x, Center.y);
        path.lineTo(Right.x, Right.y);
        path.close();
    }

    @Override
    public void grow(float speed) {
        width = width + (speed * 2);
        height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));
        size = width / 2.0f;

        Point Left;
        Point Center;
        Point Right;

        if(inverted) {
            Left = new Point((int)(center.x - width/2.0f), (int)(center.y - (height/2.0f)));
            Center = new Point(center.x, (int)(center.y + (height/2.0f)));
            Right = new Point((int)(center.x + width/2.0f), (int)(center.y - (height/2.0f)));
        } else {
            Left = new Point((int)(center.x - width/2.0f), (int)(center.y + (height/2.0f)));
            Center = new Point(center.x, (int)(center.y - (height/2.0f)));
            Right = new Point((int)(center.x + width/2.0f), (int)(center.y + (height/2.0f)));
        }

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Center.x, Center.y);
        path.lineTo(Right.x, Right.y);
        path.close();

        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && Center.y >= Constants.HEADER_HEIGHT && Center.y <= Constants.SCREEN_HEIGHT && Left.y <= Constants.SCREEN_HEIGHT;
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
    public float getArea() {
        // BL = 1, Top = 2, BR = 3
        return (float)area((int)(center.x - size), (int)(center.y + height/2.0), center.x, (int)(center.y - height/2.0), (int)(center.x + size), (int)(center.y - height/2.0) );
    }

    public static IGameObject GetInstance() {
        return new ObstacleTriangle(0,0, false,0,0, Color.YELLOW);
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
        // Triangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawPath(path, paint);
        // Border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawPath(path, paint);
    }

    public void update(Point point) {
        center = point;

        Point Left = new Point((int)(center.x - width/2.0f), (int)(center.y + (height/2.0f)));
        Point Center = new Point(center.x, (int)(center.y - (height/2.0f)));
        Point Right = new Point((int)(center.x + width/2.0f), (int)(center.y + (height/2.0f)));

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Center.x, Center.y);
        path.lineTo(Right.x, Right.y);
        path.close();

        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && Center.y >= Constants.HEADER_HEIGHT && Left.y <= Constants.SCREEN_HEIGHT;
    }

    @Override
    public boolean obstacleCollideCircle(Point obCenter, float obSize) {
        float width = size * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF cRect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize);
        RectF tRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        float radSq = obSize * obSize;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( tRect.left <= cRect.right && tRect.right >= cRect.left && tRect.top <= cRect.bottom && tRect.bottom >= cRect.top) {
            // First check triangle points and bottom center
            if ( (center.x - obCenter.x)*(center.x - obCenter.x) + (tRect.top - obCenter.y)*(tRect.top - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (center.x - obCenter.x)*(center.x - obCenter.x) + (tRect.bottom - obCenter.y)*(tRect.bottom - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (tRect.left - obCenter.x)*(tRect.left - obCenter.x) + (tRect.bottom - obCenter.y)*(tRect.bottom - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (tRect.right - obCenter.x)*(tRect.right - obCenter.x) + (tRect.bottom - obCenter.y)*(tRect.bottom - obCenter.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //bottom
            if(obCenter.y > tRect.bottom) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {return true;}
                }
            }
            //top left
            if (obCenter.x < center.x) {
                m = (tRect.bottom - tRect.top) / (tRect.left - center.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {return true;}
                }
            }
            //top right
            if (obCenter.x > center.x) {
                m = (tRect.top - tRect.bottom) / (center.x - tRect.right);
                b = tRect.top - center.x * m;
                for (int i = center.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {return true;}
                }
            }
        }
        return false;
    }

    @Override
    public boolean obstacleCollideSquare(Point obCenter, float obSize) {
        float width = size * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF square = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        RectF tRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );
        // First check triangle points and bottom center
        if (square.contains(center.x, tRect.top) ) {return true;}
        if (square.contains(center.x, tRect.bottom) ) {return true;}
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
            if (obCenter.y > tRect.bottom) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if (square.contains(x, y)) {return true;}
                }
            }
            //top left
            if (obCenter.x < center.x) {
                m = (tRect.bottom - tRect.top) / (tRect.left - center.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {return true;}
                }
            }
            //top right
            if (obCenter.x > center.x) {
                m = (tRect.top - tRect.bottom) / (center.x - tRect.right);
                b = tRect.top - center.x * m;
                for (int i = center.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {return true;}
                }
            }
        }
        return false;
    }

    @Override
    public boolean obstacleCollideTriangle(Point obCenter, float obSize) {
        float tWidth = obSize * 2.0f;
        float tHeight = (float)(Math.sqrt((tWidth*tWidth) - (tWidth/2.0f)*(tWidth/2.0f)));
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - (tHeight / 2.0f), obCenter.x + obSize, obCenter.y + (tHeight / 2.0f) );

        RectF tRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( tRect.left <= rect.right && tRect.right >= rect.left && tRect.top <= rect.bottom && tRect.bottom >= rect.top) {
            // If GameObject is inside ClassObject
            Point bLeft = new Point((int)tRect.left, (int)tRect.bottom);
            Point tCenter = new Point(center.x, (int)tRect.top);
            Point bRight = new Point((int)tRect.right, (int)tRect.bottom);

            if ( isInside(bLeft.x,bLeft.y, tCenter.x, tCenter.y, bRight.x, bRight.y, (int)rect.left, (int)rect.bottom) ){
                return true;
            }
            if ( isInside(bLeft.x,bLeft.y, tCenter.x, tCenter.y, bRight.x, bRight.y, obCenter.x, (int)rect.top) ){
                return true;
            }
            if ( isInside(bLeft.x,bLeft.y, tCenter.x, tCenter.y, bRight.x, bRight.y, (int)rect.right, (int)rect.bottom) ){
                return true;
            }

            // If ClassObject is inside GameObject
            bLeft = new Point((int)rect.left, (int)rect.bottom);
            tCenter = new Point(obCenter.x, (int)rect.top);
            bRight = new Point((int)rect.right, (int)rect.bottom);

            if ( isInside(bLeft.x,bLeft.y, tCenter.x, tCenter.y, bRight.x, bRight.y, (int)tRect.left, (int)tRect.bottom) ){
                return true;
            }
            if ( isInside(bLeft.x,bLeft.y, tCenter.x, tCenter.y, bRight.x, bRight.y, center.x, (int)tRect.top) ){
                return true;
            }
            return isInside(bLeft.x, bLeft.y, tCenter.x, tCenter.y, bRight.x, bRight.y, (int) tRect.right, (int) tRect.bottom);
        }
        return false;
    }

    private static double area(int x1, int y1, int x2, int y2, int x3, int y3){
        /* calculate area of triangle formed by (x1, y1) (x2, y2) and (x3, y3) */
        return Math.abs( (x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2) ) / 2.0);
    }

    private static boolean isInside(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y){
        /* check if Point (x,y) is inside (x1,y1) / (x2,y2) \ (x3,y3) */
        /* Calculate area of triangles and points and compare to total area */
        double A = area(x1, y1, x2, y2, x3, y3);
        double A1 = area(x, y, x2, y2, x3, y3);
        double A2 = area(x1, y1, x, y, x3, y3);
        double A3 = area(x1, y1, x2, y2, x, y);
        /* Check if sum of A1, A2 and A3 is same as A */
        return (A == A1 + A2 + A3);
    }

}
