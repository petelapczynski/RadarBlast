package petelap.radarblast;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Common {
    public static int randomInt(int low, int high) {
        int randomNum = ThreadLocalRandom.current().nextInt(low, high + 1);
        if (randomNum > high) {randomNum = high;}
        return randomNum;
    }
    public static float randomFlt(float low, float high) {
        Random r = new Random();
        float f = r.nextFloat();
        return (f * (high - low)) + low;
    }
    public static double randomDbl(double low, double high) {
        Random r = new Random();
        double d = r.nextDouble();
        return (d * (high - low)) + low;
    }
    public static String getPreferenceString(String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
        return settings.getString(key, "defaultValue");
    }
    public static boolean getPreferenceBoolean(String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
        return settings.getBoolean(key, false);
    }
    public static int getPreferenceInteger(String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
        return Integer.parseInt(settings.getString(key, "2"));
    }
}