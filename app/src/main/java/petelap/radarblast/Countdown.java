package petelap.radarblast;

import android.os.CountDownTimer;

public class Countdown {
    private static  final long START_TIME_IN_MILLIS = 60000;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private int timeLeftInSeconds;
    private boolean done;


    public Countdown() {
        done = false;
        timeLeftInMillis = START_TIME_IN_MILLIS;
        timeLeftInSeconds = (int)timeLeftInMillis / 1000;
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer( timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                timeLeftInSeconds = (int)timeLeftInMillis / 1000;
            }

            @Override
            public void onFinish() {
                done = true;
                timeLeftInSeconds = 0;
            }
        }.start();
    }

    public void pauseTimer(){
        countDownTimer.cancel();
        done = false;
    }

    public int getTimeLeft() {
        return timeLeftInSeconds;
    }

    public boolean IsFinished() {
        return done;
    }
}
