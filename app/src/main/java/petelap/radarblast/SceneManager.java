package petelap.radarblast;

import android.graphics.Canvas;
import android.view.MotionEvent;
import java.util.ArrayList;


/**
 * Created by Pete on 3/25/2018.
 */

// TODO have some way to manage scenes better for Levels on GamePlayScene(), maybe from a json file?

public class SceneManager {
    private ArrayList<IScene> scenes;
    public static int ACTIVE_SCENE;

    public SceneManager() {
        scenes = new ArrayList<>();
        scenes.add(new GamePlayMenu());
        scenes.add(new GamePlayScene1());
        scenes.add(new GamePlayScene());
        Constants.HEADER_HEIGHT = 0;
        ACTIVE_SCENE = 0;
    }

    public void resetScenes() {
        scenes = new ArrayList<>();
        scenes.add(new GamePlayMenu());
        scenes.add(new GamePlayScene1());
        scenes.add(new GamePlayScene());
        Constants.HEADER_HEIGHT = 0;
        ACTIVE_SCENE = 0;
    }

    public static void changeScene(String scene) {
        switch (scene) {
            case "MENU":
                // GamePlayMenu
                Constants.HEADER_HEIGHT = 0;
                ACTIVE_SCENE = 0;
                break;
            case "BLAST":
                // GamePlayScene1
                Constants.HEADER_HEIGHT = 0;
                ACTIVE_SCENE = 1;
                break;
            case "AREA":
                // GamePlayScene
                Constants.HEADER_HEIGHT = 200;
                ACTIVE_SCENE = 2;
                break;
        }
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
