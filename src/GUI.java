import Backend.*;
import Backend.Process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

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
    private JLabel filePrompt;
    private JLabel throughputLabel;
    private JTextField timeUnit;
    private JTextField fileLocation;
    private JTextArea firstCPUDetails;
    private JTextArea secondCPUDetails;
    private JTextArea systemStats;
    private JTable waitingProcessQueue;
    private JTable processStatisticsTable;
    private JScrollPane processScrollPane;
    private JScrollPane procStatScrollPane;
    private Processor processor;
    private String[] tableColumnNames = {"Process Name", "Service Time"};
    private String[] psTableColumnNames = {"Process Name", "Arrival Time", "Service Time", "Finish Time", "TAT", "nTAT"};
    private int unit = 100;
    private ProcessQueue pq;

    public GUI() {
        javax.swing.SwingUtilities.invokeLater(this::spawnGUI);

    }

    private void spawnGUI() {
        // Create the frame window for the GUI
        this.frame = new JFrame("CS 490 Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exits the program completely upon closing GUI

        // Create the panel on which to place all GUI elements
        this.panel = new JPanel();
        panel.setLayout(null);

        // Create and position start button
        this.startButton = new JButton("Start System");
        startButton.setBounds(30, 80, 125, 35);

        // Create and position pause button
        this.pauseButton = new JButton("Pause System");
        pauseButton.setBounds(175, 80, 125, 35);

        // Create and position label to display the status of the system
        this.statusLabel = new JLabel();
        statusLabel.setText("System is Idle");
        statusLabel.setBounds(320, 80, 125, 35);

        // Create and position label to identify the process queue
        this.queueLabel = new JLabel();
        queueLabel.setText("Waiting Process Queue");
        queueLabel.setBounds(50, 125, 150, 35);

        // Create and position label to identify time unit text box
        this.timeUnitLabel = new JLabel();
        timeUnitLabel.setText("1 time unit =");
        timeUnitLabel.setBounds(265, 125, 125, 35);

        // Create and position a read-only text area to display the details of CPU 1
        this.firstCPUDetails = new JTextArea();
        firstCPUDetails.setBounds(240, 173, 200, 75);
        firstCPUDetails.setBackground(Color.yellow);
        firstCPUDetails.setBorder(BorderFactory.createLineBorder(Color.orange));
        firstCPUDetails.setEditable(false);

        // Create and position a second read-only text area to display the details of CPU 2
        this.secondCPUDetails = new JTextArea();
        secondCPUDetails.setBounds(240, 275, 200, 75);
        secondCPUDetails.setBackground(Color.yellow);
        secondCPUDetails.setBorder(BorderFactory.createLineBorder(Color.orange));
        secondCPUDetails.setEditable(false);

        // Create and position a label to prompt the user to enter their desired file location
        this.filePrompt = new JLabel();
        filePrompt.setText("Input the File Location of the processes: ");
        filePrompt.setBounds(30, 5, 250, 35);

        // Create and position a text field to take user input for a filepath
        this.fileLocation = new JTextField();
        fileLocation.setBounds(30, 33, 270, 25);

        // Create and position a button to confirm the user's filepath entry
        this.fileButton = new JButton("OK");
        fileButton.setBounds(305, 33, 55, 25);

        // Action listener to read from the user's chosen file upon clicking the fileButton
        fileButton.addActionListener(e -> {
            try {
                //this is really dumb code that gets a console input from user for a file, then extracts the data from that file into a ProcessQueue object
                pq = new Reader(new Scanner(fileLocation.getText()).next()).getData();
                System.out.println(pq);
            } catch (FileNotFoundException ex) {
                //print the exception and retire the application
                System.out.println("Unable to open file. Terminating program...");
                System.exit(0);
                return; //code will not compile without this redundant return
            }
        });

        // Create and position a label to display the current throughput
        this.throughputLabel = new JLabel();
        throughputLabel.setBounds(20, 530, 420, 35);

        // Create and position a label to display the unit of time the program uses (milliseconds)
        this.unitLabel = new JLabel();
        unitLabel.setText("ms");
        unitLabel.setBounds(395, 125, 125, 35);

        // Create and position a label to tell the user to hit Enter after entering their desired time
        this.enterPrompt = new JLabel();
        enterPrompt.setFont(new Font("", Font.PLAIN, 10));
        enterPrompt.setText("(Press Enter to Set New Unit)");
        enterPrompt.setBounds(340, 145, 150, 35);

        // Create and position a text field to read the user's time unit input (Default 100)
        this.timeUnit = new JTextField(1);
        timeUnit.setText("100");
        timeUnit.setBounds(340, 130, 50, 25);

        // Action listener that reads in the user's input after they press Enter
        timeUnit.addActionListener(e -> {
            this.unit = Integer.parseInt(timeUnit.getText());

        });

        // Timer to poll every millisecond to check the back end for changes
        Timer t = new Timer(1, e -> {
            if(pq != null) {
                // Convert the processQueue from an ArrayList into a 2d array compatible with a JTable
                ArrayList<Process> procq = pq.getQueue();
                Process[] processQueueArr = procq.toArray(new Process[procq.size()]);
                String[][] processQueue2dArr = new String[processQueueArr.length][2];
                // Populate the 2D array
                for(int i = 0; i < processQueueArr.length; i++) {
                    processQueue2dArr[i][0] = processQueueArr[i].getProcessID();
                    processQueue2dArr[i][1] = String.valueOf(processQueueArr[i].getServiceTime());
                }
                // Create and position JTable responsible for process queue
                this.waitingProcessQueue = new JTable(processQueue2dArr, tableColumnNames);
                this.processScrollPane = new JScrollPane(waitingProcessQueue);
                processScrollPane.setBounds(20, 160, 200, 190);
                panel.add(processScrollPane);

            }

            // Display CPU stats, throughput, and finished process stats
            if(processor != null) {
                // Display CPU stats
                if(processor.isRunning() && !processor.getIsPaused(0)) {
                    // Count down the time remaining for each running process while the processor isn't paused
                    firstCPUDetails.setText(" CPU0 \n Exec: Running\n Time Remaining = " + processor.timeRemaining(0));
                    secondCPUDetails.setText(" CPU1 \n Exec: Running\n Time Remaining = " + processor.timeRemaining(1));
                } else if(processor.isRunning() && processor.getIsPaused(0)) {
                    // Display the CPUs as Idle if the processor is paused
                    firstCPUDetails.setText(" CPU0 \n Exec: Idle\n Time Remaining = " + processor.timeRemaining(0));
                    secondCPUDetails.setText(" CPU1 \n Exec: Idle\n Time Remaining = " + processor.timeRemaining(1));
                } else {
                    // Default display when the process is paused and there is no current running process
                    firstCPUDetails.setText(" CPU0 \n Exec: Idle\n Time Remaining = n/a");
                    secondCPUDetails.setText(" CPU1 \n Exec: Idle\n Time Remaining = n/a");
                    statusLabel.setText("System is Idle");
                }

                // Display throughput and update every tick of the Timer
                throughputLabel.setText("Current Throughput: " + processor.getCurrentThroughput() + " process/unit of time");

                // Display the stats of finished processes
                List<ProcessStatistics> procStats = processor.getProcessStatistics();
                String[][] processStatisticsMatrix = new String[procStats.size()][6]; // Create a 2D array based on procStats, with 6 fields for statistics
                // Populate the 2D array
                for(int i = 0; i < processor.getProcessStatistics().size(); i++)
                {
                    processStatisticsMatrix[i][0] = String.valueOf(procStats.get(i).getProcess().getProcessID());
                    processStatisticsMatrix[i][1] = String.valueOf(procStats.get(i).getArrivalTime());
                    processStatisticsMatrix[i][2] = String.valueOf(procStats.get(i).getServiceTime());
                    processStatisticsMatrix[i][3] = String.valueOf(procStats.get(i).getFinishTime());
                    processStatisticsMatrix[i][4] = String.valueOf(procStats.get(i).getTat());
                    processStatisticsMatrix[i][5] = String.valueOf(procStats.get(i).getNtat());
                }
                // Create and position a JTable responsible for displaying finished processes and their stats
                this.processStatisticsTable = new JTable(processStatisticsMatrix, psTableColumnNames);
                this.procStatScrollPane = new JScrollPane(processStatisticsTable);
                procStatScrollPane.setBounds(5, 375, 550, 150);
                panel.add(procStatScrollPane);

            }

        });
        t.start();

        // Start button - Starts the system and creates a processor, and unpauses a paused system
        startButton.addActionListener(e -> {
            if (processor != null) {
                processor.unpauseSystem();
            }
            else {
                processor = new Processor(2, unit, pq);
                processor.startProcessor();

            }
            statusLabel.setText("System is Running");

        });

        // Pause button - Pauses the system if it is currently running
        pauseButton.addActionListener(e -> {
            if (processor != null && processor.isRunning())
                processor.pauseSystem();
                //firstCPUDetails.setText(" CPU \n Exec: Idle\n Time Remaining = [time]");
            statusLabel.setText("System is Paused");

        });

        // Adds all of the GUI elements to the main panel and frame
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(filePrompt);
        panel.add(fileLocation);
        panel.add(fileButton);
        panel.add(statusLabel);
        panel.add(queueLabel);
        panel.add(timeUnitLabel);
        panel.add(unitLabel);
        panel.add(enterPrompt);
        panel.add(timeUnit);
        panel.add(firstCPUDetails);
        panel.add(secondCPUDetails);
        panel.add(throughputLabel);
        frame.setContentPane(panel); // Sets 'panel' as the content display
        frame.getContentPane().setBackground(Color.lightGray); // Colors the background of the frame gray
        frame.pack();
        frame.setSize(575, 600); // Sets window size to 500x600
        frame.setVisible(true); // Allows everything to be visible
    }

}
