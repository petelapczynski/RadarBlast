package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.SweepGradient;

/**
 * Created by Pete on 3/23/2018.
 */

public interface IGameObject {
    void draw(Canvas canvas);
    void update();
    void update(Point playerPoint);
    void grow(float speed);
    IGameObject NewInstance();
    boolean InGameArea();
    float getArea();

    // Center of object
    Point getCenter();
    // Half the object width
    float getSize();
    // Type of object
    String getType();
    // obstacleCollide
    boolean obstacleCollideCircle(Point obCenter, float obSize);
    boolean obstacleCollideSquare(Point obCenter, float obSize);
    boolean obstacleCollideTriangle(Point obCenter, float obSize);
}
