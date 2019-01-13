package petelap.radarblast;

import android.graphics.Bitmap;
import android.graphics.PointF;

public abstract class ObstacleSpecials {
    protected float radius;
    protected int color;
    protected PointF center;
    protected String type;
    protected boolean InGameArea;
    protected boolean pop;
    protected Bitmap image;
    protected int dx;
    protected int dy;
    protected float spin;
}
