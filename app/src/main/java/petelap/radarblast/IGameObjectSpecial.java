package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public interface IGameObjectSpecial {
    void draw(Canvas canvas);
    void update(PointF playerPoint);
    IGameObjectSpecial NewInstance();
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
}