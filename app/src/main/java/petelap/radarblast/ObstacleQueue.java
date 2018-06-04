package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pete on 4/24/2018.
 */

public class ObstacleQueue {
    private ArrayList<String> queue;

    public ObstacleQueue(int count) {
        queue = new ArrayList<>();
        if(count <= 0) {count = 1;}
        for(int i = 0; i < count; i++) {
            queue.add( randomObj(1,3) );
        }
    }

    public void addItem(){
        queue.add( randomObj(1,3) );
    }

    public void removeItem(){
        if(!queue.isEmpty()){
            queue.remove(0);
        }
    }

    public IGameObject getItem(){
        if (!queue.isEmpty()) {
            String item = queue.get(0);
            // C = Circle, S = Square, T = Triangle
            switch (item) {
                case "C":
                    // Circle
                    return ObstacleCircle.GetInstance();
                case "S":
                    // Square
                    return ObstacleSquare.GetInstance();
                case "T":
                    // Triangle
                    return ObstacleTriangle.GetInstance();
            }
        }
        return null;
    }

    public void update(){}

    public void draw(Canvas canvas) {
        // last3 holds randomly created objects C = Circle, S = Square, T = Triangle
        // Array position i assigns drawn queue from right to left in the header
        // point is center of drawn queue object
        if(queue.isEmpty()){return;}

        Paint paint = new Paint();
        Point point = new Point();
        point.y = Constants.HEADER_HEIGHT / 2;

        for(int i=0; i < queue.size() && i < 3; i++) {

            String item = queue.get(i);
            point.x = Constants.SCREEN_WIDTH - 100 - (i * 200 );
            switch(item) {
                case "C":
                    // Circle
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.BLUE);
                    canvas.drawCircle(point.x, point.y,75, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(point.x, point.y,75, paint);
                    break;
                case "S":
                    // Square
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.RED);
                    canvas.drawRect(point.x - 75, point.y - 75, point.x + 75, point.y + 75, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(point.x - 75, point.y - 75, point.x + 75, point.y + 75, paint);
                    break;
                case "T":
                    // Triangle
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.YELLOW);
                    drawTriangle(point, 150, 150, false, paint, canvas);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    drawTriangle(point, 150, 150, false, paint, canvas);
                    break;
            }
        }
    }

    private String randomObj(int low, int high) {
        // Random between, inclusive of high = random.nextInt(high - low +1) + low;
        Random random = new Random();
        int randomNum = random.nextInt((high - low)+1) + low;
        switch (randomNum) {
            case 1:
                // Circle
                return "C";
            case 2:
                // Square
                return "S";
            case 3:
                // Triangle
                return "T";
        }
        return "";
    }

    private void drawTriangle(Point center, int width, int height, boolean inverted, Paint paint, Canvas canvas){
        Point bottomLeft = new Point(center.x - (width/2), center.y + (height/2) );
        Point topCenter = new Point(center.x, center.y - (height/2) );
        Point bottomRight = new Point(center.x + (width/2), center.y + (height/2) );

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(bottomLeft.x, bottomLeft.y);
        path.lineTo(topCenter.x, topCenter.y);
        path.lineTo(bottomRight.x, bottomRight.y);
        path.close();

        canvas.drawPath(path, paint);
    }

}
