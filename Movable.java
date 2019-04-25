import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Vector;

public abstract class Movable {
    // For debug purpose only
    protected static boolean DEBUG = false;

    protected String name;
    protected Movable parent = null;
    protected Vector<Movable> children = new Vector<Movable>();
    protected AffineTransform transform = new AffineTransform();
    protected Point2D lastPoint = null;

    // For ROTATION purpose
    protected double thetaInit = 0;
    protected double thetaNow = 0;
    protected Point2D attachedPoint = new Point2D.Double(0, 0);
    protected double oldTheta = 0;

    // For SCALING purpose
    protected boolean isScalable = false;
    protected double scaleFactor = 1;
    protected double scaleFactorX = 1;
    protected double oldScaleFactor = 1;
    protected double oldScaleFactorX = 1;
    protected int initHeight = 0;
    protected Movable objBinded = null;
    protected Movable objKeeped = null;

    public Movable(String name) { this.name = name; }

    // Rotatable Constructor
    public Movable(String name, double thetaInit, int originX, int originY) {
        this.name = name;
        if (thetaInit < 0) thetaInit += 360;
        this.thetaInit = thetaInit;
        this.thetaNow = thetaInit;
        this.attachedPoint = new Point2D.Double(originX, originY);
    }

    public void addChild(Movable child) {
        children.add(child);
        child.setParent(this);
    }

    public Movable getParent() { return parent; }
    protected void setParent(Movable parent) { this.parent = parent; }

    public void setTheta(double theta) { this.thetaNow = theta; }

    public double getScaleFactor() { return this.scaleFactor; }
    public void setScaleFactor(double f) { this.scaleFactor = f; }
    public void setScaleFactorX(double x) { this.scaleFactorX = x; }
    public void setScaleFactorXY(double x, double y) {
        this.scaleFactorX = x;
        this.scaleFactor = y;
    }

    public Point2D getPosition() {
        Point2D pos = new Point2D.Double(0, 0);
        if (!this.name.equals("Torso")) return pos;
        transform.transform(pos, pos);
        return pos;
    }

    public void setPosition(double x, double y) {
        transform = new AffineTransform();
        transform(AffineTransform.getTranslateInstance(x, y));
    }

    public static void enableDebug() { DEBUG = true; }
    public void debug(String msg) {
        if (DEBUG) System.out.println("[" + this.name + "] " + msg);
    }

    public void enableScaling(int height, Movable toBind, Movable toKeep) {
        this.isScalable = true;
        this.initHeight = height;
        this.objBinded = toBind;
        this.objKeeped = toKeep;
    }

    public abstract boolean pointInside(Point2D p);

    protected void handleMouseDownEvent(MouseEvent e) {
        lastPoint = e.getPoint();
        if (e.getButton() != MouseEvent.BUTTON1) return;
    }

    protected void handleMouseDragEvent(MouseEvent e) {
        Point2D oldPoint = lastPoint;
        Point2D newPoint = e.getPoint();

        double x_diff = newPoint.getX() - oldPoint.getX();
        double y_diff = newPoint.getY() - oldPoint.getY();
        transform.translate(x_diff, y_diff);

        // Save our last point, if it's needed next time around
        lastPoint = e.getPoint();
    }

    protected void handleMouseUp(MouseEvent e) {
        // interactionMode = InteractionMode.IDLE;
        // Do any other interaction handling necessary here
    }

    public Movable getMovableHit(MouseEvent e) {
        for (Movable m : children) {
            Movable s = m.getMovableHit(e);
            if (s != null) {
                return s;
            }
        }
        if (this.pointInside(e.getPoint())) {
            return this;
        }
        return null;
    }

    /**
     * Returns the full transform to this object from the root
     */
    public AffineTransform getFullTransform() {
        AffineTransform returnTransform = new AffineTransform();
        Movable curMovable = this;
        while (curMovable != null) {
            returnTransform.preConcatenate(curMovable.getLocalTransform());
            curMovable = curMovable.getParent();
        }
        return returnTransform;
    }

    // Get the relative coordinate of a point
    protected Point2D getRelativePos(Point2D p) {
        AffineTransform inverseTransform = getTransform();
        Point2D newPoint = (Point2D)p.clone();
        inverseTransform.transform(newPoint, newPoint);
        return newPoint;
    }

    protected AffineTransform getTransform() {
        AffineTransform fullTransform = this.getFullTransform();
        AffineTransform inverseTransform = null;
        try {
            inverseTransform = fullTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return inverseTransform;
    }

    protected AffineTransform getLocalScaleTransform() {
        AffineTransform t = new AffineTransform();
        t.concatenate(AffineTransform.getScaleInstance(scaleFactorX, scaleFactor));
        return t;
    }

    /**
     * Returns our local transform
     */
    public AffineTransform getLocalTransform() {
        AffineTransform t = (AffineTransform)transform.clone();
        if (thetaNow != 0) {
            t.concatenate(AffineTransform.getRotateInstance(
                    Math.toRadians(thetaNow), attachedPoint.getX(), attachedPoint.getY()
            ));
        }
        if (scaleFactor != 1 || scaleFactorX != 1) {
            t.concatenate(AffineTransform.getScaleInstance(scaleFactorX, scaleFactor));
        }
        return t;
    }

    /**
     * Performs an arbitrary transform on this sprite
     */
    public void transform(AffineTransform t) {
        transform.concatenate(t);
    }

    /**
     * Draws the sprite. This method will call drawSprite after
     * the transform has been set up for this sprite.
     */
    public void draw(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();

        // Set to our transform
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform currentAT = g2.getTransform();
        currentAT.concatenate(this.getFullTransform());
        g2.setTransform(currentAT);

        // Draw the sprite (delegated to sub-classes)
        this.drawMovable(g);

        // Restore original transform
        g.setTransform(oldTransform);

        // Draw children
        for (Movable m : children) {
            m.draw(g);
        }
    }

    /**
     * The method that actually does the sprite drawing. This method
     * is called after the transform has been set up in the draw() method.
     * Sub-classes should override this method to perform the drawing.
     */
    protected abstract void drawMovable(Graphics2D g);

    public String toString() { return "[" + this.name + "] Theta = " + thetaNow + ", Factor = " + scaleFactor; }

    public void reset() {
        transform = AffineTransform.getTranslateInstance(600, 150);
        for (Movable m : children) {
            m.reset();
        }
    }

}
