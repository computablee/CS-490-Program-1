package Backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    //list of times that processes have completed
    private final List<Long> completedTimes;
    //lock for reading/writing to the completedTimes object
    private Lock completedTimesLock;
    //
    private boolean isPaused;

    /**
     * Constructor
     *
     * @param num CPU ID on processor
     * @param millisecsPerTime Milliseconds per unit of time
     */
    public CPU(int num, int millisecsPerTime) {
        //this is standard constructor stuff, I don't feel like I need to explain
        this.queue = null;
        this.CPUNum = num;
        this.currProcess = null;
        this.t = null;
        this.millisecsPerTime = millisecsPerTime;
        this.timeLeft = null;
        this.startTime = 0;
        this.completedTimes = new ArrayList<>();
        this.completedTimesLock = new ReentrantLock();
        this.isPaused = true;
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
            //pop a process (synchronization is taken care of in the ProcessQue class, don't worry!)
            currProcess = queue.popProcess();

            //even though we checked if the queue was not empty, there is a chance that another thread has already stolen the last item
            //in this case, we want to check if the popProcess method returned null, and if it did, do not attempt to execute any more code
            if (currProcess == null)
                break;

            //Thread.sleep can throw an InterruptedException, we have to handle that
            try {
                //output the currently executing process to the console
                System.out.println("CPU" + CPUNum.toString() + " now executing \"" + currProcess.getProcessID() + "\" for " + millisecsPerTime * currProcess.getServiceTime() + " milliseconds.");

                //get the time left as a variable
                timeLeft = currProcess.getServiceTime();

                //while there is time left in the process
                while (timeLeft > 0) {
                    //sleep for the designated milliseconds
                    Thread.sleep(millisecsPerTime);
                    //decrement the amount of time left
                    timeLeft--;
                }
            } catch (InterruptedException e) {
                //if we were interrupted, simply terminate the thread
                return;
            }

            //add the current time to the completed times queue, thread-safely
            completedTimesLock.lock();
            completedTimes.add(new Date().getTime() - startTime);
            completedTimesLock.unlock();
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
     * Gets average process duration in time units
     *
     * @return Average process duration in time units
     */
    public double getAverageProcessDuration() {
        long total = 0;
        double average = 0;
        //lock around all of our completedTimes reads, we don't want it changed while we're relying on it
        completedTimesLock.lock();
        //as long as there's at least one process to get data from
        if (completedTimes.size() > 0) {
            //this is awful, but since completedTimes measures milliseconds-since-start for each process,
            //this is how we measure average duration of a process
            total = completedTimes.get(0);
            for (int i = 1; i < completedTimes.size(); i++)
                total += completedTimes.get(i) - completedTimes.get(i - 1);
            //this is just dividing by number of processes, which a unit conversion from milliseconds to time units
            average = (double) total / (double) millisecsPerTime / (double) completedTimes.size();
        }
        //we're done with completedTimes, unlock the Lock
        completedTimesLock.unlock();
        return average;
    }

    /**
     * Gets process throughput in terms of processes/second
     *
     * @return Average process throughput
     */
    public double getProcessThroughputPerSecond() {
        //lock around all of our completedTimes reads, we don't want it changed while we're relying on it
        completedTimesLock.lock();
        //one-liner to get processes per second
        double throughput = (double)completedTimes.size() / ((double)(new Date().getTime() - startTime) / 1000f);
        //we're done with completedTimes, unlock the Lock
        completedTimesLock.unlock();
        return throughput;
    }
    public boolean getIsPaused() {
        return isPaused;
    }
}
