package petelap.radarblast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Full screen area, no title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        // Default preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_main, false);

        // Default Context
        Constants.CONTEXT = this;

        // Shape Gradients
        Constants.SHAPE_GRADIENT = Common.getPreferenceBoolean("gradient");

        //Smooth pixels
        getWindow().setFormat(PixelFormat.RGBA_8888);

        //Game Sounds
        Constants.SOUND_MANAGER = new SoundManager();

        // Set GamePanel
        setContentView(new GamePanel(this));

        // Input User Name dialog box
        if (Common.getPreferenceString("username").equals("_")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Welcome to Radar Blast!");
            builder.setMessage("Enter your name or initials.");
            builder.setCancelable(false);
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String txt = input.getText().toString();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", txt.trim());
                    editor.apply();
                    dialogInterface.cancel();
                }
            });
        /* builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing
            }
        }); */
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.GAME_STATUS.equals("PAUSED")) {
            SoundManager.playMusic();
            Constants.GAME_STATUS = "GAMELOOP";
        }

        if (Constants.GAME_STATUS.equals("OPTIONS")) {
            SoundManager.refresh();
            Constants.GAME_STATUS = "GAMELOOP";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.GAME_STATUS.equals("GAMELOOP")) {
            SoundManager.pause();
            Constants.GAME_STATUS = "PAUSED";
        }
        if (isFinishing()) {
            SoundManager.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        // On back button, Alert to prompt Main Menu
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Return to Game Menu?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Constants.GAME_STATUS = "GAMELOOP";
                SceneManager.changeScene("MENU");
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}