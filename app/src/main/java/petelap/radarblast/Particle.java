package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;


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


    public Particle(int x, int y, int color, String type) {
        this.x = x;
        this.y = y;
        this.color = color;
        paint = new Paint();
        paint.setColor(color);
        this.type = type;
        state = Particle.STATE_ALIVE;

        size = Common.randomFlt(3,MAX_DIMENSION);

        lifetime = DEFAULT_LIFETIME;
        age = 0;

        xv = Common.randomFlt(MAX_SPEED * -1,MAX_SPEED );
        yv = Common.randomFlt(MAX_SPEED * -1,MAX_SPEED );
//        // smooth out diag speed
//        if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
//            xv *= 0.7;
//            yv *= 0.7;
//        }

    }

    public int getState() {
        return state;
    }

    public void update() {
        if (state != STATE_DEAD) {
            x += xv;
            y += yv;

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
        if(type == "TriangleUp" || type == "TriangleDown" || type == "Rhombus"){
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
            canvas.drawPath(path, paint);
        }

    }
}
