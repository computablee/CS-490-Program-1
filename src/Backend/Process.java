package Backend;

/**
 * Class that represents a process that can be run by a CPU
 *
 * @author Lane Allen
 */
public class Process {
    //arrival time
    private int arrivalTime;
    //process ID
    private String processID;
    //service time
    private int serviceTime;
    //priority
    private int priority;
    //time left
    public int timeLeft;

    /**
     * Constructor
     *
     * @param arrivalTime Arrival time
     * @param processID Process ID
     * @param serviceTime Service time
     * @param priority Priority
     */
    public Process(int arrivalTime, String processID, int serviceTime, int priority) {
        //generic constructor stuff
        this.arrivalTime = arrivalTime;
        this.processID = processID;
        this.serviceTime = serviceTime;
        this.priority = priority;
        this.timeLeft = serviceTime;
    }

    /**
     * Getter for arrival time
     * @return Arrival time
     */
    public int getArrivalTime() { return this.arrivalTime; }

    /**
     * Getter for service time
     * @return Service time
     */
    public int getServiceTime() { return this.serviceTime; }

    /**
     * Getter for process ID
     * @return Process ID
     */
    public String getProcessID() { return this.processID; }

    /**
     * Getter for priority
     * @return Priority
     */
    public int getPriority() { return this.priority; }

    /**
     * Deep copies a process
     *
     * @return A deep copy of the current process
     */
    public Process deepCopy() {
        Process copiedProcess = new Process(this.arrivalTime, this.processID, this.serviceTime, this.priority);

        return copiedProcess;
    }
}
