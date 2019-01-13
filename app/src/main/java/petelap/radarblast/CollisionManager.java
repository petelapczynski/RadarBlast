package petelap.radarblast;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

public class CollisionManager {

    public CollisionManager() {

    }

    public static boolean GameObjectCollide(IGameObject gob1, IGameObject gob2 ) {
        //bounding box collision
        if (RectF.intersects(gob1.getBoundsRect(), gob2.getBoundsRect())) {
            switch ( gob1.getType() ) {
                case "Square":
                case "Rectangle":
                    switch (gob2.getType()) {
                        case "Obstacle":
                        case "Square":
                        case "Rectangle":
                            return true;
                        case "TriangleUp":
                        case "TriangleDown":
                        case "Rhombus":
                        case "Hexagon":
                            return RectangleCollidePolygon(gob1, gob2);
                        case "Circle":
                            return RectangleCollideCircle(gob1, gob2);
                    }
                    break;
                case "TriangleUp":
                case "TriangleDown":
                case "Rhombus":
                case "Hexagon":
                    switch (gob2.getType()) {
                        case "Obstacle":
                        case "Square":
                        case "Rectangle":
                            return RectangleCollidePolygon(gob2, gob1);
                        case "TriangleUp":
                        case "TriangleDown":
                        case "Rhombus":
                        case "Hexagon":
                            return PolygonCollidePolygon(gob1, gob2);
                        case "Circle":
                            return CircleCollidePolygon(gob2, gob1);
                    }
                    break;
                case "Circle":
                    switch (gob2.getType()) {
                        case "Obstacle":
                        case "Square":
                        case "Rectangle":
                            return RectangleCollideCircle(gob2, gob1);
                        case "TriangleUp":
                        case "TriangleDown":
                        case "Rhombus":
                        case "Hexagon":
                            return CircleCollidePolygon(gob1, gob2);
                        case "Circle":
                            return CircleCollideCircle(gob1, gob2);
                    }
                    break;
            }
        }

        //default
        return false;
    }

    public static boolean GameObjectSpecialCollide(IGameObject gob1, IGameObjectSpecial gob2) {
        //bounding box collision
        if (RectF.intersects(gob1.getBoundsRect(), gob2.getBoundsRect())) {

            switch ( gob1.getType() ) {
                case "Square":
                case "Rectangle":
                    return RectangleCollideSpecial(gob1, gob2);
                case "TriangleUp":
                case "TriangleDown":
                case "Rhombus":
                case "Hexagon":
                    return PolygonCollideSpecial(gob1, gob2);
                case "Circle":
                    return CircleCollideSpecial(gob1, gob2);
            }
        }
        return false;
    }

    private static boolean RectangleCollideCircle(IGameObject gob1, IGameObject gob2) {
        // rect pts in circle
        if ( pointsInGameObject(gob1, gob2) ) {
            return true;
        }

        // circle pts in rect
        if ( pointsInGameObject(gob2, gob1) ) {
            return true;
        }

        // circle pts in rect
        for(int i = 0; i <= 360; i++ ) {
            // convert angle to radian
            double a = Math.toRadians((double)i);
            float x = gob2.getCenter().x + (float)( gob2.getSize() * Math.sin(a) );
            float y = gob2.getCenter().y + (float)( gob2.getSize() * Math.cos(a) );
            // if point is inside rect
            if( gob1.getBoundsRect().contains(x,y) ) {
                return true;
            }
        }
        return gob1.pointInside(gob2.getCenter());
    }

    private static boolean RectangleCollidePolygon(IGameObject gob1, IGameObject gob2) {
        // rect pts in poly
        if ( pointsInGameObject(gob1, gob2) ) {
            return true;
        }

        // poly pts in rect
        if ( pointsInGameObject(gob2, gob1) ) {
            return true;
        }

        // poly lines in rect
        if ( linesInGameObject(gob2, gob1) ) {
            return true;
        }
        return gob1.pointInside(gob2.getCenter());
    }

    private static boolean CircleCollideCircle(IGameObject gob1, IGameObject gob2) {
        float distSq = (gob1.getCenter().x - gob2.getCenter().x) * (gob1.getCenter().x - gob2.getCenter().x) + (gob1.getCenter().y - gob2.getCenter().y) * (gob1.getCenter().y - gob2.getCenter().y);
        float radSumSq = (gob1.getSize() + gob2.getSize()) * (gob1.getSize() + gob2.getSize());
        return distSq <= radSumSq;
    }

