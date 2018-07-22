package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Point;

/**
 * Created by Pete on 3/23/2018.
 */

public interface IGameObject {
    void draw(Canvas canvas);
    void update(Point playerPoint);
    void grow(float speed);
    IGameObject NewInstance();
    boolean InGameArea();
    boolean pointInside(Point playerPoint);
    void pop();
    float getArea();

    // Center of object
    Point getCenter();
    // Half the object width
    float getSize();
    // Type of object
    String getType();
    // obstacleCollide
    boolean CollideCircle(Point obCenter, float obSize);
    boolean CollideSquare(Point obCenter, float obSize);
    boolean CollideTriangleUp(Point obCenter, float obSize);
    boolean CollideTriangleDown(Point obCenter, float obSize);
    boolean CollideRhombus(Point obCenter, float obSize);
    boolean CollideHexagon(Point obCenter, float obSize);
}
