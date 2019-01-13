package petelap.radarblast;

public class GameTimer {
    private long timeTotalInMillis;
    private long timeLeftInMillis;
    private boolean done;
    private boolean paused;


    public GameTimer(long totalTime) {
        timeLeftInMillis = totalTime;
        done = false;
        paused = false;
    }

    public int getTimeLeftInSeconds() {
        return Math.round(timeLeftInMillis / 1000);
    }

    public long getTimeLeftInMillis () {
        return timeLeftInMillis;
    }

    public boolean IsFinished() {
        return done;
    }

    public void startTimer() {
        timeTotalInMillis = System.currentTimeMillis() + timeLeftInMillis;
        done = false;
        paused = false;
    }

    public void pauseTimer(){
        paused = true;
    }

    public void update() {
        if(!paused){
            long t = System.currentTimeMillis();
            if(t >= timeTotalInMillis){
                done = true;
            } else {
                timeLeftInMillis = timeTotalInMillis - t;
            }
        }
    }
}