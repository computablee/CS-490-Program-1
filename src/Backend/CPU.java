package Backend;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class to represent an individual CPU
 *
 * @author Lane Allen
 */
public class CPU implements Runnable {
    //queue associated with this CPU
    private ProcessQueue queue;
    //CPU ID
    private final Integer CPUNum;
    //thread for running the CPU
    private Thread t;
    //current executing process
    private Process currProcess;
    //milliseconds per unit of time
    private int millisecsPerTime;
    //whether or not this specific CPU is running any processes
    private boolean isRunning;
    //time left remaining in process
    private Integer timeLeft;
    //time that the CPU started
    private long startTime;
    //is paused
    private boolean isPaused;
    //shared process statistics ArrayList
    private Queue<ProcessStatistics> processStatistics;
    //current processor time, element in the RR schedule, the RR time quantum, and the amount of time currently remaining in the RR queue
    int currentTime, currentRRElem, rrTimeQuantum, rrTimeRemaining;

    /**
     * Constructor
     *
     * @param num CPU ID on processor
     * @param millisecsPerTime Milliseconds per unit of time
     */
    public CPU(int num, int millisecsPerTime, Queue<ProcessStatistics> processStatistics) {
        //this is standard constructor stuff, I don't feel like I need to explain
        this.queue = null;
        this.CPUNum = num;
        this.currProcess = null;
        this.t = null;
        this.millisecsPerTime = millisecsPerTime;
        this.timeLeft = null;
        this.startTime = 0;
        this.isPaused = true;
        this.processStatistics = processStatistics;
        this.currentTime = 0;
        this.currentRRElem = -1;
        this.rrTimeQuantum = 1;
        this.rrTimeRemaining = this.rrTimeQuantum;
    }

    /**
     * Assign a specific queue to this CPU
     *
     * @param pq The process queue to assign
     */
    public void assignQueue(ProcessQueue pq) {
        this.queue = pq;
    }

    /**
     * Sets the RR time quantum for this CPU
     *
     * @param q Time quantum in units of time
     */
    public void setRRTimeQuantum(int q) {
        this.rrTimeQuantum = q;
        this.rrTimeRemaining = this.rrTimeQuantum;
    }

    /**
     * Gets the number of processes in the queue (such that their arrival time is <= the current time
     *
     * @return Number of processes in the queue
     */
    private int inQueueProcesses() {
        int total = 0;
        for (int i = 0; i < queue.count(); i++)
            if (queue.get(i).getArrivalTime() <= currentTime)
                total++;
        return total;
    }

    /**
     * Gets the next process to execute depending on the type of queue (HRRN or Round Robin)
     *
     * @return The next process to execute
     * @throws InterruptedException idk this is something for Thread.sleep
     */
    private Process getNextProcess() throws InterruptedException {
        //if the queue is empty, return null
        if (queue.count() == 0) return null;

        //if HRRN
        if (queue.getQueueOrdering() == QueueOrdering.HRRN) {
            if (currProcess != null) {
                //current process statistics
                ProcessStatistics currProcessStatistics = new ProcessStatistics(currProcess);
                //set some process statistics
                currProcessStatistics.setFinishTime(currentTime);
                currProcessStatistics.setTat(currentTime - currProcess.getArrivalTime());
                currProcessStatistics.setNtat((float) currProcessStatistics.getTat() / (float) currProcess.getServiceTime());

                processStatistics.add(currProcessStatistics);
            }

            //Create an array of response ratios
            ArrayList<Double> responseRatio = new ArrayList<>();

            //Calculate response ratios for each process
            for (int i = 0; i < queue.count(); i++) {
                Process p = queue.get(i);
                //if the process hasn't arrived yet, set its response ratio to -1
                if (p.getArrivalTime() > currentTime)
                    responseRatio.add(-1.0);
                    //else calculate its response ratio
                else
                    responseRatio.add(((double) currentTime - (double) p.getArrivalTime() + (double) p.getServiceTime()) / (double) p.getServiceTime());
            }

            int maxElem = -1;
            double max = 0;

            //find the max
            for (int i = 0; i < queue.count(); i++) {
                if (responseRatio.get(i) > max) {
                    max = responseRatio.get(i);
                    maxElem = i;
                }
            }

            //if the process isn't the system yet
            if (max < 1.0) {
                //sleep for the designated milliseconds
                Thread.sleep(millisecsPerTime);
                //increment current time
                currentTime++;
                //try again
                return null;
            } else {
                //get the process
                Process retProc = queue.get(maxElem);
                //remove it from the queue
                queue.removeProcessAt(maxElem);
                //and return it
                return retProc;
            }
        }
        //if round robin
        else if (queue.getQueueOrdering() == QueueOrdering.RR) {
            if (currentRRElem >= 0 && queue.get(currentRRElem).timeLeft == 0) {
                //current process statistics
                ProcessStatistics currProcessStatistics = new ProcessStatistics(currProcess);
                //set some process statistics
                currProcessStatistics.setFinishTime(currentTime);
                currProcessStatistics.setTat(currentTime - currProcess.getArrivalTime());
                currProcessStatistics.setNtat((float)currProcessStatistics.getTat() / (float)currProcess.getServiceTime());

                processStatistics.add(currProcessStatistics);

                queue.removeProcessAt(currentRRElem);
                currentRRElem--;
            }

            if (queue.count() == 0)
                return null;

            int initElem = currentRRElem + 1 & queue.count();
            currentRRElem++;
            currentRRElem %= inQueueProcesses();

            while (queue.get(currentRRElem).getArrivalTime() > currentTime) {
                currentRRElem++;
                currentRRElem %= inQueueProcesses();
                if (currentRRElem == initElem) {
                    //sleep for the designated milliseconds
                    Thread.sleep(millisecsPerTime);
                    //increment current time
                    currentTime++;
                    //try again
                    return null;
                }
            }

            //if the queue is empty, return null
            if (queue.count() == 0)
                return null;

            //set the new remaining time in the current quantum
            rrTimeRemaining = rrTimeQuantum;
            //return the selected element
            return queue.get(currentRRElem);
        } else {
            //this will never trigger
            //if for some godforesaken reason it does, just treat it like a FIFO queue
            Process p = queue.get(0);
            queue.removeProcessAt(0);
            return p;
        }
    }

