package petelap.radarblast;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface IScene {
    void update();
    void draw(Canvas canvas);
    void terminate();
    void receiveTouch(MotionEvent event);
}