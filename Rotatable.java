import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class Rotatable extends Movable {
    private Ellipse2D object;
    private Point2D originPoint;
    private CoordInfo oldInfo = null;
    private boolean isAttachedToBottom = false;
    private boolean isClickedOnTop = false;
    private double thetaLo = 0;
    private double thetaHi = 0;
    private double oldScaleBind = 1;
    private double oldScaleKeep = 1;

    public Rotatable(String name, double theta, int originX, int originY, int width, int height) {
        super(name, theta, originX, originY);
        object = new Ellipse2D.Double(0, 0, width, height);
    }

    public void setAttachedToBottom() { this.isAttachedToBottom = true; }

    public boolean pointInside(Point2D p) {
        return object.contains(getRelativePos(p));
    }

    protected void drawMovable(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.draw(object);
//        if (DEBUG) g.fillArc((int)attachedPoint.getX() - 4, (int)attachedPoint.getY() - 4, 8, 8, 0, 360);
    }

    public void setRestriction(int lo, int hi) {
        this.thetaLo = lo;
        this.thetaHi = hi;
    }

    public class CoordInfo {
        double deltaX;
        double deltaY;
        boolean isReversed;
        double theta;

        public CoordInfo(Point2D o, Point2D p) {
            deltaX = p.getX() - o.getX();
            deltaY = p.getY() - o.getY();
            isReversed = (deltaY < 0);
            if (isAttachedToBottom) isReversed = !isReversed;
            if (isClickedOnTop) isReversed = !isReversed;
            theta = Math.toDegrees(Math.atan(deltaX / deltaY));
        }
    }

    protected void handleMouseDownEvent(MouseEvent e) {
        super.handleMouseDownEvent(e);

        // Calculate the absolute position of attached point
        AffineTransform fullTransform = this.getFullTransform();
        originPoint = (Point2D)attachedPoint.clone();
        originPoint = fullTransform.transform(originPoint, originPoint);
        if (originPoint.getY() > lastPoint.getY() && !isAttachedToBottom) isClickedOnTop = true;

        // Rotation
        oldTheta = thetaNow;
        oldInfo = new CoordInfo(originPoint, lastPoint);

        // Scaling
        if (!isScalable) return;
        oldScaleFactor = scaleFactor;
        oldScaleFactorX = scaleFactorX;

        oldScaleBind = objBinded.getScaleFactor();
        oldScaleKeep = objKeeped.getScaleFactor();
    }

    protected void handleMouseDragEvent(MouseEvent e) {
        Point2D newPoint = e.getPoint();
        CoordInfo newInfo = new CoordInfo(originPoint, newPoint);

        // Handle Rotation
        boolean limit = false;
        double deg = oldInfo.theta - newInfo.theta;
        if (newInfo.isReversed) deg += 180;
        double newTheta = oldTheta + deg;
        if (newTheta >= 360) newTheta -= 360;
        if (newTheta < 0) newTheta += 360;

        if (thetaLo == 0 || thetaHi == 0 || newTheta >= thetaHi || newTheta <= thetaLo) {
            thetaNow = newTheta;
        } else {
            limit = true;
        }
        if (thetaHi - newTheta < 20 || newTheta - thetaLo < 20) {
            limit = false;
        }
        if (!isScalable) return;

        // Scaling related
        double realAngle = thetaNow;
        if (parent.name.equals("Left Upper Leg") || parent.name.equals("Right Upper Leg")) {
            realAngle += parent.thetaNow;
        }

        double angleRotated = thetaNow - oldTheta;

        if (isScalable && !limit) {
            Point2D mouseDownPt = (Point2D)lastPoint.clone();
            AffineTransform trans = new AffineTransform();
            trans.rotate(Math.toRadians(angleRotated), originPoint.getX(), originPoint.getY());
            mouseDownPt = trans.transform(mouseDownPt, mouseDownPt);

            double dist = Math.sqrt(Math.pow(newPoint.getX() - mouseDownPt.getX(), 2) + Math.pow(newPoint.getY() - mouseDownPt.getY(), 2));
            double oldDistToAP = Math.sqrt(Math.pow(mouseDownPt.getX() - originPoint.getX(), 2) + Math.pow(mouseDownPt.getY() - originPoint.getY(), 2));
            double newDistToAP = Math.sqrt(Math.pow(newPoint.getX() - originPoint.getX(), 2) + Math.pow(newPoint.getY() - originPoint.getY(), 2));
            if (newDistToAP < oldDistToAP) dist = -dist;       // Dragging toward AP

            if (this.name.contains("Upper")) {
                this.setScaleFactorXY(1, oldScaleFactor + (dist / initHeight));
//                this.objBinded.setScaleFactorX(1 / this.scaleFactor);
            } else {
                this.setScaleFactorXY(1, oldScaleFactor + (dist / initHeight));
            }
            this.objKeeped.setScaleFactor(1 / (this.scaleFactor * this.objBinded.scaleFactor));
            this.objKeeped.setScaleFactorX(1 / (this.scaleFactorX * this.objBinded.scaleFactorX));
        }
    }

    protected void handleMouseUp(MouseEvent e) {
        isClickedOnTop = false;
    }

    public void reset() {
        for (Movable m : children) {
            m.reset();
        }
        thetaNow = thetaInit;
        scaleFactor = 1;
    }
}
