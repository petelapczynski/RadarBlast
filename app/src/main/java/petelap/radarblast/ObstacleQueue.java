package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Pete on 4/24/2018.
 */

public class ObstacleQueue {
    private ArrayList<String> queue;

    public ObstacleQueue(int count) {
        queue = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            queue.add( randomObj() );
        }
    }

    public void addItem(){
        queue.add( randomObj() );
    }

    public void addItem(String item){
        for (int i=0; i < item.length(); i++) {
            String c = item.charAt(i) + "";
            queue.add(c);
        }
    }

    public void removeItem(){
        if(!queue.isEmpty()){
            queue.remove(0);
        }
    }

    public IGameObject getItem(){
        if (!queue.isEmpty()) {
            String item = queue.get(0);
            // C = Circle, S = Square, T = TriangleUp, I = TriangleDown, R = Rhombus
            switch (item) {
                case "C":
                    // Circle
                    return ObstacleCircle.GetInstance();
                case "S":
                    // Square
                    return ObstacleSquare.GetInstance();
                case "T":
                    // Triangle Up
                    return ObstacleTriangleUp.GetInstance();
                case "I":
                    // Triangle Down
                    return ObstacleTriangleDown.GetInstance();
                case "R":
                    // Rhombus
                    return ObstacleRhombus.GetInstance();
            }
        }
        return null;
    }

    public void draw(Canvas canvas) {
        // last3 holds randomly created objects C = Circle, S = Square, T = Triangle
        // Array position i assigns drawn queue from right to left in the header
        // point is center of drawn queue object
        if(queue.isEmpty()){return;}

        Paint paint = new Paint();
        Point point = new Point();
        point.y = Constants.HEADER_HEIGHT / 2;
        Path path;

        for(int i=0; i < queue.size() && i < 4; i++) {
            int size = 75 - (i*18);
            String item = queue.get(i);
            point.x = Constants.SCREEN_WIDTH - 100 - (i * 200 );
            switch(item) {
                case "C":
                    // Circle
                    paint.setStyle(Paint.Style.FILL);
                    paint.setARGB( 255,0,0,255);
                    canvas.drawCircle(point.x, point.y,size, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(point.x, point.y,size, paint);
                    break;
                case "S":
                    // Square
                    paint.setStyle(Paint.Style.FILL);
                    paint.setARGB(255,255,0,0);
                    canvas.drawRect(point.x - size, point.y - size, point.x + size, point.y + size, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(point.x - size, point.y - size, point.x + size, point.y + size, paint);
                    break;
                case "T":
                    // TriangleUp
                    path = new Path();
                    path.setFillType(Path.FillType.EVEN_ODD);
                    path.moveTo(point.x - size, point.y + size);
                    path.lineTo(point.x, point.y - size);
                    path.lineTo(point.x + size, point.y + size);
                    path.close();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setARGB(255,255,255,0);
                    canvas.drawPath(path, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawPath(path, paint);
                    break;
                case "I":
                    // TriangleDown
                    path = new Path();
                    path.setFillType(Path.FillType.EVEN_ODD);
                    path.moveTo(point.x - size, point.y - size);
                    path.lineTo(point.x, point.y + size);
                    path.lineTo(point.x + size, point.y - size);
                    path.close();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setARGB(255,255,255,0);
                    canvas.drawPath(path, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawPath(path, paint);
                    break;
                case "R":
                    // Rhombus
                    path = new Path();
                    path.setFillType(Path.FillType.EVEN_ODD);
                    path.moveTo(point.x - (int)(size * .75), point.y);
                    path.lineTo(point.x, point.y - size);
                    path.lineTo(point.x + (int)(size * .75), point.y);
                    path.lineTo(point.x, point.y + size);
                    path.close();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setARGB(255,255,140,0);
                    canvas.drawPath(path, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawPath(path, paint);
                    break;
            }
        }
    }

    private String randomObj() {
        // Random between, inclusive of high = random.nextInt(high - low +1) + low;
        int randomNum = Common.randomInt(1,5);
        switch (randomNum) {
            case 1:
                // Circle
                return "C";
            case 2:
                // Square
                return "S";
            case 3:
                // Triangle Up
                return "T";
            case 4:
                // Triangle Down
                return "I";
            case 5:
                // Rhombus
                return "R";
        }
        return "";
    }

//    private void drawTriangle(Point center, int width, int height, boolean inverted, Paint paint, Canvas canvas){
//        Point Left;
//        Point Center;
//        Point Right;
//        if(inverted) {
//            Left = new Point(center.x - (width/2), center.y - (height/2) );
//            Center = new Point(center.x, center.y + (height/2) );
//            Right = new Point(center.x + (width/2), center.y - (height/2) );
//        } else {
//            Left = new Point(center.x - (width/2), center.y + (height/2) );
//            Center = new Point(center.x, center.y - (height/2) );
//            Right = new Point(center.x + (width/2), center.y + (height/2) );
//        }
//
//        Path path = new Path();
//        path.setFillType(Path.FillType.EVEN_ODD);
//        path.moveTo(Left.x, Left.y);
//        path.lineTo(Center.x, Center.y);
//        path.lineTo(Right.x, Right.y);
//        path.close();
//
//        canvas.drawPath(path, paint);
//    }

}
