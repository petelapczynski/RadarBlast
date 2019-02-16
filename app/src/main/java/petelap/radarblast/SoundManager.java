package petelap.radarblast;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {
    private static SoundPool soundPool;
    private static MediaPlayer bgMusic;
    private static int soundID_pop_1;
    private static int soundID_pop_2;
    private static int soundID_pop_3;
    private static int soundID_pop_4;
    private static int soundID_pop_5;
    private static int soundID_pop_6;
    private static int soundID_pop_7;
    private static int soundID_spike;
    private static boolean bMusic;
    private static boolean bSound;

    public SoundManager(){
        //Game Music Setting
        bMusic = Common.getPreferenceBoolean("music");
        //Game Sound Setting
        bSound = Common.getPreferenceBoolean("sound");

        if(bMusic){
            //Set Music
            bgMusic = MediaPlayer.create(Constants.CONTEXT,R.raw.music_menu);
            bgMusic.setLooping(true);
        }
        if(bSound){
            //Build attributes
            AudioAttributes.Builder attrBuilder  = new AudioAttributes.Builder();
            attrBuilder.setUsage(AudioAttributes.USAGE_GAME);
            attrBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            AudioAttributes attributes = attrBuilder.build();
            //Build SoundPool
            SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
            soundPoolBuilder.setAudioAttributes(attributes);
            soundPoolBuilder.setMaxStreams(5);
            soundPool = soundPoolBuilder.build();
            //Load sounds
            soundID_pop_1 = soundPool.load(Constants.CONTEXT,R.raw.pop_1,1);
            soundID_pop_2 = soundPool.load(Constants.CONTEXT,R.raw.pop_2,1);
            soundID_pop_3 = soundPool.load(Constants.CONTEXT,R.raw.pop_3,1);
            soundID_pop_4 = soundPool.load(Constants.CONTEXT,R.raw.pop_4,1);
            soundID_pop_5 = soundPool.load(Constants.CONTEXT,R.raw.pop_5,1);
            soundID_pop_6 = soundPool.load(Constants.CONTEXT,R.raw.pop_6,1);
            soundID_pop_7 = soundPool.load(Constants.CONTEXT,R.raw.pop_7,1);
            soundID_spike = soundPool.load(Constants.CONTEXT,R.raw.spike,1);
        }
    }

    public static void setGameMusic(String GameMode){
        if(bMusic){
            switch (GameMode) {
                case "MENU":
                    bgMusic = MediaPlayer.create(Constants.CONTEXT, R.raw.music_menu);
                    break;
                case "AREA":
                    bgMusic = MediaPlayer.create(Constants.CONTEXT, R.raw.music_area);
                    break;
                case "BLAST":
                    bgMusic = MediaPlayer.create(Constants.CONTEXT, R.raw.music_blast);
                    break;
                case "LASER":
                    bgMusic = MediaPlayer.create(Constants.CONTEXT, R.raw.music_laser);
                    break;
            }
            bgMusic.setLooping(true);
        }
    }

    public static void playMusic(){
        if(bMusic){
            if (bgMusic != null && !bgMusic.isPlaying()) {
                bgMusic.start();
            }
        }
    }

    public static void playSound(String Sound){
        if(bSound){
            switch (Sound) {
                case "POP":
                    int soundID = Common.randomInt(1,7);
                    switch (soundID) {
                        case 1:
                            soundPool.play(soundID_pop_1,1.0f,1.0f,1,0,1);
                            break;
                        case 2:
                            soundPool.play(soundID_pop_2,1.0f,1.0f,1,0,1);
                            break;
                        case 3:
                            soundPool.play(soundID_pop_3,1.0f,1.0f,1,0,1);
                            break;
                        case 4:
                            soundPool.play(soundID_pop_4,1.0f,1.0f,1,0,1);
                            break;
                        case 5:
                            soundPool.play(soundID_pop_5,1.0f,1.0f,1,0,1);
                            break;
                        case 6:
                            soundPool.play(soundID_pop_6,1.0f,1.0f,1,0,1);
                            break;
                        case 7:
                            soundPool.play(soundID_pop_7,1.0f,1.0f,1,0,1);
                            break;
                        default:
                            soundPool.play(soundID_pop_4,1.0f,1.0f,1,0,1);
                            break;
                    }
                    break;
                case "SPIKE":
                    soundPool.play(soundID_spike,1.0f,1.0f,1,0,1);
                    break;
            }
        }
    }

    public static void pause(){
        if(bMusic){
            if (bgMusic != null && bgMusic.isPlaying()) {
                bgMusic.pause();
            }
            //bgMusic.release();
        }
        if(bSound){
            soundPool.autoPause();
        }
    }

    public static void stop(){
        if (bMusic){
            bgMusic.stop();
            bgMusic.release();
        }

        if(bSound){
            soundPool.release();
        }
    }

    public static void refresh(){
        //Game Music Setting
        bMusic = Common.getPreferenceBoolean("music");
        //Game Sound Setting
        bSound = Common.getPreferenceBoolean("sound");
    }
}