    private static boolean CircleCollidePolygon(IGameObject gob1, IGameObject gob2) {
        // circle pts in poly
        if ( pointsInGameObject(gob1, gob2) ) {
            return true;
        }

        // poly pts in circle
        if ( pointsInGameObject(gob2, gob1) ) {
            return true;
        }

        // poly lines in circle
        if ( linesInGameObject(gob2, gob1) ) {
            return true;
        }
        return gob1.pointInside(gob2.getCenter());
    }

    private static boolean PolygonCollidePolygon(IGameObject gob1, IGameObject gob2) {
        // poly pts in poly
        if ( pointsInGameObject(gob1, gob2) ) {
            return true;
        }

        // poly pts in poly
        if ( pointsInGameObject(gob2, gob1) ) {
            return true;
        }

        // poly lines in poly
        if ( linesInGameObject(gob1, gob2) ) {
            return true;
        }

        // poly lines in poly
        if ( linesInGameObject(gob2, gob1) ) {
            return true;
        }

        return gob1.pointInside(gob2.getCenter());
    }

    private static boolean CircleCollideSpecial(IGameObject gob1, IGameObjectSpecial gob2) {
        float distSq = (gob1.getCenter().x - gob2.getCenter().x) * (gob1.getCenter().x - gob2.getCenter().x) + (gob1.getCenter().y - gob2.getCenter().y) * (gob1.getCenter().y - gob2.getCenter().y);
        float radSumSq = (gob1.getSize() + gob2.getSize()) * (gob1.getSize() + gob2.getSize());
        return distSq <= radSumSq;
    }

    private static boolean RectangleCollideSpecial(IGameObject gob1, IGameObjectSpecial gob2) {
        // rect pts in circle
        for (PointF p: gob1.getPoints()) {
            if (gob2.pointInside(p)) {
                return true;
            }
        }

        // circle pts in rect
        for(int i = 0; i <= 360; i++ ) {
            // convert angle to radian
            double a = Math.toRadians((double)i);
            float x = gob2.getCenter().x + (float)( gob2.getSize() * Math.sin(a) );
            float y = gob2.getCenter().y + (float)( gob2.getSize() * Math.cos(a) );
            // if point is inside rect
            if( gob1.getBoundsRect().contains(x,y) ) {
                return true;
            }
        }
        return gob1.pointInside(gob2.getCenter());
    }

    private static boolean PolygonCollideSpecial(IGameObject gob1, IGameObjectSpecial gob2) {
        // poly pts in circle
        for (PointF p: gob1.getPoints()) {
            if (gob2.pointInside(p)) {
                return true;
            }
        }

        // circle pts in poly
        for(int i = 0; i <= 360; i++ ) {
            // convert angle to radian
            double a = Math.toRadians((double)i);
            float x = gob2.getCenter().x + (float)( gob2.getSize() * Math.sin(a) );
            float y = gob2.getCenter().y + (float)( gob2.getSize() * Math.cos(a) );
            // if point is inside rect
            if( gob1.pointInside(new PointF(x,y)) ) {
                return true;
            }
        }
        return gob1.pointInside(gob2.getCenter());
    }

    private static boolean pointsInGameObject(IGameObject gob1, IGameObject gob2) {
        for (PointF p: gob1.getPoints()) {
            if (gob2.pointInside(p)) {
                return true;
            }
        }
        return false;
    }

    private static boolean linesInGameObject(IGameObject gob1, IGameObject gob2) {
        ArrayList<PointF> gob1Pts = gob1.getPoints();
        for (int i = 0; i < gob1Pts.size() - 1; i++) {
            if (i == gob1Pts.size() - 1) {
                if (lineInGameObject(gob1Pts.get(0), gob1Pts.get(i), gob2)) {
                    return true;
                }
            } else {
                if (lineInGameObject(gob1Pts.get(i), gob1Pts.get(i+1), gob2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean lineInGameObject(PointF A, PointF B, IGameObject gob) {
        // A.x must be < than B.x
        PointF Left, Right;
        if (A.x <= B.x) {
            Left = A;
            Right = B;
        } else {
            Left = B;
            Right = A;
        }

        float m = (Left.y - Right.y) / (Left.x - Right.x);
        float b = Left.y - Left.x * m;
        float x, y;

        for (int i = (int)Left.x; i <= (int)Right.x; i++) {
            x = (float)i;
            y = m * (float)i + b;
            PointF p = new PointF(x, y);
            if (gob.pointInside(p)) {
                return true;
            }
        }
        return false;
    }
}