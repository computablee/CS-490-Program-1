import Backend.Processor;

import javax.swing.*;
import java.awt.Color;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JButton startButton;
    private JButton pauseButton;
    private JLabel statusLabel;
    private Processor processor;

    public GUI() {
        javax.swing.SwingUtilities.invokeLater(this::spawnGUI);
        this.processor = null;
    }

    private void spawnGUI() {
        this.frame = new JFrame("CS 490 Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel = new JPanel();
        panel.setLayout(null);

        this.startButton = new JButton("Start System");
        startButton.setBounds(30, 5, 125, 35);


        this.pauseButton = new JButton("Pause System");
        pauseButton.setBounds(175, 5, 125, 35);

        this.statusLabel = new JLabel();
        statusLabel.setText("System is Paused");
        statusLabel.setBounds(320, 5, 125, 35);

        startButton.addActionListener(e -> {
            if (processor != null && !processor.isRunning()) {
                processor.startProcessor();
                statusLabel.setText("System is Running");
            }
            else if (processor != null) {
                processor.unpauseSystem();
                statusLabel.setText("System is Running");
            }
            else {
                statusLabel.setText("ERR: No input file");
            }

            // add more functionality here once i know wtf is going on
        });

        pauseButton.addActionListener(e -> {
            if (processor != null)
                processor.pauseSystem();

            statusLabel.setText("System is Paused");
            // add more functionality here once i know wtf is going on
        });

        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(statusLabel);
        frame.setContentPane(panel);
        frame.getContentPane().setBackground(Color.lightGray);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }
}
