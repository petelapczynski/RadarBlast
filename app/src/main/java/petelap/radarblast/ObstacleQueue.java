package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.ArrayList;

public class ObstacleQueue {
    private ArrayList<String> queue;
    private Paint paint;
    private Paint bPaint;
    private PointF point;
    private boolean bAnimation;
    private long startTime;

    public ObstacleQueue(){
        queue = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        bPaint.setAntiAlias(true);

        point = new PointF();
        point.y = Constants.HEADER_HEIGHT / 2;

        bAnimation = false;
    }

    public ObstacleQueue(int count) {
        queue = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            queue.add( randomObj() );
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        bPaint.setAntiAlias(true);

        point = new PointF();
        point.y = Constants.HEADER_HEIGHT / 2;
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
            startTime = System.currentTimeMillis();
            bAnimation = true;
        }
    }

    public IGameObject getItem(){
        if (!queue.isEmpty()) {
            String item = queue.get(0);
            // C = Circle, S = Square, E = Rectangle, T = TriangleUp, I = TriangleDown, R = Rhombus, H = Hexagon
            switch (item) {
                case "C":
                    // Circle
                    return ObstacleCircle.GetInstance();
                case "S":
                    // Square
                    return ObstacleSquare.GetInstance();
                case "E":
                    // Rectangle
                    return ObstacleRectangle.GetInstance();
                case "T":
                    // Triangle Up
                    return ObstacleTriangleUp.GetInstance();
                case "I":
                    // Triangle Down
                    return ObstacleTriangleDown.GetInstance();
                case "R":
                    // Rhombus
                    return ObstacleRhombus.GetInstance();
                case "H":
                    // Hexagon
                    return ObstacleHexagon.GetInstance();
            }
        }
        return null;
    }

    public int getCount() {
        return queue.size();
    }

    public void draw(Canvas canvas) {
        // Array position i assigns drawn queue from right to left in the header
        // point is center of drawn queue object
        if(queue.isEmpty()){return;}

        Path path;

        for(int i=0; i < queue.size() && i < 5; i++) {
            String item = queue.get(i);
            float size;
            if (bAnimation) {
                if (System.currentTimeMillis() < (startTime + 400)) {
                    float complete = 1f - (System.currentTimeMillis() - startTime) / 400.0f;
                    size = 75 - (i * 13f) - (13f * complete);
                    point.x = Constants.SCREEN_WIDTH - 100f - (i * 200f) -  (200f * complete);
                } else {
                    bAnimation = false;
                    size = 75f - (i*13f);
                    point.x = Constants.SCREEN_WIDTH - 100f - (i * 200f);
                }
            } else {
                size = 75f - (i*13f);
                point.x = Constants.SCREEN_WIDTH - 100f - (i * 200f);
            }

            switch(item) {
                case "C":
                    // Circle
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Circle") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawCircle(point.x, point.y,size, paint);
                    canvas.drawCircle(point.x, point.y,size, bPaint);
                    break;
                case "S":
                    // Square
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Square") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawRect(point.x - size, point.y - size, point.x + size, point.y + size, paint);
                    canvas.drawRect(point.x - size, point.y - size, point.x + size, point.y + size, bPaint);
                    break;
                case "E":
                    // Rectangle
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Rectangle") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawRect(point.x - size, point.y - (size * 0.618f), point.x + size, point.y + (size * 0.618f), paint);
                    canvas.drawRect(point.x - size, point.y - (size * 0.618f), point.x + size, point.y + (size * 0.618f), bPaint);
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
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Triangle") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawPath(path, paint);
                    canvas.drawPath(path, bPaint);
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
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Triangle") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawPath(path, paint);
                    canvas.drawPath(path, bPaint);
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
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Rhombus") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawPath(path, paint);
                    canvas.drawPath(path, bPaint);
                    break;
                case "H":
                    // Hexagon
                    path = new Path();
                    path.setFillType(Path.FillType.EVEN_ODD);
                    path.moveTo(point.x - size, point.y);
                    path.lineTo(point.x - (int)(size * .5), point.y - size);
                    path.lineTo(point.x + (int)(size * .5), point.y - size);
                    path.lineTo(point.x + size, point.y);
                    path.lineTo(point.x + (int)(size * .5), point.y + size);
                    path.lineTo(point.x - (int)(size * .5), point.y + size);
                    path.close();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor( Common.getPreferenceString("color_Hexagon") ));
                    if (Constants.SHAPE_GRADIENT) {
                        paint.setShader(new RadialGradient(point.x,point.y, size*4, paint.getColor(), Color.BLACK, Shader.TileMode.MIRROR));
                    }
                    canvas.drawPath(path, paint);
                    canvas.drawPath(path, bPaint);
                    break;
            }
        }
    }

    private String randomObj() {
        // Random between, inclusive of high = random.nextInt(high - low +1) + low;
        int randomNum = Common.randomInt(1,7);
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
            case 6:
                // Hexagon
                return "H";
            case 7:
                // Rectangle
                return "E";
        }
        return "";
    }
}