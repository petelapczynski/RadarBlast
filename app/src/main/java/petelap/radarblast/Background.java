package petelap.radarblast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.AsyncTask;

import java.io.InputStream;

public class Background {
    // Draws bitmap to full screen
    private Bitmap image;

    public Background(int left, int top, int right, int bottom) {
        String imgStr = Common.getPreferenceString("background");
        if (imgStr.equals("bg_url")) {
            setBackgroundURL(Common.getPreferenceString("pictureURL"));
        } else {
            int resID = Constants.CONTEXT.getResources().getIdentifier(Common.getPreferenceString("background"), "drawable", Constants.CONTEXT.getPackageName());
            image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(Constants.CONTEXT.getResources(),resID), Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT,false);
        }
    }

    public Background(String URLString, int left, int top, int right, int bottom) {
        setBackgroundURL(URLString);
    }

    public Background(int color, int left, int top, int right, int bottom) {
        int x, y, radius;
        x = Constants.SCREEN_WIDTH/2;
        y = (Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT)/2 + Constants.HEADER_HEIGHT;
        Bitmap b = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        if (Constants.SHAPE_GRADIENT) {
            radius = Constants.SCREEN_HEIGHT/2;
            Paint bgPaint = new Paint();
            bgPaint.setDither(true);
            bgPaint.setAntiAlias(true);
            bgPaint.setShader(new RadialGradient(x, y, radius, color, Color.DKGRAY, Shader.TileMode.CLAMP));
            c.drawColor( Color.DKGRAY );
            c.drawCircle(x, y, radius, bgPaint);
        } else {
            c.drawColor( color );
        }
        image = b;
    }

    private void setBackgroundURL(String url){
        int resID = Constants.CONTEXT.getResources().getIdentifier("bg_gray", "drawable", Constants.CONTEXT.getPackageName());
        image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(Constants.CONTEXT.getResources(),resID),Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT,false);

        new DownloadImageTask(image).execute(url);
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image,0,Constants.HEADER_HEIGHT,null);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                image = Bitmap.createScaledBitmap(result,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT - Constants.HEADER_HEIGHT,false);
            }
        }
    }
}