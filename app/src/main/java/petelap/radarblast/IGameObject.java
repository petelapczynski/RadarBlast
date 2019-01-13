package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

public interface IGameObject {
    void draw(Canvas canvas);
    void update(PointF playerPoint);
    void grow(float speed);
    void setPaint(Paint ovrPaint);
    IGameObject NewInstance();
    boolean InGameArea();
    boolean pointInside(PointF playerPoint);
    void pop();
    float getArea();
    // Center of object
    PointF getCenter();
    // Half the object width
    float getSize();
    // Type of object
    String getType();
    // Bounding box
    RectF getBoundsRect();
    // Bounding Points
    ArrayList<PointF> getPoints();
}