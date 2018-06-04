package petelap.radarblast;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Pete on 3/25/2018.
 */

public interface IScene {
    void update();
    void draw(Canvas canvas);
    void terminate();
    void receiveTouch(MotionEvent event);
}
