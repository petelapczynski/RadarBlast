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

public class ObstacleTriangleUp implements IGameObject {
    private float width;
    private float height;
    private int color;
    private Path path;
    private Point center;
    private float size;
    private String type;
    private boolean InGameArea;
    private boolean pop;

    public ObstacleTriangleUp(float width, float height, int startX, int startY, int color) {
        this.width = width;
        this.height = height;
        this.color = color;

        center = new Point(startX, startY);
        size = width / 2.0f;
        type = "TriangleUp";
        InGameArea = true;
        pop = false;

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
        Point Left = new Point((int)(center.x - width/2.0f), (int)(center.y + (height/2.0f)));
        Point Center = new Point(center.x, (int)(center.y - (height/2.0f)));
        Point Right = new Point((int)(center.x + width/2.0f), (int)(center.y + (height/2.0f)));

        return IsPointInTri(point, Left, Center, Right);
    }

    @Override
    public void pop() {
        pop = true;
    }

    @Override
    public float getArea() {
        // area of equilateral triangle = sqr(3) / 4 * (Side*Side)
        return (float)(Math.sqrt(3)/4)*(width * width);
    }

    public static IGameObject GetInstance() {
        return new ObstacleTriangleUp(0,0, 0,0, Color.YELLOW);
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
    public boolean CollideCircle(Point obCenter, float obSize) {
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
            if(obCenter.y >= tRect.bottom) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //top left
            if (obCenter.x <= center.x) {
                m = (tRect.bottom - tRect.top) / (tRect.left - center.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x >= center.x) {
                m = (tRect.top - tRect.bottom) / (center.x - tRect.right);
                b = tRect.top - center.x * m;
                for (int i = center.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean CollideSquare(Point obCenter, float obSize) {
        float width = size * 2.0f;
        float height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));

        RectF square = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        RectF tRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );
        // First check triangle points and bottom center
        if (square.contains(center.x, tRect.top) ) {
            return true;
        }
        if (square.contains(center.x, tRect.bottom) ) {
            return true;
        }
        if (square.contains(tRect.left, tRect.bottom) ) {
            return true;
        }
        if (square.contains(tRect.right, tRect.bottom) ) {
            return true;
        }
        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( tRect.left <= square.right && tRect.right >= square.left && tRect.top <= square.bottom && tRect.bottom >= square.top) {
            float m;
            float b;
            float x;
            float y;

            //bottom
            if (obCenter.y >= tRect.bottom) {
                for (int i = (int) tRect.left; i <= (int) tRect.right; i++) {
                    x = (float) i;
                    y = tRect.bottom;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top left
            if (obCenter.x <= center.x) {
                m = (tRect.bottom - tRect.top) / (tRect.left - center.x);
                b = tRect.bottom - tRect.left * m;
                for (int i = (int) tRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x >= center.x) {
                m = (tRect.top - tRect.bottom) / (center.x - tRect.right);
                b = tRect.top - center.x * m;
                for (int i = center.x; i <= tRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean CollideTriangleUp(Point obCenter, float obSize) {
        float tWidth = obSize * 2.0f;
        float tHeight = (float)(Math.sqrt((tWidth*tWidth) - (tWidth/2.0f)*(tWidth/2.0f)));
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - (tHeight / 2.0f), obCenter.x + obSize, obCenter.y + (tHeight / 2.0f) );

        RectF tRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( tRect.left <= rect.right && tRect.right >= rect.left && tRect.top <= rect.bottom && tRect.bottom >= rect.top) {

            //GameObject points
            Point gobLeft = new Point((int)rect.left, (int)rect.bottom);
            Point gobCenter = new Point(obCenter.x, (int)rect.top);
            Point gobRight = new Point((int)rect.right, (int)rect.bottom);
            //ClassObject points
            Point cobLeft = new Point((int)tRect.left, (int)tRect.bottom);
            Point cobCenter = new Point(center.x, (int)tRect.top);
            Point cobRight = new Point((int)tRect.right, (int)tRect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(cobLeft, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(cobCenter, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(cobRight, gobLeft, gobCenter, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, cobLeft, cobCenter, cobRight)){
                return true;
            }
            if (IsPointInTri(gobCenter, cobLeft, cobCenter, cobRight)){
                return true;
            }
            if (IsPointInTri(gobRight, cobLeft, cobCenter, cobRight)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean CollideTriangleDown(Point obCenter, float obSize) {
        float tWidth = obSize * 2.0f;
        float tHeight = (float)(Math.sqrt((tWidth*tWidth) - (tWidth/2.0f)*(tWidth/2.0f)));
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - (tHeight / 2.0f), obCenter.x + obSize, obCenter.y + (tHeight / 2.0f) );

        RectF tRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( tRect.left <= rect.right && tRect.right >= rect.left && tRect.top <= rect.bottom && tRect.bottom >= rect.top) {

            // flats collide
            if (obCenter.y >= center.y && obCenter.x >= tRect.left && obCenter.x <= tRect.right ){
                return true;
            }

            //GameObject points
            Point gobLeft = new Point((int)rect.left, (int)rect.top);
            Point gobCenter = new Point(obCenter.x, (int)rect.bottom);
            Point gobRight = new Point((int)rect.right, (int)rect.top);
            //ClassObject points
            Point cobLeft = new Point((int)tRect.left, (int)tRect.bottom);
            Point cobCenter = new Point(center.x, (int)tRect.top);
            Point cobRight = new Point((int)tRect.right, (int)tRect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(cobLeft, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(cobCenter, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(cobRight, gobLeft, gobCenter, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, cobLeft, cobCenter, cobRight)){
                return true;
            }
            if (IsPointInTri(gobCenter, cobLeft, cobCenter, cobRight)){
                return true;
            }
            if (IsPointInTri(gobRight, cobLeft, cobCenter, cobRight)){
                return true;
            }
        }
        return false;
    }

    private static float Sign(Point p1, Point p2, Point p3)
    {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    private static boolean IsPointInTri(Point pt, Point v1, Point v2, Point v3)
    {
        boolean b1, b2, b3;

        b1 = Sign(pt, v1, v2) < 0.0f;
        b2 = Sign(pt, v2, v3) < 0.0f;
        b3 = Sign(pt, v3, v1) < 0.0f;

        return ((b1 == b2) && (b2 == b3));
    }

}
