package petelap.radarblast;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

public class ObstacleSpecialSpike extends ObstacleSpecials implements IGameObjectSpecial {

    public ObstacleSpecialSpike(float startX, float startY, float radius, int color) {
        this.radius = radius;
        this.center = new PointF(startX, startY);
        this.color = color;
        type = "SpecialSpike";
        InGameArea = true;
        pop = false;
        image = BitmapFactory.decodeResource(Constants.CONTEXT.getResources(),R.drawable.spec_spike);
        dx = Common.randomInt(-10,10);
        dy = Common.randomInt(-10,10);
        spin = 0;
    }

    @Override
    public IGameObjectSpecial NewInstance() {
        return GetInstance();
    }

    @Override
    public boolean InGameArea() {
        return InGameArea;
    }

    @Override
    public boolean pointInside(PointF point) {
        return ((point.x - center.x) * (point.x - center.x) + (point.y - center.y) * (point.y - center.y) <= radius * radius);
    }

    @Override
    public void pop() {
        pop = true;
        InGameArea = false;
    }

    @Override
    public float getArea() {
        return ((float)Math.PI * radius * radius);
    }

    public static IGameObjectSpecial GetInstance() {
        return new ObstacleSpecialSpike(0,0,(Constants.BTN_HEIGHT * 1.7f), Color.WHITE);
    }

    @Override
    public PointF getCenter() {
        return center;
    }

    @Override
    public float getSize() {
        return radius;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public RectF getBoundsRect() {
        return new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
    }

    @Override
    public void draw(Canvas canvas) {
        // Image
        /*RectF bRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        canvas.drawBitmap(image, null, bRect, null);*/
        Matrix matrix = new Matrix();
        matrix.setRotate(spin, image.getWidth()/2f, image.getHeight()/2f);
        matrix.postTranslate(center.x - image.getWidth()/2f , center.y - image.getHeight()/2f);
        canvas.drawBitmap(image, matrix, null);
    }

    @Override
    public void update(PointF point) {
        if(point.x > 0) {
            center = point;
        } else {
            center.x += dx;
            center.y += dy;

            spin += dx;
            if (spin >= 360 ) {
                spin -= 360;
            } else if (spin <= -360) {
                spin += 360;
            }
        }
        InGameArea = center.x - radius >= 0 && center.x + radius <= Constants.SCREEN_WIDTH && center.y + radius <= Constants.SCREEN_HEIGHT && center.y - radius >= Constants.HEADER_HEIGHT;
    }
}