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
        v = new Vector2D(0f,1f);
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

    public void changeSpeedPercent(float percent){
        speed = (percent/100f * speed) + speed;
        if (speed > 25f) {
            speed = 25f;
        }
    }
    public void changeSpeedValue(float fixed){
        speed += fixed;
    }

    public void Collide(IGameObject obj, boolean isGrowing) {
        Vector2D circleLine;
        PointF objCenter = obj.getCenter();
        RectF objRect = obj.getBoundsRect();

        if (isGrowing) {
            changeSpeedPercent(10);
        }

        switch( obj.getType() ){
            case "Circle":
                circleLine = new Vector2D(objCenter.x - center.x, objCenter.y - center.y);
                circleLine.rotate(90f);
                bounce(objCenter.x,objCenter.y,objCenter.x + circleLine.x, objCenter.y + circleLine.y);

                if (isGrowing) {
                    if (center.y < objRect.top && rect.right >= objRect.left && rect.left <= objRect.right) {
                        //Top
                        if (v.y >= 0) {
                            v.y *= -1.0f;
                        }
                    } else if (center.y > objRect.bottom && rect.right >= objRect.left && rect.left <= objRect.right) {
                        //Bottom
                        if (v.y <= 0) {
                            v.y *= -1.0f;
                        }
                    } else if (center.x < objRect.left && rect.bottom >= objRect.top && rect.top <= objRect.bottom) {
                        //Left
                        if (v.x >= 0) {
                            v.x *= -1.0f;
                        }
                    } else if (center.x > objRect.right && rect.bottom >= objRect.top && rect.top <= objRect.bottom) {
                        //Right
                        if (v.x <= 0) {
                            v.x *= -1.0f;
                        }
                    }
                    return;
                }
                break;
            case "Square":
            case "Rectangle":
                if (center.y < objRect.top && center.x >= objRect.left && center.x <= objRect.right) {
                    //Top
                    bounce(objRect.left, objRect.top, objRect.right, objRect.top);
                    if (isGrowing && v.y >= 0) {
                        v.y *= -1.0f;
                    }
                    return;
                } else if (center.y > objRect.bottom && center.x >= objRect.left && center.x <= objRect.right)  {
                    //Bottom
                    bounce(objRect.left, objRect.bottom, objRect.right, objRect.bottom);
                    if (isGrowing && v.y <= 0) {
                        v.y *= -1.0f;
                    }
                    return;
                } else if (center.x < objRect.left && center.y >= objRect.top && center.y <= objRect.bottom) {
                    //Left
                    bounce(objRect.left, objRect.top, objRect.left , objRect.bottom);
                    if (isGrowing && v.x > 0) {
                        v.x *= -1.0f;
                    } else if (isGrowing && v.x == 0) {
                        v.x = -1.0f;
                        v.normalize();
                    }
                    return;
                } else if (center.x > objRect.right && center.y >= objRect.top && center.y <= objRect.bottom) {
                    //Right
                    bounce(objRect.right, objRect.top, objRect.right, objRect.bottom);
                    if (isGrowing && v.x < 0) {
                        v.x *= -1.0f;
                    } else if (isGrowing && v.x == 0) {
                        v.x = 1.0f;
                        v.normalize();
                    }
                    return;
                } else {
                    //Corner
                    if (center.x < objCenter.x && center.y < objCenter.y) {
                        circleLine = new Vector2D(objRect.left - center.x, objRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.left,objRect.top,objRect.left + circleLine.x, objRect.top + circleLine.y);
                    } else if (center.x > objCenter.x && center.y < objCenter.y) {
                        circleLine = new Vector2D(objRect.right - center.x, objRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.right,objRect.top,objRect.right + circleLine.x, objRect.top + circleLine.y);
                    } else if (center.x < objCenter.x && center.y > objCenter.y) {
                        circleLine = new Vector2D(objRect.left - center.x, objRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.left,objRect.bottom,objRect.left + circleLine.x, objRect.bottom + circleLine.y);
                    } else if (center.x > objCenter.x && center.y > objCenter.y) {
                        circleLine = new Vector2D(objRect.right - center.x, objRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.right,objRect.bottom,objRect.right + circleLine.x, objRect.bottom + circleLine.y);
                    } else {
                        circleLine = new Vector2D(objCenter.x - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objCenter.y,objCenter.x + circleLine.x, objCenter.y + circleLine.y);
                    }
                }
                break;
            case "TriangleUp":
                if ((center.y > objRect.bottom) && center.x >= objRect.left && center.x <= objRect.right ) {
                    //Bottom
                    bounce(objRect.left, objRect.bottom, objRect.right, objRect.bottom);
                    if (isGrowing && v.y <= 0) {
                        v.y *= -1.0f;
                    }
                    return;
                } else if (center.x < objCenter.x && center.y >= objRect.top && center.y <= objRect.bottom) {
                    //Left
                    bounce(objRect.left, objRect.bottom, objCenter.x, objRect.top);
                    if (isGrowing && v.x >= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (center.x > objCenter.x && center.y >= objRect.top && center.y <= objRect.bottom) {
                    //Right
                    bounce(objRect.right, objRect.bottom, objCenter.x, objRect.top);
                    if (isGrowing && v.x <= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else {
                    //Corner
                    if (rect.top < objRect.top) {
                        circleLine = new Vector2D(objCenter.x - center.x, objRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objRect.top,objCenter.x + circleLine.x, objRect.top + circleLine.y);
                    } else if (center.x < objCenter.x && center.y > objCenter.y) {
                        circleLine = new Vector2D(objRect.left - center.x, objRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.left,objRect.bottom,objRect.left + circleLine.x, objRect.bottom + circleLine.y);
                    } else if (center.x > objCenter.x && center.y > objCenter.y) {
                        circleLine = new Vector2D(objRect.right - center.x, objRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.right,objRect.bottom,objRect.right + circleLine.x, objRect.bottom + circleLine.y);
                    } else {
                        circleLine = new Vector2D(objCenter.x - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objCenter.y,objCenter.x + circleLine.x, objCenter.y + circleLine.y);
                    }
                }
                break;
            case "TriangleDown":
                if ((center.y < objRect.top) && center.x >= objRect.left && center.x <= objRect.right ) {
                    //Top
                    bounce(objRect.left, objRect.top, objRect.right, objRect.top);
                    if (isGrowing && v.y >= 0) {
                        v.y *= -1.0f;
                    }
                    return;
                } else if (center.x <= objCenter.x && center.y >= objRect.top && center.y <= objRect.bottom) {
                    //Left
                    bounce(objRect.left, objRect.top, objCenter.x, objRect.bottom);
                    if (isGrowing && v.x >= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (center.x > objCenter.x && center.y >= objRect.top && center.y <= objRect.bottom) {
                    //Right
                    bounce(objRect.right, objRect.top, objCenter.x, objRect.bottom);
                    if (isGrowing && v.x <= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else {
                    //Corner
                    if (rect.bottom > objRect.bottom) {
                        circleLine = new Vector2D(objCenter.x - center.x, objRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objRect.bottom,objCenter.x + circleLine.x, objRect.bottom + circleLine.y);
                    } else if (center.x < objCenter.x && center.y < objCenter.y) {
                        circleLine = new Vector2D(objRect.left - center.x, objRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.left,objRect.top,objRect.left + circleLine.x, objRect.top + circleLine.y);
                    } else if (center.x > objCenter.x && center.y < objCenter.y) {
                        circleLine = new Vector2D(objRect.right - center.x, objRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.right,objRect.top,objRect.right + circleLine.x, objRect.top + circleLine.y);
                    } else {
                        circleLine = new Vector2D(objCenter.x - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objCenter.y,objCenter.x + circleLine.x, objCenter.y + circleLine.y);
                    }
                }
                break;
            case "Rhombus":
                if (rect.right < objCenter.x && center.y <= objCenter.y ) {
                    //TopLeft
                    bounce(objRect.left, objCenter.y, objCenter.x, objRect.top);
                    if (isGrowing && v.x >= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (rect.left > objCenter.x && center.y <= objCenter.y) {
                    //TopRight
                    bounce(objRect.right, objCenter.y, objCenter.x, objRect.top);
                    if (isGrowing && v.x <= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (rect.right <= objCenter.x && center.y >= objCenter.y) {
                    //BottomLeft
                    bounce(objRect.left, objCenter.y, objCenter.x, objRect.bottom);
                    if (isGrowing && v.x >= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (rect.left > objCenter.x && center.y >= objCenter.y) {
                    //BottomRight
                    bounce(objRect.right, objCenter.y, objCenter.x, objRect.bottom);
                    if (isGrowing && v.x <= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else {
                    //Corner
                    if (center.y < objRect.top) {
                        circleLine = new Vector2D(objCenter.x - center.x, objRect.top - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objRect.top,objCenter.x + circleLine.x, objRect.top + circleLine.y);
                    } else if (center.y > objRect.bottom) {
                        circleLine = new Vector2D(objCenter.x - center.x, objRect.bottom - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objRect.bottom,objCenter.x + circleLine.x, objRect.bottom + circleLine.y);
                    } else if (center.x < objRect.left) {
                        circleLine = new Vector2D(objRect.left - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.left,objCenter.y,objRect.left + circleLine.x, objCenter.y + circleLine.y);
                    } else if (center.x > objRect.right) {
                        circleLine = new Vector2D(objRect.right - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objRect.right,objCenter.y,objRect.right + circleLine.x, objCenter.y + circleLine.y);
                    } else {
                        circleLine = new Vector2D(objCenter.x - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objCenter.y,objCenter.x + circleLine.x, objCenter.y + circleLine.y);
                    }
                }
                break;
            case "Hexagon":
                PointF hexLeft = obj.getPoints().get(0);
                PointF hexTopLeft = obj.getPoints().get(1);
                PointF hexTopRight = obj.getPoints().get(2);
                PointF hexRight = obj.getPoints().get(3);
                PointF hexBottomRight = obj.getPoints().get(4);
                PointF hexBottomLeft = obj.getPoints().get(5);

                if (center.y < objRect.top && center.x >= hexTopLeft.x && center.x <= hexTopRight.x) {
                    //Top
                    bounce(hexTopLeft.x, hexTopLeft.y, hexTopRight.x, hexTopRight.y);
                    if (isGrowing && v.y >= 0) {
                        v.y *= -1.0f;
                    }
                    return;
                } else if (center.y > objRect.bottom && center.x >= hexBottomLeft.x && center.x <= hexBottomRight.x) {
                    //Bottom
                    bounce(hexBottomLeft.x, hexBottomLeft.y, hexBottomRight.x, hexBottomRight.y);
                    if (isGrowing && v.y <= 0) {
                        v.y *= -1.0f;
                    }
                    return;
                } else if (center.x < objCenter.x && rect.bottom <= objCenter.y && rect.top >= objRect.top) {
                    //TopLeft
                    bounce(hexLeft.x, hexLeft.y, hexTopLeft.x, hexTopLeft.y);
                    if (isGrowing && v.x >= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (center.x > objCenter.x && rect.bottom <= objCenter.y && rect.top >= objRect.top) {
                    //TopRight
                    bounce(hexRight.x, hexRight.y, hexTopRight.x, hexTopRight.y);
                    if (isGrowing && v.x <= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (center.x < objCenter.x && rect.top >= objCenter.y && rect.bottom <= objRect.bottom) {
                    //BottomLeft
                    bounce(hexLeft.x, hexLeft.y, hexBottomLeft.x, hexBottomLeft.y);
                    if (isGrowing && v.x >= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else if (center.x > objCenter.x && rect.top >= objCenter.y && rect.bottom <= objRect.bottom) {
                    //BottomRight
                    bounce(hexRight.x, hexRight.y, hexBottomRight.x, hexBottomRight.y);
                    if (isGrowing && v.x <= 0) {
                        v.x *= -1.0f;
                    }
                    return;
                } else {
                    //Corner
                    if (center.y < objRect.top && center.x < objCenter.x) {
                        circleLine = new Vector2D(hexTopLeft.x - center.x, hexTopLeft.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexTopLeft.x,hexTopLeft.y,hexTopLeft.x + circleLine.x, hexTopLeft.y + circleLine.y);
                    } else if (center.y < objRect.top && center.x > objCenter.x) {
                        circleLine = new Vector2D(hexTopRight.x - center.x, hexTopRight.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexTopRight.x,hexTopRight.y,hexTopRight.x + circleLine.x, hexTopRight.y + circleLine.y);
                    } else if (center.y > objRect.bottom && center.x < objCenter.x) {
                        circleLine = new Vector2D(hexBottomLeft.x - center.x, hexBottomLeft.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexBottomLeft.x,hexBottomLeft.y,hexBottomLeft.x+ circleLine.x, hexBottomLeft.y + circleLine.y);
                    } else if (center.y > objRect.bottom && center.x > objCenter.x) {
                        circleLine = new Vector2D(hexBottomRight.x - center.x, hexBottomRight.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexBottomRight.x,hexBottomRight.y,hexBottomRight.x + circleLine.x, hexBottomRight.y + circleLine.y);
                    } else if (center.x < objRect.left) {
                        circleLine = new Vector2D(hexLeft.x - center.x, hexLeft.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexLeft.x,hexLeft.y,hexLeft.x + circleLine.x, hexLeft.y + circleLine.y);
                    } else if (center.x > objRect.right) {
                        circleLine = new Vector2D(hexRight.x - center.x, hexRight.y - center.y);
                        circleLine.rotate(90f);
                        bounce(hexRight.x,hexRight.y,hexRight.x + circleLine.x, hexRight.y + circleLine.y);
                    } else {
                        circleLine = new Vector2D(objCenter.x - center.x, objCenter.y - center.y);
                        circleLine.rotate(90f);
                        bounce(objCenter.x,objCenter.y,objCenter.x + circleLine.x, objCenter.y + circleLine.y);
                    }
                }
                break;
        }
    }
}