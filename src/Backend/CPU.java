package Backend;

/**
 * Class to represent an individual CPU
 *
 * @author Lane Allen
 */
public class CPU implements Runnable {
    //queue associated with this CPU
    private ProcessQueue queue;
    //CPU ID
    private Integer CPUnum;
    //thread for running the CPU
    private Thread t;
    //current executing process
    private Process currProcess;
    //milliseconds per unit of time
    private int millisecsPerTime;
    //whether or not this specific CPU is running any processes
    private boolean isRunning;

    /**
     * Constructor
     *
     * @param num CPU ID on processor
     * @param millisecsPerTime Milliseconds per unit of time
     */
    public CPU(int num, int millisecsPerTime) {
        //this is standard constructor stuff, I don't feel like I need to explain
        this.queue = null;
        this.CPUnum = num;
        this.currProcess = null;
        this.t = null;
        this.millisecsPerTime = millisecsPerTime;
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
        this.isRunning = true;

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
                System.out.println("CPU" + CPUnum.toString() + " now executing \"" + currProcess.getProcessID() + "\" for " + millisecsPerTime * currProcess.getServiceTime() + " milliseconds.");

                //sleep for the designated milliseconds
                Thread.sleep(millisecsPerTime * currProcess.getServiceTime());
            } catch (InterruptedException e) {
                //if we were interrupted, simply terminate the thread
                return;
            }
        }

        //set the current process to null
        currProcess = null;
        //set isRunning to false
        this.isRunning = false;
        //output that the CPU has retired
        System.out.println("CPU" + CPUnum.toString() + " retired.");
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
            t = new Thread(this, "CPU" + CPUnum.toString());
            //start the thread
            t.start();
        }
    }

    /**
     * Get the CPU ID
     *
     * @return CPU ID
     */
    public int getCPUnum() {
        return CPUnum;
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
}
