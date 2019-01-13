package petelap.radarblast;

public class Vector2D {
    public float x;
    public float y;

    public Vector2D( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public Vector2D( Vector2D a ) {
        this.x = a.x;
        this.y = a.y;
    }

    public float length() {
        return (float) Math.sqrt( (x * x) + (y * y) );
    }

    public Vector2D normalize(){
        float len = length();
        if (len > 0f || len < 0f) {
            x /= len;
            y /= len;
        }
        return new Vector2D(x, y);
    }

    public float dot(Vector2D v) {
        return x * v.x + y * v.y;
    }

    public Vector2D add( Vector2D a ) {
        x += a.x;
        y += a.y;
        return new Vector2D(x, y);
    }

    public Vector2D subtract( Vector2D a ) {
        x -= a.x;
        y -= a.y;
        return new Vector2D(x, y);
    }

    public Vector2D multiply( Vector2D a ) {
        x *= a.x;
        y *= a.y;
        return new Vector2D(x, y);
    }

    public Vector2D multiply( float scalar ) {
        x *= scalar;
        y *= scalar;
        return new Vector2D(x, y);
    }

    public Vector2D divide( Vector2D a ) {
        x /= a.x;
        y /= a.y;
        return new Vector2D(x, y);
    }

    public Vector2D divide( float scalar ) {
        x /= scalar;
        y /= scalar;
        return new Vector2D(x, y);
    }

    public Vector2D rotate(float degrees) {
        float sin = (float)Math.sin(Math.toRadians(degrees));
        float cos = (float)Math.cos(Math.toRadians(degrees));

        float tx = x;
        float ty = y;
        x = (cos * tx) - (sin * ty);
        y = (sin * tx) + (cos * ty);
        return new Vector2D(x, y);
    }

    public Vector2D reflect(Vector2D normal) {
        //Reflect: -2*(V dot N)*N + V
        //equation: http://wiki.roblox.com/index.php?title=User:EgoMoose/The_scary_thing_known_as_the_dot_product
        return subtract(normal.multiply(2f * dot(normal)));
    }

}
