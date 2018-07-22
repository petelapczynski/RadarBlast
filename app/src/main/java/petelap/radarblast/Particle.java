package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;


public class Particle {
    public static final int STATE_ALIVE = 0;
    public static final int STATE_DEAD = 1;

    public static final int DEFAULT_LIFETIME = 125;
    public static final int FADE_TIME = 5;
    public static final int MAX_DIMENSION = 20;
    public static final int MAX_SPEED = 12;

    private int state;
    private float x, y;
    private float xv, yv;
    private int age;
    private int lifetime;
    private int color;
    private Paint paint;
    private float size;
    private String type;
    private Point center;


    public Particle(Point start, int x, int y, int color, String type) {
        center = start;
        this.x = x;
        this.y = y;
        this.color = color;
        paint = new Paint();
        paint.setColor(color);
        this.type = type;
        state = Particle.STATE_ALIVE;

        size = Common.randomFlt(5,MAX_DIMENSION);

        lifetime = DEFAULT_LIFETIME;
        age = 0;

        xv = Common.randomFlt(MAX_SPEED * -1,MAX_SPEED );
        yv = Common.randomFlt(MAX_SPEED * -1,MAX_SPEED );
        // smooth out diag speed
//        if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
//            xv *= 0.7;
//            yv *= 0.7;
//        }

        // Explode from center point
        if (x <= center.x && xv > 0.0f) {
            xv *= -1;
        }
        if (x > center.x && xv < 0.0f) {
            xv *= -1;
        }
        if (y <= center.y && yv > 0.0f) {
            yv *= -1;
        }
        if (y > center.y && yv < 0.0f) {
            yv *= -1;
        }
    }

    public int getState() {
        return state;
    }

    public void update() {
        if (state != STATE_DEAD) {
            x += xv;
            y += yv;

            // Bounce off walls
            if(x < 0 || x > Constants.SCREEN_WIDTH){
                xv *= -1;
                x += xv + xv;
            }
            if(y < Constants.HEADER_HEIGHT || y > Constants.SCREEN_HEIGHT){
                yv *= -1;
                y += yv + yv;
            }

            // extract alpha
            int a = color >>> 24;
            a -= FADE_TIME; // fade by FADE_TIME
            if (a <= 0) {
                state = STATE_DEAD;
            } else {
                color = (color & 0x00ffffff) + (a << 24); // set new alpha
                paint.setAlpha(a);
                age++; // increase age
            }
            if (age >= lifetime) {
                state = STATE_DEAD;
            }
        }
    }

    public void draw(Canvas canvas) {
        if(type == "Circle") {
            canvas.drawCircle(x,y,size,paint);
        }
        if(type == "Square") {
            canvas.drawRect(x-size,y-size,x+size,y+size,paint);
        }
        if(type == "TriangleUp" || type == "TriangleDown" || type == "Rhombus" || type == "Hexagon"){
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            if(type == "TriangleUp"){
                path.moveTo(x - size, y + size);
                path.lineTo(x, y - size);
                path.lineTo(x + size, y + size);
                path.close();
            }
            if(type == "TriangleDown"){
                path.moveTo(x - size, y - size);
                path.lineTo(x, y + size);
                path.lineTo(x + size, y - size);
                path.close();
            }
            if(type == "Rhombus"){
                path.moveTo(x - (int)(size * .75), y);
                path.lineTo(x, y - size);
                path.lineTo(x + (int)(size * .75), y);
                path.lineTo(x, y + size);
                path.close();
            }
            if(type == "Hexagon"){
                path.moveTo(x - size, y);
                path.lineTo(x - (int)(size * .5), y - size);
                path.lineTo(x + (int)(size * .5), y - size);
                path.lineTo(x + size, y);
                path.lineTo(x + (int)(size * .5), y + size);
                path.lineTo(x - (int)(size * .5), y + size);
                path.close();
            }
            canvas.drawPath(path, paint);
        }

    }
}
