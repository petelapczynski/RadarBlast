package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Pete on 7/4/2018.
 */

public class ObstacleHexagon implements IGameObject {
    private float width;
    private float height;
    private int color;
    private Path path;
    private Point center, Left, TopLeft, TopRight, Right, BottomLeft, BottomRight;
    private float size;
    private String type;
    private boolean InGameArea;
    private boolean pop;

    public ObstacleHexagon(float width, float height, int startX, int startY, int color) {
        this.width = width;
        this.height = height;
        this.color = color;

        center = new Point(startX, startY);
        size = width / 2.0f;
        type = "Hexagon";
        InGameArea = true;
        pop = false;

        Left = new Point(startX, startY);
        TopLeft = new Point(startX,startY);
        TopRight = new Point(startX,startY);
        Right = new Point(startX, startY);
        BottomLeft = new Point(startX, startY);
        BottomRight = new Point(startX, startY);


        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(TopLeft.x, TopLeft.y);
        path.lineTo(TopRight.x, TopRight.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(BottomRight.x, BottomRight.y);
        path.lineTo(BottomLeft.x, BottomLeft.y);
        path.close();
    }

    @Override
    public void grow(float speed) {
        width = width + (speed * 2);
        //height = width;
        height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));
        size = width / 2.0f;

        Left = new Point((int)(center.x - size), center.y );
        TopLeft = new Point(center.x - (int)(0.5f * size), (int)(center.y - (height/2.0f)));
        TopRight = new Point(center.x + (int)(0.5f * size), (int)(center.y - (height/2.0f)));
        Right = new Point((int)(center.x + size), center.y );
        BottomRight = new Point(center.x + (int)(0.5f * size), (int)(center.y + (height/2.0f)));
        BottomLeft = new Point(center.x - (int)(0.5f * size), (int)(center.y + (height/2.0f)));

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(TopLeft.x, TopLeft.y);
        path.lineTo(TopRight.x, TopRight.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(BottomRight.x, BottomRight.y);
        path.lineTo(BottomLeft.x, BottomLeft.y);
        path.close();

        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && TopLeft.y >= Constants.HEADER_HEIGHT && BottomLeft.y <= Constants.SCREEN_HEIGHT;
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
        if (IsPointInTri(point, Left, TopLeft, center)) {
            return true;
        }
        if (IsPointInTri(point, TopLeft, center, TopRight)) {
            return true;
        }
        if (IsPointInTri(point, center, TopRight, Right)) {
            return true;
        }
        if (IsPointInTri(point, center, BottomRight, Right)) {
            return true;
        }
        if (IsPointInTri(point, BottomLeft, center, BottomRight)) {
            return true;
        }
        if (IsPointInTri(point, Left, BottomLeft, center)) {
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
        // area of regular hexagon = (3 * Sqrt(3) * SideLength) / 2
        float side = TopLeft.x - TopRight.x;
        return (3 * (float)Math.sqrt(3) * side * side) / 2f;
    }

    public static IGameObject GetInstance() {
        return new ObstacleHexagon(0,0, 0,0, Color.rgb(0,255,0));
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
        // Hexagon
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
        TopLeft = new Point(center.x, (int)(center.y - (height/2.0f)));
        TopRight = new Point(center.x, (int)(center.y - (height/2.0f)));
        Right = new Point((int)(center.x + width/2.0f), (int)(center.y + (height/2.0f)));
        BottomRight = new Point(center.x, (int)(center.y + (height/2.0f)));
        BottomLeft = new Point(center.x, (int)(center.y + (height/2.0f)));

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(TopLeft.x, TopLeft.y);
        path.lineTo(TopRight.x, TopRight.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(BottomRight.x, BottomRight.y);
        path.lineTo(BottomLeft.x, BottomLeft.y);
        path.close();

        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && TopLeft.y >= Constants.HEADER_HEIGHT && BottomLeft.y <= Constants.SCREEN_HEIGHT;
    }

    @Override
    public boolean CollideCircle(Point obCenter, float obSize) {
        RectF cRect = new RectF(obCenter.x - obSize, obCenter.y - obSize, obCenter.x + obSize, obCenter.y + obSize);
        RectF hRect = new RectF(center.x - size, center.y - size, center.x + size, center.y + size );

        float radSq = obSize * obSize;

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        // points in circle: (x - CircleX)^2 + (y - CircleY)^2 <= Radius^2
        if ( hRect.left <= cRect.right && hRect.right >= cRect.left && hRect.top <= cRect.bottom && hRect.bottom >= cRect.top) {
            // First check points, top and bottom center
            if ( (Left.x - obCenter.x)*(Left.x - obCenter.x) + (Left.y - obCenter.y)*(Left.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (TopLeft.x - obCenter.x)*(TopLeft.x - obCenter.x) + (TopLeft.y - obCenter.y)*(TopLeft.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (TopRight.x - obCenter.x)*(TopRight.x - obCenter.x) + (TopRight.y - obCenter.y)*(TopRight.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (Right.x - obCenter.x)*(Right.x - obCenter.x) + (Right.y - obCenter.y)*(Right.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (BottomRight.x - obCenter.x)*(BottomRight.x - obCenter.x) + (BottomRight.y - obCenter.y)*(BottomRight.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (BottomLeft.x - obCenter.x)*(BottomLeft.x - obCenter.x) + (BottomLeft.y - obCenter.y)*(BottomLeft.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (center.x - obCenter.x)*(center.x - obCenter.x) + (TopLeft.y - obCenter.y)*(TopLeft.y - obCenter.y) <= radSq ) {
                return true;
            }
            if ( (center.x - obCenter.x)*(center.x - obCenter.x) + (BottomLeft.y - obCenter.y)*(BottomLeft.y - obCenter.y) <= radSq ) {
                return true;
            }

            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (Left.y - TopLeft.y) / (Left.x - TopLeft.x);
                b = Left.y - Left.x * m;
                for (int i = Left.x; i <= TopLeft.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (TopRight.y - Right.y) / (TopRight.x - Right.x);
                b = TopRight.y - TopRight.x * m;
                for (int i = TopRight.x; i <= Right.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (Left.y - BottomLeft.y) / (Left.x - BottomLeft.x);
                b = Left.y - Left.x * m;
                for (int i = Left.x; i <= BottomLeft.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if ( (x - obCenter.x)*(x - obCenter.x) + (y - obCenter.y)*(y - obCenter.y) <= radSq ) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (BottomRight.y - Right.y) / (BottomRight.x - Right.x);
                b = BottomRight.y - BottomRight.x * m;
                for (int i = BottomRight.x; i <= Right.x; i++) {
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
        RectF hRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );
        // First check 6, top and bottom center
        if (square.contains(Left.x, Left.y) ) {
            return true;
        }
        if (square.contains(TopLeft.x, TopLeft.y) ) {
            return true;
        }
        if (square.contains(TopRight.x, TopRight.y) ) {
            return true;
        }
        if (square.contains(Right.x, Right.y) ) {
            return true;
        }
        if (square.contains(BottomRight.x, BottomRight.y) ) {
            return true;
        }
        if (square.contains(BottomLeft.x, BottomLeft.y) ) {
            return true;
        }
        if (square.contains(center.x, TopLeft.y) ) {
            return true;
        }
        if (square.contains(center.x, BottomLeft.y) ) {
            return true;
        }

        // check bounding box collision first, then if inside create a list of points, for each line
        // points on a line slope: m = (y1 - y2) / (x1-x2); b = y1 - x1 * m; Loop for x: y = mx + b;
        if ( hRect.left <= square.right && hRect.right >= square.left && hRect.top <= square.bottom && hRect.bottom >= square.top) {
            float m;
            float b;
            float x;
            float y;

            //top left
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (Left.y - TopLeft.y) / (Left.x - TopLeft.x);
                b = Left.y - Left.x * m;
                for (int i = Left.x; i <= TopLeft.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //top right
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (TopRight.y - Right.y) / (TopRight.x - Right.x);
                b = TopRight.y - TopRight.x * m;
                for (int i = TopRight.x; i <= Right.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom left
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (Left.y - BottomLeft.y) / (Left.x - BottomLeft.x);
                b = Left.y - Left.x * m;
                for (int i = Left.x; i <= BottomLeft.x; i++) {
                    x = (float) i;
                    y = m * x + b;
                    if (square.contains(x, y)) {
                        return true;
                    }
                }
            }
            //bottom right
            if (obCenter.x <= center.x && obCenter.y <= center.y) {
                m = (BottomRight.y - Right.y) / (BottomRight.x - Right.x);
                b = BottomRight.y - BottomRight.x * m;
                for (int i = BottomRight.x; i <= Right.x; i++) {
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

        RectF hRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( hRect.left <= rect.right && hRect.right >= rect.left && hRect.top <= rect.bottom && hRect.bottom >= rect.top) {

            //GameObject points
            Point gobLeft = new Point((int)rect.left, (int)rect.bottom);
            Point gobCenter = new Point(obCenter.x, (int)rect.top);
            Point gobRight = new Point((int)rect.right, (int)rect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(TopRight, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(new Point(center.x, TopLeft.y), gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(new Point(center.x, BottomLeft.y), gobLeft, gobCenter, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobLeft, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobCenter, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobRight, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobLeft, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobCenter, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobRight,BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobLeft, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, BottomLeft, center)){
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

        RectF hRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( hRect.left <= rect.right && hRect.right >= rect.left && hRect.top <= rect.bottom && hRect.bottom >= rect.top) {

            // flats collide
            if (obCenter.y >= center.y && obCenter.x >= hRect.left && obCenter.x <= hRect.right ){
                return true;
            }

            //GameObject points
            Point gobLeft = new Point((int)rect.left, (int)rect.top);
            Point gobCenter = new Point(obCenter.x, (int)rect.bottom);
            Point gobRight = new Point((int)rect.right, (int)rect.top);

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(TopRight, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(new Point(center.x, TopLeft.y), gobLeft, gobCenter, gobRight)){
                return true;
            }
            if (IsPointInTri(new Point(center.x, BottomLeft.y), gobLeft, gobCenter, gobRight)){
                return true;
            }

            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobLeft, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobCenter, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobRight, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobLeft, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobCenter, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobCenter, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobRight,BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobLeft, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobCenter, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, BottomLeft, center)){
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

        RectF hRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( hRect.left <= rect.right && hRect.right >= rect.left && hRect.top <= rect.bottom && hRect.bottom >= rect.top) {

            //GameObject points
            Point gobLeft = new Point((int)rect.left, obCenter.y);
            Point gobTop = new Point(obCenter.x, (int)rect.top);
            Point gobRight = new Point((int)rect.right, obCenter.y);
            Point gobBottom = new Point(obCenter.x, (int)rect.bottom);

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(TopRight, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, gobLeft, gobTop, gobRight)){
                return true;
            }
            if (IsPointInTri(Left, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(TopRight, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(Right, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, gobLeft, gobBottom, gobRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, gobLeft, gobBottom, gobRight)){
                return true;
            }
            // is GameObject inside ClassObject
            if (IsPointInTri(gobLeft, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobTop, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobBottom, Left, TopLeft, center)){
                return true;
            }
            if (IsPointInTri(gobLeft, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobTop, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobRight, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobBottom, TopLeft, center, TopRight)){
                return true;
            }
            if (IsPointInTri(gobLeft, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobTop, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobBottom, center, TopRight, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobTop, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobRight, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobBottom, center, BottomRight, Right)){
                return true;
            }
            if (IsPointInTri(gobLeft, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobTop, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobRight, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobBottom, BottomLeft, center, BottomRight)){
                return true;
            }
            if (IsPointInTri(gobLeft, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobTop, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobRight, Left, BottomLeft, center)){
                return true;
            }
            if (IsPointInTri(gobBottom, Left, BottomLeft, center)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean CollideHexagon(Point obCenter, float obSize) {
        float hexWidth = obSize * 2.0f;
        float hexHeight = (float)(Math.sqrt((hexWidth*hexWidth) - (hexWidth/2.0f)*(hexWidth/2.0f)));
        float hexSize = hexWidth / 2.0f;

        RectF rect = new RectF(obCenter.x - obSize, obCenter.y - (hexHeight / 2.0f), obCenter.x + obSize, obCenter.y + (hexHeight / 2.0f) );

        RectF hRect = new RectF(center.x - size, center.y - (height / 2.0f), center.x + size, center.y + (height / 2.0f) );

        // check bounding box collision first, then check points inside each other
        // point is inside triangle of sum(area of all three individually) = area of total triangle
        if ( hRect.left <= rect.right && hRect.right >= rect.left && hRect.top <= rect.bottom && hRect.bottom >= rect.top) {

            //GameObject points
            Point hexLeft = new Point((int)(obCenter.x - hexSize), obCenter.y );
            Point hexTopLeft = new Point(obCenter.x - (int)(0.5f * hexSize), (int)(obCenter.y - (hexHeight/2.0f)));
            Point hexTopRight = new Point(obCenter.x + (int)(0.5f * hexSize), (int)(obCenter.y - (hexHeight/2.0f)));
            Point hexRight = new Point((int)(obCenter.x + hexSize), obCenter.y );
            Point hexBottomRight = new Point(obCenter.x + (int)(0.5f * hexSize), (int)(obCenter.y + (hexHeight/2.0f)));
            Point hexBottomLeft = new Point(obCenter.x - (int)(0.5f * hexSize), (int)(obCenter.y + (hexHeight/2.0f)));

            // is ClassObject inside GameObject
            if (IsPointInTri(Left, hexLeft, hexTopLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(TopLeft, hexLeft, hexTopLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(TopRight, hexLeft, hexTopLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(Right, hexLeft, hexTopLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(BottomRight, hexLeft, hexTopLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(BottomLeft, hexLeft, hexTopLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(Left, hexTopLeft, obCenter, hexTopRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, hexTopLeft, obCenter, hexTopRight)){
                return true;
            }
            if (IsPointInTri(TopRight, hexTopLeft, obCenter, hexTopRight)){
                return true;
            }
            if (IsPointInTri(Right, hexTopLeft, obCenter, hexTopRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, hexTopLeft, obCenter, hexTopRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, hexTopLeft, obCenter, hexTopRight)){
                return true;
            }
            if (IsPointInTri(Left, obCenter, hexTopRight, hexRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, obCenter, hexTopRight, hexRight)){
                return true;
            }
            if (IsPointInTri(TopRight, obCenter, hexTopRight, hexRight)){
                return true;
            }
            if (IsPointInTri(Right, obCenter, hexTopRight, hexRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, obCenter, hexTopRight, hexRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, obCenter, hexTopRight, hexRight)){
                return true;
            }
            if (IsPointInTri(Left, obCenter, hexBottomRight, hexRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, obCenter, hexBottomRight, hexRight)){
                return true;
            }
            if (IsPointInTri(TopRight, obCenter, hexBottomRight, hexRight)){
                return true;
            }
            if (IsPointInTri(Right, obCenter, hexBottomRight, hexRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, obCenter, hexBottomRight, hexRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, obCenter, hexBottomRight, hexRight)){
                return true;
            }
            if (IsPointInTri(Left, hexBottomLeft, obCenter, hexBottomRight)){
                return true;
            }
            if (IsPointInTri(TopLeft, hexBottomLeft, obCenter, hexBottomRight)){
                return true;
            }
            if (IsPointInTri(TopRight, hexBottomLeft, obCenter, hexBottomRight)){
                return true;
            }
            if (IsPointInTri(Right, hexBottomLeft, obCenter, hexBottomRight)){
                return true;
            }
            if (IsPointInTri(BottomRight, hexBottomLeft, obCenter, hexBottomRight)){
                return true;
            }
            if (IsPointInTri(BottomLeft, hexBottomLeft, obCenter, hexBottomRight)){
                return true;
            }
            if (IsPointInTri(Left, hexLeft, hexBottomLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(TopLeft, hexLeft, hexBottomLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(TopRight, hexLeft, hexBottomLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(Right, hexLeft, hexBottomLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(BottomRight, hexLeft, hexBottomLeft, obCenter)){
                return true;
            }
            if (IsPointInTri(BottomLeft, hexLeft, hexBottomLeft, obCenter)){
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