    /**
     * Run the CPU on its process queue
     * NOTE: This is part of the Runnable interface and should not be called directly; you are probably looking for CPU::start()
     */
    public void run() {
        //set the isRunning flag to true
        isRunning = true;

        //get the time that the CPU starts executing processes
        startTime = new Date().getTime();

        //while the queue is not empty
        while (queue.hasProcesses()) {
            //Thread.sleep can throw an InterruptedException, we have to handle that
            try {
                //pop a process (synchronization is taken care of in the ProcessQue class, don't worry!)
                currProcess = getNextProcess();

                //even though we checked if the queue was not empty, there is a chance that another thread has already stolen the last item
                //in this case, we want to check if the popProcess method returned null, and if it did, do not attempt to execute any more code
                if (currProcess == null)
                    continue;

                //output the currently executing process to the console
                System.out.println("CPU" + CPUNum.toString() + " now executing \"" + currProcess.getProcessID() + "\" for " + millisecsPerTime * currProcess.timeLeft + " milliseconds.");

                //while there is time left in the process and time left in the round robin quantum
                while (currProcess.timeLeft > 0 && rrTimeRemaining > 0) {
                    //sleep for the designated milliseconds
                    Thread.sleep(millisecsPerTime);
                    //decrement the amount of time left in the process and the RR time quantum
                    currProcess.timeLeft--;
                    //if the queue is set to RR, decrement the time remaining in the quantum
                    if (queue.getQueueOrdering() == QueueOrdering.RR)
                        rrTimeRemaining--;
                    //increment the current time
                    currentTime++;
                }
            } catch (InterruptedException e) {
                //if we were interrupted, simply terminate the thread
                return;
            }
        }

        if (queue.getQueueOrdering() == QueueOrdering.HRRN) {
            //current process statistics
            ProcessStatistics currProcessStatistics = new ProcessStatistics(currProcess);
            //set some process statistics
            currProcessStatistics.setFinishTime(currentTime);
            currProcessStatistics.setTat(currentTime - currProcess.getArrivalTime());
            currProcessStatistics.setNtat((float) currProcessStatistics.getTat() / (float) currProcess.getServiceTime());

            processStatistics.add(currProcessStatistics);
        }

        //set the current process to null
        currProcess = null;
        //set isRunning to false
        this.isRunning = false;
        //output that the CPU has retired
        System.out.println("CPU" + CPUNum.toString() + " retired.");
        //set the thread to null so we can run again if needed
        t = null;
    }

    /**
     * Start the CPU
     */
    public void start() {
        //if we're not already running
        if (t == null) {
            //create the thread on the "this" object
            t = new Thread(this, "CPU" + CPUNum.toString());
            //start the thread
            t.start();
            isPaused = false;
        }
    }

    /**
     * Get the CPU ID
     *
     * @return CPU ID
     */
    public int getCPUNum() {
        return CPUNum;
    }

    /**
     * Get currently executing process
     *
     * @return Currently executing process
     */
    public String getExecutingProcess() {
        //account for the possibility that no process is executing
        if (currProcess == null)
            return "No Backend.Process Executing";
        else
            return currProcess.getProcessID();
    }

    /**
     * Check if CPU is running
     *
     * @return Whether or not the CPU is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gets the time remaining in the current process
     *
     * @return Time units remaining in currently executing process, null if no process is executing
     */
    public Integer timeRemaining() {
        if (currProcess == null)
            return null;
        else
            return timeLeft;
    }

    /**
     * Pauses the CPU
     */
    public void pauseSystem() {
        //this method is deprecated
        //nonetheless, it still exists in Java 15, and it does what I want it to do, so I will continue to use it
        t.suspend();
        isPaused = true;
    }

    /**
     * Unpauses the CPU
     */
    public void unpauseSystem() {
        //like Thread::suspend, this is deprecated
        //also like Thread::suspend, it does what I want, so I continue to use it
        t.resume();
        isPaused = false;
    }

    /**
     * Gets boolean of if process is paused
     *
     * @return true if paused, false otherwise
     */
    public boolean getIsPaused() {
        return isPaused;
    }

    /**
     * get the current time
     * @return the current time
     */
    public int getCurrentTime() {
        return currentTime;
    }
}
