import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class Window extends JFrame implements Observer {
    private Canvas c = null;
    private MessageBox msgBox;
    private Window window = this;
    private AnimationMaster master;

    public Window(Model model) {
        setTitle("Animated Ragdoll");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 900);
        setResizable(false);
        setVisible(true);

        model.addObserver(this);
        this.msgBox = new MessageBox(this);

        this.setLayout(new BorderLayout());

        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        jp.add(new MenuBarView(this));
        master = new AnimationMaster();
        jp.add(master);

        this.add(jp, BorderLayout.NORTH);
    }

    public void setCanvas(Canvas c) { this.c = c; this.master.setCanvas(c); }

    public class MenuBarView extends JPanel {
        public MenuBarView(Window w) {
//            setMinimumSize(new Dimension(400, 100));
            setLayout(new GridLayout(0, 1));
            setVisible(true);

            JMenu fileMenu = new JMenu("File");
            JMenuItem resetMI = new JMenuItem("Reset");
            resetMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
            resetMI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (c != null) c.reset();
                }
            });

            JMenuItem saveMI = new JMenuItem("Save");
            saveMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
            saveMI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileDialog fd = new FileDialog(window, "Choose where to save your pose", FileDialog.SAVE);
                    fd.setFile("sample.json");
                    fd.setVisible(true);
                    String filename = fd.getFile();
                    if (filename != null) {
                        String filepath = fd.getDirectory() + filename;
                        boolean isGood = c.savePoseToFile(filepath);
                        if (isGood) {
                            msgBox.infoBox("Successfully saved!");
                        } else {
                            msgBox.errorBox("Failed to save. Please check log.");
                        }
                    }
                }
            });

            JMenuItem loadMI = new JMenuItem("Load");
            loadMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
            loadMI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileDialog fd = new FileDialog(window, "Choose a file", FileDialog.LOAD);
                    fd.setFile("*.json");
                    fd.setVisible(true);
                    String filename = fd.getFile();
                    if (filename != null) {
                        String filepath = fd.getDirectory() + filename;
                        String errorMsg = c.loadPoseFromFile(filepath);
                        if (errorMsg != null) {
                            msgBox.errorBox(errorMsg);
                        }
                    }
                }
            });

            JMenuItem quitMI = new JMenuItem("Quit");
            quitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
            quitMI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int res = msgBox.confirmBox("Are you sure you want to quit?");
                    if (res == JOptionPane.YES_OPTION) {
                        w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
                    }
                }
            });

            fileMenu.add(resetMI);
            fileMenu.add(saveMI);
            fileMenu.add(loadMI);
            fileMenu.addSeparator();
            fileMenu.add(quitMI);

            JMenuBar menuBar = new JMenuBar();
            menuBar.add(fileMenu);
            add(menuBar);
        }
    }

    public void update(Object observable) {
        validate();
        repaint();
    }

    public class MessageBox {
        private Window window;
        public MessageBox(Window window) { this.window = window; }
        public void infoBox(String infoMsg) {
            JOptionPane.showMessageDialog(window, infoMsg, "JSketch", JOptionPane.INFORMATION_MESSAGE);
        }
        public void errorBox(String errorMsg) {
            JOptionPane.showMessageDialog(window, errorMsg, "JSketch", JOptionPane.ERROR_MESSAGE);
        }
        public int confirmBox(String confirmMsg) {
            return JOptionPane.showConfirmDialog(window, confirmMsg, "JSketch", JOptionPane.YES_NO_OPTION);
        }
    }
}
