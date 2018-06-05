package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 5/29/2018.
 */

public class ObstacleRhombus implements IGameObject {
    private float width;
    private float height;
    private int color;
    private Path path;
    private Point center, Left, Top, Right, Bottom;
    private float size;
    private String type;
    private boolean InGameArea;
    private boolean pop;

    public ObstacleRhombus(float width, float height, int startX, int startY, int color) {
        this.width = width;
        this.height = height;
        this.color = color;

        center = new Point(startX, startY);
        size = width / 2.0f;
        type = "Rhombus";
        InGameArea = true;
        pop = false;

        Left = new Point(startX, startY);
        Top = new Point(startX,startY);
        Right = new Point(startX, startY);
        Bottom = new Point(startX, startY);


        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Top.x, Top.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(Bottom.x, Bottom.y);
        path.close();
    }

    @Override
    public void grow(float speed) {
        width = width + (speed * 2);
        height = width * 1.732f;
        size = width / 2.0f;

        Left = new Point((int)(center.x - size), center.y );
        Top = new Point(center.x, (int)(center.y - (height/2.0f)));
        Right = new Point((int)(center.x + size), center.y );
        Bottom = new Point(center.x, (int)(center.y + (height/2.0f)));

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Top.x, Top.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(Bottom.x, Bottom.y);
        path.close();

        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && Top.y >= Constants.HEADER_HEIGHT && Bottom.y <= Constants.SCREEN_HEIGHT;
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
        if (IsPointInTri(point, Left, Top, Right)) {
            return true;
        }
        if (IsPointInTri(point, Left, Bottom, Right)) {
            return true;
        }
        return false;
    }

    @Override
    public void pop() {
        pop = true;
    }

    @Override
    public float getArea() {
        // area of rhombus = (d1*d2)/2
        return (width * height)/2f;
    }

    public static IGameObject GetInstance() {
        return new ObstacleRhombus(0,0, 0,0, Color.rgb(255,140,0));
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

        Left = new Point((int)(center.x - width/2.0f), (int)(center.y + (height/2.0f)));
        Top = new Point(center.x, (int)(center.y - (height/2.0f)));
        Right = new Point((int)(center.x + width/2.0f), (int)(center.y + (height/2.0f)));
        Bottom = new Point(center.x, (int)(center.y + (height/2.0f)));

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Top.x, Top.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(Bottom.x, Bottom.y);
        path.close();

        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && Top.y >= Constants.HEADER_HEIGHT && Bottom.y <= Constants.SCREEN_HEIGHT;
    }

    @Override
    public boolean CollideCircle(Point obCenter, float obSize) {
        RectF cRect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize);
        RectF rRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        float radSq = obSize * obSize;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( rRect.left <= cRect.right && rRect.right >= cRect.left && rRect.top <= cRect.bottom && rRect.bottom >= cRect.top) {
            // First check triangle points and bottom center
            if ( (center.x - obCenter.x)*(center.x - obCenter.x) + (rRect.top - obCenter.y)*(rRect.top - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (center.x - obCenter.x)*(center.x - obCenter.x) + (rRect.bottom - obCenter.y)*(rRect.bottom - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (rRect.left - obCenter.x)*(rRect.left - obCenter.x) + (center.y - obCenter.y)*(center.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (rRect.right - obCenter.x)*(rRect.right - obCenter.x) + (center.y - obCenter.y)*(center.y - obCenter.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (center.y - rRect.top) / (rRect.left - center.x);
                b = center.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x >= center.x && obCenter.y <= center.y) {
                m = (rRect.top - center.y) / (center.x - rRect.right);
                b = rRect.top - center.x * m;
                for (int i = center.x; i <= rRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x <= center.x && obCenter.y >= center.y) {
                m = (center.y - rRect.bottom) / (rRect.left - center.x);
                b = center.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x >= center.x && obCenter.y >= center.y) {
                m = (rRect.bottom - center.y) / (center.x - rRect.right);
                b = rRect.bottom - center.x * m;
                for (int i = center.x; i <= rRect.right; i++) {
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
        RectF square = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize );
        RectF rRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );
        // First check triangle points and bottom center
        if (square.contains(Left.x, Left.y) ) {
            return true;
        }
        if (square.contains(Top.x, Top.y) ) {
            return true;
        }
        if (square.contains(Right.x, Right.y) ) {
            return true;
        }
        if (square.contains(Bottom.x, Bottom.y) ) {
            return true;
        }
        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( rRect.left <= square.right && rRect.right >= square.left && rRect.top <= square.bottom && rRect.bottom >= square.top) {
            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (center.y - rRect.top) / (rRect.left - center.x);
                b = center.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x >= center.x && obCenter.y <= center.y) {
                m = (rRect.top - center.y) / (center.x - rRect.right);
                b = rRect.top - center.x * m;
                for (int i = center.x; i <= rRect.right; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x <= center.x && obCenter.y >= center.y) {
                m = (center.y - rRect.bottom) / (rRect.left - center.x);
                b = center.y - rRect.left * m;
                for (int i = (int) rRect.left; i <= center.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x >= center.x && obCenter.y >= center.y) {
                m = (rRect.bottom - center.y) / (center.x - rRect.right);
                b = rRect.bottom - center.x * m;
                for (int i = center.x; i <= rRect.right; i++) {
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
//            //ClassObject points
//            Point cobLeft = new Point((int)tRect.left, (int)tRect.bottom);
//            Point cobCenter = new Point(center.x, (int)tRect.top);
//            Point cobRight = new Point((int)tRect.right, (int)tRect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Top, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Bottom, gobLeft, gobCenter, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, Bottom, Right)){
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
//            //ClassObject points
//            Point cobLeft = new Point((int)tRect.left, (int)tRect.bottom);
//            Point cobCenter = new Point(center.x, (int)tRect.top);
//            Point cobRight = new Point((int)tRect.right, (int)tRect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Top, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Bottom, gobLeft, gobCenter, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, Bottom, Right)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean CollideRhombus(Point obCenter, float obSize) {
        float tWidth = obSize * 2.0f;
        float tHeight = (float)(Math.sqrt((tWidth*tWidth) - (tWidth/2.0f)*(tWidth/2.0f))) * 2.0f;
        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - (tHeight / 2.0f), obCenter.x + obSize, obCenter.y + (tHeight / 2.0f) );

        RectF rRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( rRect.left <= rect.right && rRect.right >= rect.left && rRect.top <= rect.bottom && rRect.bottom >= rect.top) {

            //GameObject points
            Point gobLeft = new Point((int)rect.left, obCenter.y);
            Point gobTop = new Point(obCenter.x, (int)rect.top);
            Point gobRight = new Point((int)rect.right, obCenter.y);
            Point gobBottom = new Point(obCenter.x, (int)rect.bottom);

//            //ClassObject points
//            Point cobLeft = new Point((int)tRect.left, (int)tRect.bottom);
//            Point cobCenter = new Point(center.x, (int)tRect.top);
//            Point cobRight = new Point((int)tRect.right, (int)tRect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(Top, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(Bottom, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(Left, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(Top, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(Bottom, gobLeft, gobBottom, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobTop, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobBottom, Left, Top, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobTop, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, Bottom, Right)){
                return true;
            }
            if (IsPointInTri(gobBottom, Left, Bottom, Right)){
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
