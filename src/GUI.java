import Backend.Processor;
import Backend.ProcessQueue;
import Backend.Process;

import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JButton startButton;
    private JButton pauseButton;
    private JLabel statusLabel;
    private JLabel queueLabel;
    private JTextField timeUnit;
    private JTable waitingProcessQueue;
    private Processor processor;
    private ProcessQueue processQueue;
    private String[] tableColumnNames = {"Process Name", "Service Time"};


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

        this.queueLabel = new JLabel();
        queueLabel.setText("Waiting Process Queue");
        queueLabel.setBounds(50, 75, 150, 35);

        this.timeUnit = new JTextField(1);
        timeUnit.setBounds(320, 75, 125, 35);


        Timer t = new Timer(1, e -> {
            if(processQueue != null) {
                ArrayList<Process> pq = processQueue.getQueue();
                Process[] processQueueArr = pq.toArray(new Process[pq.size()]);
                String[][] processQueue2dArr = new String[processQueueArr.length][2];
                for(int i = 0; i < processQueueArr.length; i++) {
                    processQueue2dArr[i][0] = processQueueArr[i].getProcessID();
                    processQueue2dArr[i][1] = String.valueOf(processQueueArr[i].getServiceTime());
                }
                this.waitingProcessQueue = new JTable(processQueue2dArr, tableColumnNames);
                waitingProcessQueue.setBounds(50, 110, 125, 80);
                panel.add(waitingProcessQueue);
            }
        });
        t.start();

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
        panel.add(queueLabel);
        panel.add(timeUnit);
        frame.setContentPane(panel);
        frame.getContentPane().setBackground(Color.lightGray);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public void setProcessQueue(ProcessQueue processQueue) {
        this.processQueue = processQueue;
    }
}
