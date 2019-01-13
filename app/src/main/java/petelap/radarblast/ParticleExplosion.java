package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

public class ParticleExplosion {
    private static final int STATE_ALIVE = 0;
    private static final int STATE_DEAD = 1;
    private ArrayList<Particle> particles;
    private float x, y;
    private int state;

    public ParticleExplosion(int particleCount, float size, PointF center, String type, boolean bounce) {
        this.state = STATE_ALIVE;
        double r1;
        double r2;
        this.particles = new ArrayList<>();
        int color = 0;

        //Random x,y within shape. size is 1/2 of the object
        for (int i=0; i < particleCount; i++) {
            switch (type) {
                case "Circle":
                case "Hexagon":
                case "SpecialDouble":
                case "SpecialFrenzy":
                case "SpecialTime":
                case "SpecialSpike":
                case "MenuButton":
                    float radius = Common.randomFlt(0, size); //between 0 and the radius of the circle
                    double angle = Common.randomDbl(0, 360); // between 0 and 360 (degrees)

                    x = (float)(center.x + radius * Math.cos(angle));
                    y = (float)(center.y + radius * Math.sin(angle));
                    switch (type) {
                        case "Circle":
                            color = Color.parseColor(Common.getPreferenceString("color_Circle"));
                            break;
                        case "Hexagon":
                            color = Color.parseColor(Common.getPreferenceString("color_Hexagon"));
                            break;
                        case "SpecialDouble":
                            color = Color.rgb(72, 0, 128);
                            break;
                        case "SpecialFrenzy":
                            color = Color.rgb(250, 237, 100);
                            break;
                        case "SpecialTime":
                            color = Color.rgb(117, 117, 117);
                            break;
                        case "SpecialSpike":
                            color = Color.rgb(185, 125, 25);
                            break;
                        case "MenuButton":
                            color = Color.rgb(211, 211, 211);
                            break;
                    }

                    break;
                case "Square": {
                    x = Common.randomFlt(center.x - size, center.x + size);
                    y = Common.randomFlt(center.y - size, center.y + size);
                    color = Color.parseColor(Common.getPreferenceString("color_Square"));
                    break;
                }
                case "Rectangle": {
                    x = Common.randomFlt(center.x - size, center.x + size);
                    y = Common.randomFlt(center.y - (size * 0.618f), center.y + (size * 0.618f));
                    color = Color.parseColor(Common.getPreferenceString("color_Rectangle"));
                    break;
                }
                case "TriangleUp":
                case "TriangleDown": {
                    float width = size * 2.0f;
                    float height = width * 0.866f;
                    PointF Left;
                    PointF Center;
                    PointF Right;
                    Random rnd;
                    if (type.equals("TriangleUp")) {
                        Left = new PointF((center.x - width / 2.0f), (center.y + (height / 2.0f)));
                        Center = new PointF(center.x, (center.y - (height / 2.0f)));
                        Right = new PointF((center.x + width / 2.0f), (center.y + (height / 2.0f)));
                    } else {
                        Left = new PointF((center.x - width / 2.0f), (center.y - (height / 2.0f)));
                        Center = new PointF(center.x, (center.y + (height / 2.0f)));
                        Right = new PointF((center.x + width / 2.0f), (center.y - (height / 2.0f)));
                    }

                    rnd = new Random();
                    r1 = rnd.nextDouble();
                    rnd = new Random();
                    r2 = rnd.nextDouble();

                    x = (float)((1f - Math.sqrt(r1)) * Left.x + (Math.sqrt(r1) * (1 - r2)) * Center.x + (Math.sqrt(r1) * r2) * Right.x);
                    y = (float)((1f - Math.sqrt(r1)) * Left.y + (Math.sqrt(r1) * (1 - r2)) * Center.y + (Math.sqrt(r1) * r2) * Right.y);
                    color = Color.parseColor(Common.getPreferenceString("color_Triangle"));
                    break;
                }
                case "Rhombus": {
                    float width = size * 2.0f;
                    float height = width * 1.732f;

                    PointF Left = new PointF((center.x - width / 2.0f), center.y);
                    PointF Right = new PointF((center.x + width / 2.0f), center.y);
                    PointF Center;
                    if ((i & 1) == 0) {
                        Center = new PointF(center.x, (center.y - (height / 2.0f)));
                    } else {
                        Center = new PointF(center.x, (center.y + (height / 2.0f)));
                    }
                    Random rnd;
                    rnd = new Random();
                    r1 = rnd.nextDouble();
                    rnd = new Random();
                    r2 = rnd.nextDouble();

                    x = (float) ((1f - Math.sqrt(r1)) * Left.x + (Math.sqrt(r1) * (1 - r2)) * Center.x + (Math.sqrt(r1) * r2) * Right.x);
                    y = (float) ((1f - Math.sqrt(r1)) * Left.y + (Math.sqrt(r1) * (1 - r2)) * Center.y + (Math.sqrt(r1) * r2) * Right.y);
                    color = Color.parseColor(Common.getPreferenceString("color_Rhombus"));
                    break;
                }
            }
            // Add particle
            Particle p = new Particle(center, x, y, color, type, bounce);
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
            p.draw(canvas);
        }
    }

    public int getState() {
        return state;
    }}