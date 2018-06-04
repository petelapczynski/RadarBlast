package petelap.radarblast;

import android.graphics.Canvas;
import android.view.MotionEvent;
import java.util.ArrayList;

/**
 * Created by Pete on 3/25/2018.
 */

public class SceneManager {
    private ArrayList<IScene> scenes = new ArrayList<>();
    public static int ACTIVE_SCENE;

    public SceneManager() {
        ACTIVE_SCENE = 0;
        scenes.add(new GameplayScene());
    }

    public void receiveTouch(MotionEvent event) {
        scenes.get(ACTIVE_SCENE).receiveTouch(event);
    }

    public void update() {
        scenes.get(ACTIVE_SCENE).update();
    }

    public void draw(Canvas canvas) {
        scenes.get(ACTIVE_SCENE).draw(canvas);
    }
}
