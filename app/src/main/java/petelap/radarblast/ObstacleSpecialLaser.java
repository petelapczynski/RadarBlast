package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.ArrayList;

public class ObstacleSpecialLaser extends ObstacleSpecials implements IGameObjectSpecial {
    private Vector2D v;
    private float speed;
    //private float distance;
    private Paint paint;
    private Paint bPaint;
    private ArrayList<PointF> points;
    private RectF rect;

    public ObstacleSpecialLaser(float startX, float startY, float radius, int color) {
        this.radius = radius;
        center = new PointF(startX, startY);
        rect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        this.color = color;
        type = "SpecialLaser";
        InGameArea = true;
        pop = false;
        v = new Vector2D(0,1);
        speed = 2;
        //distance = 0;
        points = new ArrayList<>();
        points.add(center);

        // Circle
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (radius + 1)*4f, this.color, Color.BLACK, Shader.TileMode.MIRROR));
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
    public IGameObjectSpecial NewInstance() {
        return GetInstance();
    }

    @Override
    public boolean InGameArea() {
        return InGameArea;
    }

    @Override
    public boolean pointInside(PointF point) {
        return (point.x - center.x) * (point.x - center.x) + (point.y - center.y) * (point.y - center.y) <= radius * radius;
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

    public static IGameObjectSpecial GetInstance() {
        return new ObstacleSpecialLaser(0,0,25, Color.WHITE);
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
    public void draw(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, radius, bPaint);

        Paint linePoint = new Paint();
        linePoint.setStyle(Paint.Style.FILL);
        linePoint.setColor(this.color);
        for(int i = 0; i < points.size(); i++) {
            linePoint.setAlpha((int)(50f * (i/100f)));
            canvas.drawCircle(points.get(i).x, points.get(i).y, radius, linePoint);
        }

        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    @Override
    public void update(PointF point) {
        center = point;
        rect.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        InGameArea = center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
    }

    public void update(){
        //Point currentPoint = new Point(center);
        center.x += (v.x * speed);
        center.y += (v.y * speed);
        //distance += Math.sqrt((center.y - currentPoint.y) * (center.y - currentPoint.y) + (center.x - currentPoint.x) * (center.x - currentPoint.x));
        rect.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);

        PointF p = new PointF();
        p.x = center.x;
        p.y = center.y;
        points.add(p);
        if (points.size() > 100) {
            points.remove(0);
        }

        InGameArea = center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
    }

    private void bounce(float x1, float y1, float x2, float y2) {
        //Start point of Wall line
        Vector2D collisionV1 = new Vector2D(x1, y1);
        //End point of Wall line
        Vector2D collisionV2 = new Vector2D(x2, y2);

        //Create Line Vector
        Vector2D surfaceVector = collisionV2.subtract(collisionV1);

        //Start point of intersecting line
        Vector2D velocityVector1 = new Vector2D(center.x,center.y);
        //End Point of the intersecting line
        Vector2D velocityVector2 = new Vector2D(center.x + (v.x * speed),center.y + (v.y * speed));
        //Create Line Vector
        Vector2D velocityVector = velocityVector2.subtract(velocityVector1);

        //Normalize the two surface vector
        surfaceVector = surfaceVector.normalize();

        //Get the Normal by flipping the X and Y and negate X
        Vector2D normal = new Vector2D( -1.0f * surfaceVector.y, surfaceVector.x);

        Vector2D bouncedVector = velocityVector.reflect(normal);
        v = bouncedVector.normalize();
    }

    //public float getDistance(){
    //    return distance;
    //}

    public void changeSpeedPercent(float percent){
        speed = (percent/100f * speed) + speed;
        if (speed > 25f) {
            speed = 25f;
        }
    }
    public void changeSpeedValue(float fixed){
        speed += fixed;
    }

    public void Collide(PointF obCenter, float obSize, String obType, boolean isGrowing) {
        RectF obRect;
        float obWidth;
        float obHeight;
        Vector2D circleLine;

        if (isGrowing) {
            changeSpeedPercent(10);
        }

        switch( obType ){
            case "Circle":
                circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                circleLine.rotate(90f);
                bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                break;
            case "Square":
                obRect = new RectF(obCenter.x - obSize,obCenter.y - obSize,obCenter.x + obSize, obCenter.y + obSize);
                if (center.y < obRect.top && center.x >= obRect.left && center.x <= obRect.right) {
                    //Top
                    bounce(obRect.left, obRect.top, obRect.right, obRect.top);
                    return;
                } else if (center.y > obRect.bottom && center.x >= obRect.left && center.x <= obRect.right)  {
                    //Bottom
                    bounce(obRect.left, obRect.bottom, obRect.right, obRect.bottom);
                    return;
                } else if (center.x < obRect.left && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Left
                    bounce(obRect.left, obRect.top, obRect.left , obRect.bottom);
                    if (v.x == 0) {v.x = -1.0f; v.normalize();}
                    return;
                } else if (center.x > obRect.right && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Right
                    bounce(obRect.right, obRect.top, obRect.right, obRect.bottom);
                    if (v.x == 0) {v.x = 1.0f; v.normalize();}
                    return;
                } else {
                    //Corner
                    if (center.x < obCenter.x && center.y < obCenter.y) {
                        circleLine = new Vector2D(obRect.left - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obRect.top,obRect.left + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.x > obCenter.x && center.y < obCenter.y) {
                        circleLine = new Vector2D(obRect.right - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obRect.top,obRect.right + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.x < obCenter.x && center.y > obCenter.y) {
                        circleLine = new Vector2D(obRect.left - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obRect.bottom,obRect.left + circleLine.x, obRect.bottom + circleLine.y);
                    } else if (center.x > obCenter.x && center.y > obCenter.y) {
                        circleLine = new Vector2D(obRect.right - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obRect.bottom,obRect.right + circleLine.x, obRect.bottom + circleLine.y);
                    } else {
                        circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                    }
                }
                break;
            case "Rectangle":
                obRect = new RectF(obCenter.x - obSize, obCenter.y - (obSize * 0.618f), obCenter.x + obSize, obCenter.y + (obSize * 0.618f) );
                if (center.y < obRect.top && center.x >= obRect.left && center.x <= obRect.right) {
                    //Top
                    bounce(obRect.left, obRect.top, obRect.right, obRect.top);
                    return;
                } else if (center.y > obRect.bottom && center.x >= obRect.left && center.x <= obRect.right)  {
                    //Bottom
                    bounce(obRect.left, obRect.bottom, obRect.right, obRect.bottom);
                    return;
                } else if (center.x < obRect.left && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Left
                    bounce(obRect.left, obRect.top, obRect.left , obRect.bottom);
                    if (v.x == 0) {v.x = -1.0f; v.normalize();}
                    return;
                } else if (center.x > obRect.right && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Right
                    bounce(obRect.right, obRect.top, obRect.right, obRect.bottom);
                    if (v.x == 0) {v.x = 1.0f; v.normalize();}
                    return;
                } else {
                    //Corner
                    if (center.x < obCenter.x && center.y < obCenter.y) {
                        circleLine = new Vector2D(obRect.left - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obRect.top,obRect.left + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.x > obCenter.x && center.y < obCenter.y) {
                        circleLine = new Vector2D(obRect.right - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obRect.top,obRect.right + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.x < obCenter.x && center.y > obCenter.y) {
                        circleLine = new Vector2D(obRect.left - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obRect.bottom,obRect.left + circleLine.x, obRect.bottom + circleLine.y);
                    } else if (center.x > obCenter.x && center.y > obCenter.y) {
                        circleLine = new Vector2D(obRect.right - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obRect.bottom,obRect.right + circleLine.x, obRect.bottom + circleLine.y);
                    } else {
                        circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                    }
                }
                break;
            case "TriangleUp":
                obWidth = obSize * 2.0f;
                obHeight = (float)(Math.sqrt((obWidth*obWidth) - (obWidth/2.0f)*(obWidth/2.0f)));
                obRect = new RectF(obCenter.x - obSize, obCenter.y - (obHeight / 2.0f), obCenter.x + obSize, obCenter.y + (obHeight / 2.0f) );

                if ((center.y > obCenter.y) && center.x >= (obCenter.x - obWidth) && center.x <= (obCenter.x + obWidth) ) {
                    //Bottom
                    bounce(obRect.left, obRect.bottom, obRect.right, obRect.bottom);
                    return;
                } else if (center.x < obCenter.x && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Left
                    bounce(obRect.left, obRect.bottom, obCenter.x, obRect.top);
                    return;
                } else if (center.x > obCenter.x && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Right
                    bounce(obRect.right, obRect.bottom, obCenter.x, obRect.top);
                    return;
                } else {
                    //Corner
                    if (rect.top < obRect.top) {
                        circleLine = new Vector2D(obCenter.x - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obRect.top,obCenter.x + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.x < obCenter.x && center.y > obCenter.y) {
                        circleLine = new Vector2D(obRect.left - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obRect.bottom,obRect.left + circleLine.x, obRect.bottom + circleLine.y);
                    } else if (center.x > obCenter.x && center.y > obCenter.y) {
                        circleLine = new Vector2D(obRect.right - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obRect.bottom,obRect.right + circleLine.x, obRect.bottom + circleLine.y);
                    } else {
                        circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                    }
                }

                break;
            case "TriangleDown":
                obWidth = obSize * 2.0f;
                obHeight = (float)(Math.sqrt((obWidth*obWidth) - (obWidth/2.0f)*(obWidth/2.0f)));
                obRect = new RectF(obCenter.x - obSize, obCenter.y - (obHeight / 2.0f), obCenter.x + obSize, obCenter.y + (obHeight / 2.0f) );

                if ((center.y < obCenter.y) && center.x >= (obCenter.x - obWidth) && center.x <= (obCenter.x + obWidth) ) {
                    //Top
                    bounce(obRect.left, obRect.top, obRect.right, obRect.top);
                    return;
                } else if (center.x <= obCenter.x && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Left
                    bounce(obRect.left, obRect.top, obCenter.x, obRect.bottom);
                    return;

                } else if (center.x > obCenter.x && center.y >= obRect.top && center.y <= obRect.bottom) {
                    //Right
                    bounce(obRect.right, obRect.top, obCenter.x, obRect.bottom);
                    return;
                } else {
                    //Corner
                    if (rect.bottom > obRect.bottom) {
                        circleLine = new Vector2D(obCenter.x - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obRect.bottom,obCenter.x + circleLine.x, obRect.bottom + circleLine.y);
                    } else if (center.x < obCenter.x && center.y < obCenter.y) {
                        circleLine = new Vector2D(obRect.left - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obRect.top,obRect.left + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.x > obCenter.x && center.y < obCenter.y) {
                        circleLine = new Vector2D(obRect.right - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obRect.top,obRect.right + circleLine.x, obRect.top + circleLine.y);
                    } else {
                        circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                    }
                }
                break;
            case "Rhombus":
                obWidth = obSize * 2.0f;
                obHeight = (float)(Math.sqrt((obWidth*obWidth) - (obWidth/2.0f)*(obWidth/2.0f))) * 2.0f;
                obRect = new RectF(obCenter.x - obSize, obCenter.y - (obHeight / 2.0f), obCenter.x + obSize, obCenter.y + (obHeight / 2.0f) );

                if (rect.right < obCenter.x && center.y <= obCenter.y ) {
                    //TopLeft
                    bounce(obRect.left, obCenter.y, obCenter.x, obRect.top);
                    return;
                } else if (rect.left > obCenter.x && center.y <= obCenter.y) {
                    //TopRight
                    bounce(obRect.right, obCenter.y, obCenter.x, obRect.top);
                    return;
                } else if (rect.right <= obCenter.x && center.y >= obCenter.y) {
                    //BottomLeft
                    bounce(obRect.left, obCenter.y, obCenter.x, obRect.bottom);
                    return;
                } else if (rect.left > obCenter.x && center.y >= obCenter.y) {
                    //BottomRight
                    bounce(obRect.right, obCenter.y, obCenter.x, obRect.bottom);
                    return;
                } else {
                    //Corner
                    if (center.y < obRect.top) {
                        circleLine = new Vector2D(obCenter.x - center.x, obRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obRect.top,obCenter.x + circleLine.x, obRect.top + circleLine.y);
                    } else if (center.y > obRect.bottom) {
                        circleLine = new Vector2D(obCenter.x - center.x, obRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obRect.bottom,obCenter.x + circleLine.x, obRect.bottom + circleLine.y);
                    } else if (center.x < obRect.left) {
                        circleLine = new Vector2D(obRect.left - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.left,obCenter.y,obRect.left + circleLine.x, obCenter.y + circleLine.y);
                    } else if (center.x > obRect.right) {
                        circleLine = new Vector2D(obRect.right - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obRect.right,obCenter.y,obRect.right + circleLine.x, obCenter.y + circleLine.y);
                    } else {
                        circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                    }
                }
                break;
            case "Hexagon":
                obWidth = obSize * 2.0f;
                obHeight = (float)(Math.sqrt((obWidth*obWidth) - (obSize)*(obSize)));

                PointF hexLeft = new PointF((obCenter.x - obSize), obCenter.y );
                PointF hexTopLeft = new PointF(obCenter.x - (0.5f * obSize), (obCenter.y - (obHeight/2.0f)));
                PointF hexTopRight = new PointF(obCenter.x + (0.5f * obSize), (obCenter.y - (obHeight/2.0f)));
                PointF hexRight = new PointF((obCenter.x + obSize), obCenter.y );
                PointF hexBottomRight = new PointF(obCenter.x + (0.5f * obSize), (obCenter.y + (obHeight/2.0f)));
                PointF hexBottomLeft = new PointF(obCenter.x - (0.5f * obSize), (obCenter.y + (obHeight/2.0f)));

                obRect = new RectF(obCenter.x - obSize, obCenter.y - (obHeight / 2.0f), obCenter.x + obSize, obCenter.y + (obHeight / 2.0f) );

                if (center.y < obRect.top && center.x >= hexTopLeft.x && center.x <= hexTopRight.x) {
                    //Top
                    bounce(hexTopLeft.x, hexTopLeft.y, hexTopRight.x, hexTopRight.y);
                    return;
                } else if (center.y > obRect.bottom && center.x >= hexBottomLeft.x && center.x <= hexBottomRight.x) {
                    //Bottom
                    bounce(hexBottomLeft.x, hexBottomLeft.y, hexBottomRight.x, hexBottomRight.y);
                    return;
                } else if (center.x < obCenter.x && rect.bottom <= obCenter.y && rect.top >= obRect.top) {
                    //TopLeft
                    bounce(hexLeft.x, hexLeft.y, hexTopLeft.x, hexTopLeft.y);
                    return;
                } else if (center.x > obCenter.x && rect.bottom <= obCenter.y && rect.top >= obRect.top) {
                    //TopRight
                    bounce(hexRight.x, hexRight.y, hexTopRight.x, hexTopRight.y);
                    return;
                } else if (center.x < obCenter.x && rect.top >= obCenter.y && rect.bottom <= obRect.bottom) {
                    //BottomLeft
                    bounce(hexLeft.x, hexLeft.y, hexBottomLeft.x, hexBottomLeft.y);
                    return;
                } else if (center.x > obCenter.x && rect.top >= obCenter.y && rect.bottom <= obRect.bottom) {
                    //BottomRight
                    bounce(hexRight.x, hexRight.y, hexBottomRight.x, hexBottomRight.y);
                    return;
                } else {
                    //Corner
                    if (center.y < obRect.top && center.x < obCenter.x) {
                        circleLine = new Vector2D(hexTopLeft.x - center.x, hexTopLeft.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexTopLeft.x,hexTopLeft.y,hexTopLeft.x + circleLine.x, hexTopLeft.y + circleLine.y);
                    } else if (center.y < obRect.top && center.x > obCenter.x) {
                        circleLine = new Vector2D(hexTopRight.x - center.x, hexTopRight.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexTopRight.x,hexTopRight.y,hexTopRight.x + circleLine.x, hexTopRight.y + circleLine.y);
                    } else if (center.y > obRect.bottom && center.x < obCenter.x) {
                        circleLine = new Vector2D(hexBottomLeft.x - center.x, hexBottomLeft.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexBottomLeft.x,hexBottomLeft.y,hexBottomLeft.x+ circleLine.x, hexBottomLeft.y + circleLine.y);
                    } else if (center.y > obRect.bottom && center.x > obCenter.x) {
                        circleLine = new Vector2D(hexBottomRight.x - center.x, hexBottomRight.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexBottomRight.x,hexBottomRight.y,hexBottomRight.x + circleLine.x, hexBottomRight.y + circleLine.y);
                    } else if (center.x < obRect.left) {
                        circleLine = new Vector2D(hexLeft.x - center.x, hexLeft.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexLeft.x,hexLeft.y,hexLeft.x + circleLine.x, hexLeft.y + circleLine.y);
                    } else if (center.x > obRect.right) {
                        circleLine = new Vector2D(hexRight.x - center.x, hexRight.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexRight.x,hexRight.y,hexRight.x + circleLine.x, hexRight.y + circleLine.y);
                    } else {
                        circleLine = new Vector2D(obCenter.x - center.x, obCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(obCenter.x,obCenter.y,obCenter.x + circleLine.x, obCenter.y + circleLine.y);
                    }
                }
                break;
        }
    }
}