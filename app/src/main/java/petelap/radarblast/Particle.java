package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

public class Particle {
    private static final int STATE_ALIVE = 0;
    private static final int STATE_DEAD = 1;
    private static final int DEFAULT_LIFETIME = 125;
    private static final int FADE_TIME = 5;
    private static final int MAX_DIMENSION = 25;
    private static final int MAX_SPEED = 12;

    private int state;
    private float x, y;
    private float xv, yv;
    private int age;
    private int lifetime;
    private int color;
    private Paint paint;
    private PointF center;
    private float size;
    private String type;
    private boolean circleType;
    private Path path;
    private boolean bBounce;


    public Particle(PointF start, float xPos, float yPos, int color, String type, boolean bounce) {
        center = start;
        this.x = xPos;
        this.y = yPos;
        this.color = color;
        paint = new Paint();
        paint.setColor(color);
        this.type = type;
        bBounce = bounce;
        state = Particle.STATE_ALIVE;

        size = Common.randomFlt(10, MAX_DIMENSION);

        lifetime = DEFAULT_LIFETIME;
        age = 0;

        xv = Common.randomFlt(MAX_SPEED * -1, MAX_SPEED);
        yv = Common.randomFlt(MAX_SPEED * -1, MAX_SPEED);

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

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        switch (type) {
            case "Square":
                path.moveTo(x - size, y - size);
                path.lineTo(x + size, y - size);
                path.lineTo(x + size, y + size);
                path.lineTo(x - size, y + size);
                path.close();
                break;
            case "Rectangle":
                path.moveTo(x - size, y - (size * 0.618f));
                path.lineTo(x + size, y - (size * 0.618f));
                path.lineTo(x + size, y + (size * 0.618f));
                path.lineTo(x - size, y + (size * 0.618f));
                path.close();
                break;
            case "TriangleUp":
                path.moveTo(x - size, y + size);
                path.lineTo(x, y - size);
                path.lineTo(x + size, y + size);
                path.close();
                break;
            case "TriangleDown":
                path.moveTo(x - size, y - size);
                path.lineTo(x, y + size);
                path.lineTo(x + size, y - size);
                path.close();
                break;
            case "Rhombus":
                path.moveTo(x - (size * 0.75f), y);
                path.lineTo(x, y - size);
                path.lineTo(x + (size * 0.75f), y);
                path.lineTo(x, y + size);
                path.close();
                break;
            case "Hexagon":
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(x - size, y);
                path.lineTo(x - (size * 0.5f), y - size);
                path.lineTo(x + (size * 0.5f), y - size);
                path.lineTo(x + size, y);
                path.lineTo(x + (size * 0.5f), y + size);
                path.lineTo(x - (size * 0.5f), y + size);
                path.close();
                break;
            default:
                circleType = true;
        }
    }

    public int getState() {
        return state;
    }

    public void update() {
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
        } else {
            // Update position
            x += xv;
            y += yv;

            // Bounce off walls
            if (bBounce) {
                if (x < 0 || x > Constants.SCREEN_WIDTH) {
                    xv *= -1;
                    x += xv + xv;
                }
                if (y < Constants.HEADER_HEIGHT || y > Constants.SCREEN_HEIGHT) {
                    yv *= -1;
                    y += yv + yv;
                }
            }

            // Update path
            path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            switch (type) {
                case "Square":
                    path.moveTo(x - size, y - size);
                    path.lineTo(x + size, y - size);
                    path.lineTo(x + size, y + size);
                    path.lineTo(x - size, y + size);
                    path.close();
                    break;
                case "Rectangle":
                    path.moveTo(x - size, y - (size * 0.618f));
                    path.lineTo(x + size, y - (size * 0.618f));
                    path.lineTo(x + size, y + (size * 0.618f));
                    path.lineTo(x - size, y + (size * 0.618f));
                    path.close();
                    break;
                case "TriangleUp":
                    path.moveTo(x - size, y + size);
                    path.lineTo(x, y - size);
                    path.lineTo(x + size, y + size);
                    path.close();
                    break;
                case "TriangleDown":
                    path.moveTo(x - size, y - size);
                    path.lineTo(x, y + size);
                    path.lineTo(x + size, y - size);
                    path.close();
                    break;
                case "Rhombus":
                    path.moveTo(x - (size * 0.75f), y);
                    path.lineTo(x, y - size);
                    path.lineTo(x + (size * 0.75f), y);
                    path.lineTo(x, y + size);
                    path.close();
                    break;
                case "Hexagon":
                    path.moveTo(x - size, y);
                    path.lineTo(x - (size * 0.5f), y - size);
                    path.lineTo(x + (size * 0.5f), y - size);
                    path.lineTo(x + size, y);
                    path.lineTo(x + (size * 0.5f), y + size);
                    path.lineTo(x - (size * 0.5f), y + size);
                    path.close();
                    break;
            }
        }
    }

    public void draw(Canvas canvas) {
        if (circleType) {
            canvas.drawCircle(x, y, size, paint);
        } else {
            canvas.drawPath(path, paint);
        }
    }
}