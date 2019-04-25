import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class Torso extends Movable {
    private RoundRectangle2D torso = null;

    public Torso(String name, int width, int height, int arc) {
        super(name);
        init(width, height, arc);
    }

    private void init(int width, int height, int arc) {
        torso = new RoundRectangle2D.Double(0, 0, (double)width, (double)height, (double)arc, (double)arc);
    }

    public boolean pointInside(Point2D p) {
        AffineTransform fullTransform = this.getFullTransform();
        AffineTransform inverseTransform = null;
        try {
            inverseTransform = fullTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        Point2D newPoint = (Point2D)p.clone();
        inverseTransform.transform(newPoint, newPoint);
        return torso.contains(newPoint);
    }

    protected void drawMovable(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.draw(torso);
    }
}
