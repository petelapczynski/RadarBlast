package petelap.radarblast;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {
    private static SoundPool soundPool;
    private static MediaPlayer bgMusic;
    private static int soundID_pop_low;
    private static int soundID_pop_med;
    private static int soundID_pop_high;
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
            bgMusic = MediaPlayer.create(Constants.CONTEXT,R.raw.music_1);
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
            soundID_pop_low = soundPool.load(Constants.CONTEXT,R.raw.pop_low,1);
            soundID_pop_med = soundPool.load(Constants.CONTEXT,R.raw.pop_med,1);
            soundID_pop_high = soundPool.load(Constants.CONTEXT,R.raw.pop_high,1);
            soundID_spike = soundPool.load(Constants.CONTEXT,R.raw.bomb,1);
        }
    }

    public static void setGameMusic(String GameMode){
        if(bMusic){
            if(GameMode.equals("AREA")){
                bgMusic = MediaPlayer.create(Constants.CONTEXT,R.raw.music_1);

            } else if (GameMode.equals("BLAST")){
                bgMusic = MediaPlayer.create(Constants.CONTEXT,R.raw.music_2);
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
                    int soundID = Common.randomInt(1,3);
                    switch (soundID) {
                        case 1:
                            soundPool.play(soundID_pop_low,1.0f,1.0f,1,0,1);
                            break;
                        case 2:
                            soundPool.play(soundID_pop_med,1.0f,1.0f,1,0,1);
                            break;
                        case 3:
                        default:
                            soundPool.play(soundID_pop_high,1.0f,1.0f,1,0,1);
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