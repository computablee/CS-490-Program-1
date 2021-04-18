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
    private JLabel cpuZeroQueueLabel;
    private JLabel cpuOneQueueLabel;
    private JLabel timeUnitLabel;
    private JLabel unitLabel;
    private JLabel timeQuantumLabel;
    private JLabel enterPrompt;
    private JLabel enterPromptQuantum;
    private JLabel filePrompt;
    private JLabel cpuZeroNTATLabel;
    private JLabel cpuOneNTATLabel;
    private JTextField timeUnit;
    private JTextField timeQuantum;
    private JTextField fileLocation;
    private JTextArea firstCPUDetails;
    private JTextArea secondCPUDetails;
    private JTable cpuZeroProcessQueue;
    private JTable cpuOneProcessQueue;
    private JTable cpuZeroProcessStats;
    private JTable cpuOneProcessStats;
    private JScrollPane cpuZeroScrollPane;
    private JScrollPane cpuOneScrollPane;
    private JScrollPane cpuZeroProcStatScrollPane;
    private JScrollPane cpuOneProcStatScrollPane;
    private Processor processor;
    private String[] tableColumnNames = {"Process Name", "Service Time"};
    private String[] psTableColumnNames = {"Process Name", "Arrival Time", "Service Time", "Finish Time", "TAT", "nTAT"};
    private int unit = 100;
    private float cpuZeroAvgNTAT;
    private float cpuOneAvgNTAT;
    private ArrayList<ProcessQueue> pq;

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
        startButton.setBounds(500, 33, 125, 35);

        // Create and position pause button
        this.pauseButton = new JButton("Pause System");
        pauseButton.setBounds(650, 33, 125, 35);

        // Create and position label to display the status of the system
        this.statusLabel = new JLabel();
        statusLabel.setText("System is Idle");
        statusLabel.setBounds(595, 5, 125, 35);

        // Create and position label to identify the process queue for CPU 0
        this.cpuZeroQueueLabel = new JLabel();
        cpuZeroQueueLabel.setText("Waiting Process Queue");
        cpuZeroQueueLabel.setBounds(50, 75, 150, 35);

        // Create and position label to identify the process queue for CPU 1
        this.cpuOneQueueLabel = new JLabel();
        cpuOneQueueLabel.setText("Waiting Process Queue");
        cpuOneQueueLabel.setBounds(630, 75, 150, 35);

        // Create and position a read-only text area to display the details of CPU 0
        this.firstCPUDetails = new JTextArea();
        firstCPUDetails.setBounds(240, 110, 200, 75);
        firstCPUDetails.setBackground(Color.yellow);
        firstCPUDetails.setBorder(BorderFactory.createLineBorder(Color.orange));
        firstCPUDetails.setEditable(false);

        // Create and position a second read-only text area to display the details of CPU 1
        this.secondCPUDetails = new JTextArea();
        secondCPUDetails.setBounds(820, 110, 200, 75);
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

        // Create and position a label to display the current average NTAT of CPU 0
        this.cpuZeroNTATLabel = new JLabel();
        cpuZeroNTATLabel.setBounds(20, 525, 420, 35);

        // Create and position a label to display the current average NTAT of CPU 1
        this.cpuOneNTATLabel = new JLabel();
        cpuOneNTATLabel.setBounds(600, 525, 420, 35);

        // Create and position label to identify time unit text box
        this.timeUnitLabel = new JLabel();
        timeUnitLabel.setText("1 time unit =");
        timeUnitLabel.setBounds(1000, 20, 125, 35);

        // Create and position a label to display the unit of time the program uses (milliseconds)
        this.unitLabel = new JLabel();
        unitLabel.setText("ms");
        unitLabel.setBounds(1130, 20, 125, 35);

        // Create and position a label to tell the user to hit Enter after entering their desired time
        /*this.enterPrompt = new JLabel();
        enterPrompt.setFont(new Font("", Font.PLAIN, 10));
        enterPrompt.setText("(Press Enter to Set New Unit)");
        enterPrompt.setBounds(1015, 40, 150, 35);*/

        // Create and position a text field to read the user's time unit input (Default 100)
        this.timeUnit = new JTextField(1);
        timeUnit.setText("100");
        timeUnit.setBounds(1075, 25, 50, 25);

        this.timeQuantumLabel = new JLabel();
        timeQuantumLabel.setText("<html>Round Robin Time Quantum: </html>");
        timeQuantumLabel.setBounds(820, 190, 100, 35);

        this.timeQuantum = new JTextField(1);
        timeQuantum.setText("1");
        timeQuantum.setBounds(920, 195, 50, 25);

        /*this.enterPromptQuantum = new JLabel();
        enterPromptQuantum.setFont(new Font("", Font.PLAIN, 10));
        enterPromptQuantum.setText("(Press Enter to Set New Quantum)");
        enterPromptQuantum.setBounds(820, 215, 175, 35);*/

        // Timer to poll every millisecond to check the back end for changes
        Timer t = new Timer(1, e -> {
            if(pq != null) {
                // Convert CPU0 process queue from an ArrayList into a 2D array compatible with a JTable
                ArrayList<Process> cpuZeroProcQ = pq.get(0).getQueue();
                Process[] cpuZeroPQArr = cpuZeroProcQ.toArray(new Process[cpuZeroProcQ.size()]);
                String[][] cpuZeroPQMatrix = new String[cpuZeroPQArr.length][2];
                // Populate the 2D array
                for(int i = 0; i < cpuZeroPQArr.length; i++) {
                    cpuZeroPQMatrix[i][0] = cpuZeroPQArr[i].getProcessID();
                    cpuZeroPQMatrix[i][1] = String.valueOf(cpuZeroPQArr[i].getServiceTime());
                }

                // Convert CPU 1 process queue from an ArrayList into a 2D array compatible with a JTable
                ArrayList<Process> cpuOneProcQ = pq.get(1).getQueue();
                Process[] cpuOnePQArr = cpuOneProcQ.toArray(new Process[cpuOneProcQ.size()]);
                String[][] cpuOnePQMatrix = new String[cpuOnePQArr.length][2];
                // Populate the 2D array
                for(int i = 0; i < cpuOnePQArr.length; i++) {
                    cpuOnePQMatrix[i][0] = cpuOnePQArr[i].getProcessID();
                    cpuOnePQMatrix[i][1] = String.valueOf(cpuOnePQArr[i].getServiceTime());
                }
                
                // Create and position JTable responsible for CPU 0 process queue
                this.cpuZeroProcessQueue = new JTable(cpuZeroPQMatrix, tableColumnNames);
                this.cpuZeroScrollPane = new JScrollPane(cpuZeroProcessQueue);
                cpuZeroScrollPane.setBounds(20, 110, 200, 190);
                panel.add(cpuZeroScrollPane);

                // Create and position JTable responsible for CPU 1 process queue
                this.cpuOneProcessQueue = new JTable(cpuOnePQMatrix, tableColumnNames);
                this.cpuOneScrollPane = new JScrollPane(cpuOneProcessQueue);
                cpuOneScrollPane.setBounds(600, 110, 200, 190);
                panel.add(cpuOneScrollPane);

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



                // Display the stats of CPU 0'S finished processes
                List<ProcessStatistics> cpuZeroProcStats = processor.getProcessStatistics(0);
                String[][] cpuZeroProcStatsMatrix = new String[cpuZeroProcStats.size()][6]; // Create a 2D array based on cpuZeroProcStats, with 6 fields for statistics
                // Populate the 2D array
                float cpuZeroNTATSum = 0.0f;
                for(int i = 0; i < processor.getProcessStatistics(0).size(); i++)
                {
                    cpuZeroProcStatsMatrix[i][0] = String.valueOf(cpuZeroProcStats.get(i).getProcess().getProcessID());
                    cpuZeroProcStatsMatrix[i][1] = String.valueOf(cpuZeroProcStats.get(i).getArrivalTime());
                    cpuZeroProcStatsMatrix[i][2] = String.valueOf(cpuZeroProcStats.get(i).getServiceTime());
                    cpuZeroProcStatsMatrix[i][3] = String.valueOf(cpuZeroProcStats.get(i).getFinishTime());
                    cpuZeroProcStatsMatrix[i][4] = String.valueOf(cpuZeroProcStats.get(i).getTat());
                    cpuZeroProcStatsMatrix[i][5] = String.valueOf(cpuZeroProcStats.get(i).getNtat());
                    cpuZeroNTATSum += cpuZeroProcStats.get(i).getNtat();
                }

                this.cpuZeroAvgNTAT = cpuZeroNTATSum / processor.getProcessStatistics(0).size();

                // Display average NTAT of CPU 0 and update every tick of the Timer
                cpuZeroNTATLabel.setText("Current Average NTAT: " + cpuZeroAvgNTAT);

                List<ProcessStatistics> cpuOneProcStats = processor.getProcessStatistics(1);
                String[][] cpuOneProcStatsMatrix = new String[cpuOneProcStats.size()][6];
                // Populate the 2D Array
                float cpuOneNTATSum = 0.0f;
                for(int i = 0; i < processor.getProcessStatistics(1).size(); i++)
                {
                    cpuOneProcStatsMatrix[i][0] = String.valueOf(cpuOneProcStats.get(i).getProcess().getProcessID());
                    cpuOneProcStatsMatrix[i][1] = String.valueOf(cpuOneProcStats.get(i).getArrivalTime());
                    cpuOneProcStatsMatrix[i][2] = String.valueOf(cpuOneProcStats.get(i).getServiceTime());
                    cpuOneProcStatsMatrix[i][3] = String.valueOf(cpuOneProcStats.get(i).getFinishTime());
                    cpuOneProcStatsMatrix[i][4] = String.valueOf(cpuOneProcStats.get(i).getTat());
                    cpuOneProcStatsMatrix[i][5] = String.valueOf(cpuOneProcStats.get(i).getNtat());
                    cpuOneNTATSum += cpuOneProcStats.get(i).getNtat();
                }

                this.cpuOneAvgNTAT = cpuOneNTATSum / processor.getProcessStatistics(1).size();

                // Display average NTAT of CPU 0 and update every tick of the Timer
                cpuOneNTATLabel.setText("Current Average NTAT: " + cpuOneAvgNTAT);
                
                // Create and position a JTable responsible for displaying finished processes and their stats
                this.cpuZeroProcessStats = new JTable(cpuZeroProcStatsMatrix, psTableColumnNames);
                this.cpuZeroProcStatScrollPane = new JScrollPane(cpuZeroProcessStats);
                cpuZeroProcStatScrollPane.setBounds(20, 375, 550, 150);
                panel.add(cpuZeroProcStatScrollPane);

                this.cpuOneProcessStats = new JTable(cpuOneProcStatsMatrix, psTableColumnNames);
                this.cpuOneProcStatScrollPane = new JScrollPane(cpuOneProcessStats);
                cpuOneProcStatScrollPane.setBounds(600, 375, 550, 150);
                panel.add(cpuOneProcStatScrollPane);

            }

        });
        t.start();

        // Start button - Starts the system and creates a processor, and unpauses a paused system
        startButton.addActionListener(e -> {
            if (processor != null) {
                processor.unpauseSystem();
            }
            else {
                this.unit = Integer.parseInt(timeUnit.getText());
                processor = new Processor(2, unit, pq);
                processor.startProcessor();
                processor.setRRTimeQuantum(Integer.parseInt(timeQuantum.getText()));
                System.out.println(unit);
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
        panel.add(cpuZeroQueueLabel);
        panel.add(cpuOneQueueLabel);
        panel.add(timeUnitLabel);
        panel.add(unitLabel);
        // panel.add(enterPrompt);
        // panel.add(enterPromptQuantum);
        panel.add(timeUnit);
        panel.add(timeQuantumLabel);
        panel.add(timeQuantum);
        panel.add(firstCPUDetails);
        panel.add(secondCPUDetails);
        panel.add(cpuZeroNTATLabel);
        panel.add(cpuOneNTATLabel);
        frame.setContentPane(panel); // Sets 'panel' as the content display
        frame.getContentPane().setBackground(Color.lightGray); // Colors the background of the frame gray
        frame.pack();
        frame.setSize(1190, 600); // Sets window size to 500x600
        frame.setVisible(true); // Allows everything to be visible
    }

}
