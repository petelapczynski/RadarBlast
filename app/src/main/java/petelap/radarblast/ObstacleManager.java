package petelap.radarblast;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class ObstacleManager {
    //higher index = lower on screen = higher y value
    private ArrayList<Obstacle> obstacles;

    public ObstacleManager() {
        obstacles = new ArrayList<>();
    }

    public ObstacleManager(List<Levels.Level.LevelObjects> lvlObjs, int color) {
        obstacles = new ArrayList<>();
        // Create obstacles to avoid
        if(lvlObjs != null) {
            for(Levels.Level.LevelObjects lo: lvlObjs) {
                // Random between = random.nextInt(high - low+1) + low;
                int startX = (int)((float)Constants.SCREEN_WIDTH * ((float)lo.getPosX() / 100f));
                int startY = (int)(Constants.HEADER_HEIGHT + ((float)(Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT) * ((float)lo.getPosY() / 100f)) );
                float obstacleHeight = (float)lo.getHeight();
                float obstacleWidth = (float)lo.getWidth();
                obstacles.add( new Obstacle(obstacleHeight, obstacleWidth, startX, startY, color) );
            }
        } else {
            int obstacleCount = Common.randomInt(0,3);
            float obstacleHeight = Common.randomFlt(100, 200);
            float obstacleWidth = Common.randomFlt(100, 200);
            for(int i = 1; i <= obstacleCount; i++) {
                // Random between = random.nextInt(high - low+1) + low;
                int startX = Common.randomInt((int)obstacleHeight, (int)(Constants.SCREEN_WIDTH - obstacleHeight));
                int startY = Common.randomInt((int)(Constants.HEADER_HEIGHT + obstacleHeight), (int)(Constants.SCREEN_HEIGHT - obstacleHeight));
                obstacles.add( new Obstacle(obstacleHeight, obstacleWidth, startX, startY, color) );
            }
        }
    }

    public void addObstacle(float obstacleHeight, float obstacleWidth, int startX, int startY, int color){
        obstacles.add( new Obstacle(obstacleHeight, obstacleWidth, startX, startY, color) );
    }

    public boolean obstacleManagerCollide(IGameObject gob ) {
        for (Obstacle ob: obstacles) {
            if (CollisionManager.GameObjectCollide(gob, ob)) {
             return true;
            }
        }
        return false;
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

    public int getCount() {
        return obstacles.size();
    }
}