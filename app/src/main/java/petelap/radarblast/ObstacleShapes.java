package petelap.radarblast;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public abstract class ObstacleShapes {
    protected RectF rect;
    protected int color;
    protected PointF center;
    protected float size;
    protected String type;
    protected boolean InGameArea;
    protected boolean pop;
    protected Paint paint;
    protected Paint bPaint;
}
