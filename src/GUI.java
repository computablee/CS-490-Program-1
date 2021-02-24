import Backend.Processor;
import Backend.ProcessQueue;
import Backend.Process;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton fileButton;
    private JLabel statusLabel;
    private JLabel queueLabel;
    private JLabel timeUnitLabel;
    private JLabel unitLabel;
    private JLabel enterPrompt;
    private JTextField timeUnit;
    private JTextArea processDetails;
    private JTextArea systemStats;
    private JTable waitingProcessQueue;
    private JScrollPane processScrollPane;
    private Processor processor;
    private ProcessQueue processQueue;
    private String[] tableColumnNames = {"Process Name", "Service Time"};
    private int unit = 100;


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

        this.timeUnitLabel = new JLabel();
        timeUnitLabel.setText("1 time unit =");
        timeUnitLabel.setBounds(265, 75, 125, 35);

        this.processDetails = new JTextArea();
        processDetails.setBounds(240, 123, 200, 75);
        processDetails.setBackground(Color.yellow);
        processDetails.setBorder(BorderFactory.createLineBorder(Color.orange));
        processDetails.setEditable(false);
        // Don't know what this needs to display
        // processDetails.append to add things here

        this.systemStats = new JTextArea();
        systemStats.setBounds(20, 225, 420, 150);
        systemStats.setBorder(BorderFactory.createLineBorder(Color.black));
        systemStats.setEditable(false);
        // Don't know what this needs to display
        // systemStats.appends to add things here

        this.unitLabel = new JLabel();
        unitLabel.setText("ms");
        unitLabel.setBounds(395, 75, 125, 35);

        this.enterPrompt = new JLabel();
        enterPrompt.setFont(new Font("", Font.PLAIN, 10));
        enterPrompt.setText("(Press Enter to Set New Unit)");
        enterPrompt.setBounds(340, 97, 150, 35);

        this.timeUnit = new JTextField(1);
        timeUnit.setText("100");
        timeUnit.setBounds(340, 75, 50, 35);
        timeUnit.addActionListener(e -> {
            // Make this change the millisecsPerTime (Processor.java) parameter somehow
            // This action listener will trigger when the user hits Enter, btw
            this.unit = Integer.parseInt(timeUnit.getText());
            // System.out.println(unit);

        });



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
                this.processScrollPane = new JScrollPane(waitingProcessQueue);
                processScrollPane.setBounds(20, 110, 200, 100);
                panel.add(processScrollPane);

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

        });

        pauseButton.addActionListener(e -> {
            if (processor != null)
                processor.pauseSystem();

            statusLabel.setText("System is Paused");

        });

        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(statusLabel);
        panel.add(queueLabel);
        panel.add(timeUnitLabel);
        panel.add(unitLabel);
        panel.add(enterPrompt);
        panel.add(timeUnit);
        panel.add(processDetails);
        panel.add(systemStats);
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

    public int getTimeUnit() {
        return this.unit;
    }
}
