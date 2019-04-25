
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;
import javax.swing.JPanel;

/**
 * A canvas that draws movables.
 *
 * Michael Terry & Jeff Avery
 */
public class Canvas extends JPanel {

    private Vector<Movable> movables = new Vector<Movable>(); // All movables we're managing
    private Movable interactiveMovable = null; // Movable with which user is interacting

    public Canvas() {
        // Install our event handlers
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleMousePress(e);
            }

            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    /**
     * Handle mouse press events
     */
    private void handleMousePress(java.awt.event.MouseEvent e) {
        for (Movable m : movables) {
            interactiveMovable = m.getMovableHit(e);
            if (interactiveMovable != null) {
                interactiveMovable.handleMouseDownEvent(e);
                break;
            }
        }
    }

    /**
     * Handle mouse released events
     */
    private void handleMouseReleased(MouseEvent e) {
        if (interactiveMovable != null) {
            interactiveMovable.handleMouseUp(e);
            repaint();
        }
        interactiveMovable = null;
    }

    /**
     * Handle mouse dragged events
     */
    private void handleMouseDragged(MouseEvent e) {
        if (interactiveMovable != null) {
            interactiveMovable.handleMouseDragEvent(e);
            repaint();
        }
    }

    /**
     * Add a top-level m to the canvas
     */
    public void addMovable(Movable s) {
        movables.add(s);
    }

    /**
     * Paint our canvas
     */
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        for (Movable m : movables) {
            m.draw((Graphics2D) g);
        }
    }

    public void reset() {
        for (Movable m : movables) {
            m.reset();
        }
        repaint();
    }

    public PoseState getCurrentPose() {
        PoseState ps = new PoseState();
        for (Movable m : movables) {
            ps.addPart(m);
        }
        return ps;
    }

    public void loadThisPose(PoseState ps) {
        ps.loadStoredPose();
        repaint();
    }

    public boolean savePoseToFile(String path) {
        PoseState ps = new PoseState();
        for (Movable m : movables) {
            ps.addPart(m);
        }
        return ps.saveToFile(path);
    }

    public String loadPoseFromFile(String path) {
        PoseState ps = new PoseState();
        for (Movable m : movables) {
            ps.addPart(m);
        }
        String msg = ps.loadFile(path);
        repaint();
        return msg;
    }
}
