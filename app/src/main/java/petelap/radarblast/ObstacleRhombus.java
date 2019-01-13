package petelap.radarblast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.ArrayList;

public class ObstacleRhombus extends ObstacleShapes implements IGameObject {
    private float width;
    private float height;
    private Path path;
    private PointF Left, Top, Right, Bottom;

    public ObstacleRhombus(float width, float height, float startX, float startY, int color) {
        this.width = width;
        this.height = height;
        this.color = color;

        center = new PointF(startX, startY);
        size = width / 2.0f;
        type = "Rhombus";
        InGameArea = true;
        pop = false;

        Left = new PointF(startX, startY);
        Top = new PointF(startX,startY);
        Right = new PointF(startX, startY);
        Bottom = new PointF(startX, startY);
        rect = new RectF(Left.x, Top.y, Right.x, Bottom.y);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Top.x, Top.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(Bottom.x, Bottom.y);
        path.lineTo(Left.x, Left.y);
        path.close();

        // Rhombus
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (size+1)*4, this.color, Color.BLACK, Shader.TileMode.MIRROR));
            paint.setAntiAlias(true);
        }
        // Border
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(3);
        bPaint.setColor(Color.BLACK);
        bPaint.setAntiAlias(true);
    }

    @Override
    public void grow(float speed) {
        width = width + (speed * 2);
        height = width * 1.732f;
        size = width / 2.0f;

        Left = new PointF((center.x - size), center.y );
        Top = new PointF(center.x, (center.y - (height/2.0f)));
        Right = new PointF((center.x + size), center.y );
        Bottom = new PointF(center.x, (center.y + (height/2.0f)));
        rect = new RectF(Left.x, Top.y, Right.x, Bottom.y);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Top.x, Top.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(Bottom.x, Bottom.y);
        path.lineTo(Left.x, Left.y);
        path.close();
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x,center.y, (size+1)*4, color, Color.BLACK, Shader.TileMode.MIRROR));
        }
        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && Top.y >= Constants.HEADER_HEIGHT && Bottom.y <= Constants.SCREEN_HEIGHT;
    }

    @Override
    public void setPaint(Paint ovrPaint) {
        paint = ovrPaint;
    }

    @Override
    public IGameObject NewInstance() {
        return GetInstance();
    }

    @Override
    public boolean InGameArea() {
        return InGameArea;
    }

    @Override
    public boolean pointInside(PointF point) {
        if (IsPointInTri(point, Left, Top, Right)) {
            return true;
        }
        return IsPointInTri(point, Left, Bottom, Right);
    }

    @Override
    public void pop() {
        pop = true;
        InGameArea = false;
    }

    @Override
    public float getArea() {
        // area of rhombus = (d1*d2)/2
        return (width * height)/2f;
    }

    public static IGameObject GetInstance() {
        return new ObstacleRhombus(1,1, 0,0, Color.parseColor(Common.getPreferenceString("color_Rhombus")));
    }

    @Override
    public PointF getCenter() {
        return center;
    }

    @Override
    public float getSize() {
        return size;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public RectF getBoundsRect() {
        return rect;
    }

    @Override
    public ArrayList<PointF> getPoints() {
        ArrayList<PointF> points = new ArrayList<>();
        points.add(Left);
        points.add(Top);
        points.add(Right);
        points.add(Bottom);
        return points;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
        canvas.drawPath(path, bPaint);
    }

    @Override
    public void update(PointF point) {
        center = point;

        Left = new PointF((center.x - width / 2.0f), (center.y + (height / 2.0f)));
        Top = new PointF(center.x, (center.y - (height / 2.0f)));
        Right = new PointF((center.x + width / 2.0f), (center.y + (height / 2.0f)));
        Bottom = new PointF(center.x, (center.y + (height / 2.0f)));
        rect = new RectF(Left.x, Top.y, Right.x, Bottom.y);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(Left.x, Left.y);
        path.lineTo(Top.x, Top.y);
        path.lineTo(Right.x, Right.y);
        path.lineTo(Bottom.x, Bottom.y);
        path.lineTo(Left.x, Left.y);
        path.close();
        if (Constants.SHAPE_GRADIENT) {
            paint.setShader(new RadialGradient(center.x, center.y, (size + 1) * 4, color, Color.BLACK, Shader.TileMode.MIRROR));
        }
        InGameArea = Left.x >= 0 && Right.x <= Constants.SCREEN_WIDTH && Top.y >= Constants.HEADER_HEIGHT && Bottom.y <= Constants.SCREEN_HEIGHT;
    }

    private static float Sign(PointF p1, PointF p2, PointF p3)
    {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    private static boolean IsPointInTri(PointF pt, PointF v1, PointF v2, PointF v3)
    {
        boolean b1, b2, b3;

        b1 = Sign(pt, v1, v2) < 0.0f;
        b2 = Sign(pt, v2, v3) < 0.0f;
        b3 = Sign(pt, v3, v1) < 0.0f;

        return ((b1 == b2) && (b2 == b3));
    }
}