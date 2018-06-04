package petelap.radarblast;

public class Explosion {
    public static final int STATE_ALIVE = 0;
    public static final int STATE_DEAD = 1;

    private Particle[] particles;
    private int x, y;
    private int count;
    private int state;

    public Explosion(int particleCount, int x, int y) {
        //Log.d(TAG, "Explosion created at " + x + "," + y);
        this.count = particleCount;
        this.state = STATE_ALIVE;
        this.particles = new Particle[particleCount];
        for (int i=0; i < this.particles.length; i++) {
            Particle p = new Particle(x,y);
            this.particles[i] = p;
        }
        this.count = particleCount;
    }
}
