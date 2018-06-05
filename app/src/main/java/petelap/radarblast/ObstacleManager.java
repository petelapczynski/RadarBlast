package petelap.radarblast;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pete on 3/23/2018.
 */

public class ObstacleManager {
    //higher index = lower on screen = higher y value
    private ArrayList<Obstacle> obstacles;
    private int obstacleCount;
    private float obstacleHeight;
    private int color;

    public ObstacleManager(int obstacleCount, float obstacleHeight, int color) {
        this.obstacleCount = obstacleCount;
        this.obstacleHeight = obstacleHeight;
        this.color = color;
        obstacles = new ArrayList<>();
        populateObstacles();
    }

    public boolean obstacleManagerCollide(IGameObject gob ) {
        for (Obstacle ob: obstacles) {
            switch ( gob.getType() ) {
                case "Circle":
                    if(ob.CollideCircle(gob.getCenter(), gob.getSize() )) {return true;}
                    break;
                case "Square":
                    if(ob.CollideSquare(gob.getCenter(), gob.getSize() )) {return true;}
                    break;
                case "TriangleUp":
                    if (ob.CollideTriangleUp(gob.getCenter(), gob.getSize() )) {return true;}
                    break;
                case "TriangleDown":
                    if (ob.CollideTriangleDown(gob.getCenter(), gob.getSize() )) {return true;}
                    break;
                case "Rhombus":
                    if (ob.CollideRhombus(gob.getCenter(), gob.getSize() )) {return true;}
                    break;
            }
        }
        return false;
    }

    private void populateObstacles() {
        // Create obstacles to avoid
        for(int i = 1; i <= obstacleCount; i++) {
            // Random between = random.nextInt(high - low+1) + low;
            Random random = new Random();
            int startX = random.nextInt((Constants.SCREEN_WIDTH - (int)obstacleHeight) - (int)obstacleHeight) + (int)obstacleHeight;
            int startY = random.nextInt((Constants.SCREEN_HEIGHT - (int)obstacleHeight) - (Constants.HEADER_HEIGHT + (int)obstacleHeight)) + (Constants.HEADER_HEIGHT + (int)obstacleHeight);
            obstacles.add( new Obstacle(obstacleHeight, startX, startY, color) );
        }
    }

    public void draw(Canvas canvas) {
        for(Obstacle ob : obstacles) {
            ob.draw(canvas);
        }
    }

    public float getArea() {
        float area = 0.0f;
        for(Obstacle ob : obstacles) {
            area += ob.getArea();
        }
        return area;
    }

}
