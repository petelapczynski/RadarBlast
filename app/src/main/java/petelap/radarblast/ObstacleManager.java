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

    public boolean obstacleManagerCollide(IGameObject gameob ) {
        for (Obstacle ob: obstacles) {
            switch ( gameob.getType() ) {
                case "Circle": return ob.obstacleCollideCircle(gameob.getCenter(), gameob.getSize() );
                case "Square": return ob.obstacleCollideSquare(gameob.getCenter(), gameob.getSize() );
                case "Triangle": return ob.obstacleCollideTriangle(gameob.getCenter(), gameob.getSize() );
                default: return false;
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

    public void update() {}

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
