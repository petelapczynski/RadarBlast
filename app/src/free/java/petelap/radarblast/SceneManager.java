package petelap.radarblast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;

public class SceneManager {
    private static ArrayList<IScene> scenes;
    private static int ACTIVE_SCENE;
    private static JsonFileHelper jsonFileHelper;
    public static Levels levels;
    public static Levels.Level level;
    public static int ACTIVE_LEVEL;
    public static Highscores highscores;
    private static String LEVEL_PREFIX;
    private static AdsManager adsManager;

    public SceneManager() {
        scenes = new ArrayList<>();
        jsonFileHelper = new JsonFileHelper();
        ACTIVE_SCENE = 0;
        highscores = new Highscores();
        Constants.GAME_STATUS = "GAMELOOP";
        adsManager = new AdsManager();
        changeScene("MENU");
    }

    public static void changeScene(String scene) {
        for (int i=0; i < scenes.size(); i++) {
            scenes.get(ACTIVE_SCENE).terminate();
            SoundManager.pause();
        }
        scenes = new ArrayList<>();
        Constants.SHAPE_GRADIENT = Common.getPreferenceBoolean("gradient");

        switch (scene) {
            case "MENU":
                // GamePlayMenu
                Constants.HEADER_HEIGHT = 0;
                scenes.add(new GamePlayMenu());
                break;
            case "BLAST":
                // GamePlayBlast
                Constants.HEADER_HEIGHT = 0;
                LEVEL_PREFIX = "blast";
                highscores = jsonFileHelper.getHighScores(LEVEL_PREFIX);
                scenes.add(new GamePlayBlast());
                adHandler();
                break;
            case "AREA_LEVELS":
                // GamePlayLevels
                Constants.HEADER_HEIGHT = 0;
                ACTIVE_LEVEL = 0;
                levels = jsonFileHelper.getLevels("area_levels.json");
                level = levels.getLevel(ACTIVE_LEVEL);
                scenes.add(new GamePlayLevelSelect("AREA"));
                adHandler();
                break;
            case "AREA":
                // GamePlayArea
                Constants.HEADER_HEIGHT = 200;
                LEVEL_PREFIX = "area_" + level;
                highscores = jsonFileHelper.getHighScores(LEVEL_PREFIX);
                scenes.add(new GamePlayArea(level));
                break;
            case "HOW_TO":
                // GamePlayHowTo
                Constants.HEADER_HEIGHT = 0;
                scenes.add(new GamePlayHowTo());
                break;
            case "OPTIONS":
                // pref_main.xml
                Constants.HEADER_HEIGHT = 0;
                Constants.GAME_STATUS = "OPTIONS";
                Constants.CONTEXT.startActivity(new Intent(Constants.CONTEXT, SettingsActivity.class));
                break;
            case "PICTURE":
                // GamePlayPicture
                Constants.HEADER_HEIGHT = 200;
                scenes.add(new GamePlayPicture());
                adHandler();
                break;
            case "LASER_LEVELS":
                // GamePlayLevelSelect
                Constants.HEADER_HEIGHT = 0;
                ACTIVE_LEVEL = 0;
                levels = jsonFileHelper.getLevels("laser_levels.json");
                level = levels.getLevel(ACTIVE_LEVEL);
                scenes.add(new GamePlayLevelSelect("LASER"));
                adHandler();
                break;
            case "LASER":
                // GamePlayLaser
                Constants.HEADER_HEIGHT = 200;
                LEVEL_PREFIX = "laser_" + level;
                highscores = jsonFileHelper.getHighScores(LEVEL_PREFIX);
                scenes.add(new GamePlayLaser(level));
                break;
            case "EXIT":
                Constants.GAME_STATUS = "EXIT";
                Activity activity = (Activity)Constants.CONTEXT;
                activity.finish();
                break;
        }
    }

    public static void changeLevel(int lvlNumber, boolean ads) {
        ACTIVE_LEVEL = lvlNumber;
        level = levels.getLevel(ACTIVE_LEVEL);
        LEVEL_PREFIX = "area_" + ACTIVE_LEVEL;
        highscores = jsonFileHelper.getHighScores(LEVEL_PREFIX);
        if (ads) {
            adHandler();
        }
    }

    public static void saveScores() {
        jsonFileHelper.saveHighscores(highscores, LEVEL_PREFIX);
    }

    public void receiveTouch(MotionEvent event) {
        if (scenes.size() > 0) {
            scenes.get(ACTIVE_SCENE).receiveTouch(event);
        }
    }

    public void update() {
        if (scenes.size() > 0) {
            scenes.get(ACTIVE_SCENE).update();
        }
    }

    public void draw(Canvas canvas) {
        if (scenes.size() > 0) {
            scenes.get(ACTIVE_SCENE).draw(canvas);
        }
    }

    private static void adHandler() {
        if (System.currentTimeMillis() >= (Constants.INIT_TIME + 60000) ) {
            Constants.GAME_STATUS = "AD";
            adsManager.showAd();
            Constants.INIT_TIME = System.currentTimeMillis();
        }
    }
}