import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.TimerTask;

public class AnimationMaster extends JPanel {
    private JSlider slider;
    private Hashtable<Integer, JLabel> labels = new Hashtable<>();
    private Hashtable<Integer, PoseState> keyframes = new Hashtable<>();
    private int[] keyFrameTicker = new int[151];
    private int frame = 0;
    private Canvas canvas = null;
    private java.util.Timer timer = new java.util.Timer(true);
    private boolean isPlaying = false;
    private JButton playBtn;
    private JButton pauseBtn;
    private JButton setBtn;
    private JButton clearBtn;

    public AnimationMaster() {
        // Slider for animation
        slider = new JSlider(0, 5 * 30);   // 30fps, 5 sec
        slider.setMajorTickSpacing(30);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setValue(0);

        // Keyframe storage
        labels.put(0, new JLabel("0s"));
        labels.put(30, new JLabel("1s"));
        labels.put(60, new JLabel("2s"));
        labels.put(90, new JLabel("3s"));
        labels.put(120, new JLabel("4s"));
        labels.put(150, new JLabel("5s"));
        slider.setLabelTable(labels);
        Arrays.fill(keyFrameTicker, 0);

        // Timer setting
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int val = slider.getValue();
                if (val >= 150) pause();
                if (isPlaying) {
                    slider.setValue(slider.getValue() + 1);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000 / 30, 1000 / 30);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider src = (JSlider)e.getSource();
                frame = src.getValue();

                if (keyFrameTicker[frame] == 1) {
                    canvas.loadThisPose(keyframes.get(frame));
                    return;
                }
                if (keyframes.size() < 2) return;

                int lastFrame = -1;
                for (int i = frame - 1; i >= 0; --i) {
                    if (keyFrameTicker[i] == 1) {
                        lastFrame = i;
                        break;
                    }
                }

                int nextFrame = -1;
                for (int i = frame + 1; i <= 150; ++i) {
                    if (keyFrameTicker[i] == 1) {
                        nextFrame = i;
                        break;
                    }
                }

                if (lastFrame == -1) {
                    canvas.loadThisPose(keyframes.get(nextFrame));
                } else if (nextFrame == -1) {
                    canvas.loadThisPose(keyframes.get(lastFrame));
                } else {
                    loadInterimPose(
                            keyframes.get(lastFrame), keyframes.get(nextFrame),
                            (frame - lastFrame) * 1.0 / (nextFrame - lastFrame)
                    );
                }
            }
        });

        // Buttons for controlling
        JPanel controlPanel = new JPanel();
        controlPanel.setVisible(true);
        controlPanel.setLayout(new FlowLayout());

        playBtn = new JButton("Play");
        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });

        pauseBtn = new JButton("Pause");
        pauseBtn.setVisible(false);
        pauseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
            }
        });


        setBtn = new JButton("Set Keyframe");
        setBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addKeyFrame(frame);
            }
        });

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearKeyFrame();
            }
        });

        controlPanel.add(playBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(setBtn);
        controlPanel.add(clearBtn);

        setMinimumSize(new Dimension(400, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setVisible(true);

        add(slider);
        add(controlPanel);
    }

    private void play() {
        if (frame == 150) slider.setValue(0);
        isPlaying = true;
        playBtn.setVisible(false);
        pauseBtn.setVisible(true);
        setBtn.setEnabled(false);
        clearBtn.setEnabled(false);
    }

    private void pause() {
        isPlaying = false;
        playBtn.setVisible(true);
        pauseBtn.setVisible(false);
        setBtn.setEnabled(true);
        clearBtn.setEnabled(true);
    }

    public void addKeyFrame(int pos) {
        // Slider tick
        if (pos % 30 == 0) {
            labels.put(pos, new JLabel("(" + pos / 30 + "s)"));
        } else {
            labels.put(pos, new JLabel("|"));
        }
        slider.setLabelTable(labels);
        slider.repaint();

        // Add to hash table
        keyframes.put(pos, canvas.getCurrentPose());
        keyFrameTicker[pos] = 1;
    }

    public void clearKeyFrame() {
        labels = new Hashtable<>();
        labels.put(0, new JLabel("0s"));
        labels.put(30, new JLabel("1s"));
        labels.put(60, new JLabel("2s"));
        labels.put(90, new JLabel("3s"));
        labels.put(120, new JLabel("4s"));
        labels.put(150, new JLabel("5s"));
        slider.setLabelTable(labels);
        slider.repaint();

        // Clear hash table
        keyframes.clear();
        Arrays.fill(keyFrameTicker, 0);
    }

    public void setCanvas(Canvas c) { this.canvas = c; }

    public void loadInterimPose(PoseState p1, PoseState p2, double percentage) {
        PoseState interimPose = new PoseState();
        for (int i = 0; i < 14; ++i) {
            double p1Theta = p1.thetas[i] > 180 ? p1.thetas[i] - 360 : p1.thetas[i];
            double p2Theta = p2.thetas[i] > 180 ? p2.thetas[i] - 360 : p2.thetas[i];
            double thetaDiff = p2Theta - p1Theta;
            double factorDiff = p2.factors[i] - p1.factors[i];
            interimPose.parts[i] = p1.parts[i];
            interimPose.thetas[i] = p1.thetas[i] + thetaDiff * percentage;
            interimPose.factors[i] = p1.factors[i] + factorDiff * percentage;
        }
        double torsoXDiff = p2.torsoX - p1.torsoX;
        double torsoYDiff = p2.torsoY - p1.torsoY;
        interimPose.parts[13] = p1.parts[13];
        interimPose.torsoX = p1.torsoX + torsoXDiff * percentage;
        interimPose.torsoY = p1.torsoY + torsoYDiff * percentage;

        canvas.loadThisPose(interimPose);
    }
}
