package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

public class ParticleExplosion {
    public static final int STATE_ALIVE = 0;
    public static final int STATE_DEAD = 1;

    private ArrayList<Particle> particles;
    private int x, y;
    private int state;
    private int count;
    private Random rnd;

    public ParticleExplosion(int particleCount, float size, int startX, int startY, String type) {
        //Log.d(TAG, "Explosion created at " + x + "," + y);
        this.state = STATE_ALIVE;
        //this.x = x;
        //this.y = y;
        this.count = particleCount;
        int s = (int)size;
        float width;
        float height;
        double r1;
        double r2;
        this.particles = new ArrayList<>();
        Point Left, Center, Right;
        int color = 0;

        //Random x,y within shape. size is 1/2 of the object
        for (int i=0; i < particleCount; i++) {
            switch (type) {
                case "Circle":
                    float radius = Common.randomFlt(0, size); //between 0 and the radius of the circle
                    double angle = Common.randomDbl(0,360); // between 0 and 360 (degrees)
                    x = (int)(startX + radius * Math.cos(angle) );
                    y = (int)(startY + radius * Math.sin(angle) );
                    color = Color.BLUE;
                    break;
                case "Square":
                    x = Common.randomInt(startX - s, startX + s );
                    y = Common.randomInt(startY - s, startY + s );
                    color = Color.RED;
                    break;
                case "TriangleUp":
                    width = size * 2.0f;
                    //height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));
                    height = width * 0.866f;

                    Left = new Point((int)(startX - width/2.0f), (int)(startY + (height/2.0f)));
                    Center = new Point(startX, (int)(startY - (height/2.0f)));
                    Right = new Point((int)(startX + width/2.0f), (int)(startY + (height/2.0f)));

                    rnd = new Random();
                    r1 = rnd.nextDouble();
                    rnd = new Random();
                    r2 = rnd.nextDouble();

                    x = (int)((1 - Math.sqrt(r1)) * Left.x + (Math.sqrt(r1) * (1 - r2)) * Center.x + (Math.sqrt(r1) * r2) * Right.x);
                    y = (int)((1 - Math.sqrt(r1)) * Left.y + (Math.sqrt(r1) * (1 - r2)) * Center.y + (Math.sqrt(r1) * r2) * Right.y);
                    color = Color.YELLOW;
                    break;
                case "TriangleDown":
                    width = size * 2.0f;
                    //height = (float)(Math.sqrt((width*width) - (width/2.0f)*(width/2.0f)));
                    height = width * 0.866f;

                    Left = new Point((int)(startX - width/2.0f), (int)(startY - (height/2.0f)));
                    Center = new Point(startX, (int)(startY + (height/2.0f)));
                    Right = new Point((int)(startX + width/2.0f), (int)(startY - (height/2.0f)));

                    rnd = new Random();
                    r1 = rnd.nextDouble();
                    rnd = new Random();
                    r2 = rnd.nextDouble();

                    x = (int)((1 - Math.sqrt(r1)) * Left.x + (Math.sqrt(r1) * (1 - r2)) * Center.x + (Math.sqrt(r1) * r2) * Right.x);
                    y = (int)((1 - Math.sqrt(r1)) * Left.y + (Math.sqrt(r1) * (1 - r2)) * Center.y + (Math.sqrt(r1) * r2) * Right.y);
                    color = Color.YELLOW;
                    break;
                case "Rhombus":
                    width = size * 2.0f;
                    height = width * 1.732f;

                    Left = new Point((int)(startX - width/2.0f), startY);
                    Right = new Point((int)(startX + width/2.0f), startY);
                    if ( (i & 1) == 0 ) {
                        Center = new Point(startX, (int)(startY - (height/2.0f)));
                    } else {
                        Center = new Point(startX, (int)(startY + (height/2.0f)));
                    }

                    rnd = new Random();
                    r1 = rnd.nextDouble();
                    rnd = new Random();
                    r2 = rnd.nextDouble();

                    x = (int)((1 - Math.sqrt(r1)) * Left.x + (Math.sqrt(r1) * (1 - r2)) * Center.x + (Math.sqrt(r1) * r2) * Right.x);
                    y = (int)((1 - Math.sqrt(r1)) * Left.y + (Math.sqrt(r1) * (1 - r2)) * Center.y + (Math.sqrt(r1) * r2) * Right.y);
                    color = Color.rgb(255,140,0);
                    break;
            }

            Particle p = new Particle(x, y, color, type);
            particles.add(p);
        }
    }

    public void update() {
        ArrayList<Particle> particlesDone = new ArrayList<>();
        for(Particle p: particles) {
            if (p.getState() == STATE_ALIVE) {
                p.update();
            } else {
                particlesDone.add(p);
            }
        }

        particles.removeAll(particlesDone);

        // particles remaining
        if ( particles.isEmpty() ) {
            this.state = STATE_DEAD;
        }
    }

    public void draw(Canvas canvas) {
        for(Particle p: particles) {
            if(p.getState() == STATE_ALIVE) {
                p.draw(canvas);
            }
        }
    }

    public int getState() {
        return state;
    }
}
