package Backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the processor of a system
 *
 * @author Lane Allen
 */
public class Processor {
    //List of all CPUs
    List<CPU> CPUs;

    /**
     * Constructor that does not assign a ProcessQueue to CPUs
     *
     * @param CPUs The number of CPUs on the processor
     * @param millisecsPerTime The number of milliseconds per unit of time
     */
    public Processor(int CPUs, int millisecsPerTime) {
        //create an ArrayList defining CPUs
        this.CPUs = new ArrayList<>();

        //instantiate each CPU
        for (int i = 0; i < CPUs; i++)
            this.CPUs.add(new CPU(i, millisecsPerTime));
    }

    /**
     * Constructor that assigns a single Backend.ProcessQueue to all CPUs (shared queue)
     *
     * @param CPUs The number of CPUs on the processor
     * @param millisecsPerTime The number of milliseconds per unit of time
     * @param processQueue The process queue to assign to each CPU
     */
    public Processor(int CPUs, int millisecsPerTime, ProcessQueue processQueue) {
        //call other constructor first
        this(CPUs, millisecsPerTime);

        //then assign the process queue to each CPU
        for (CPU cpu : this.CPUs)
            cpu.assignQueue(processQueue);
    }

    /**
     * Manually assign a process queue to an individual CPU
     *
     * @param pq The process queue to assign
     * @param CPU The ID of the CPU to assign to
     */
    public void assignQueue(ProcessQueue pq, int CPU) {
        this.CPUs.get(CPU).assignQueue(pq);
    }

    /**
     * Start processor
     */
    public void startProcessor() {
        //iterate through all CPUs and tell them to start
        for (CPU cpu : CPUs)
            cpu.start();
    }

    /**
     * Get executing process on a specific CPU
     *
     * @param CPU The CPU ID on which to check the executing process
     * @return The process executing on the specified CPU
     */
    public String getExecutingProcess(int CPU) {
        return CPUs.get(CPU).getExecutingProcess();
    }

    /**
     * Checks if processor is still running
     *
     * @return Whether or not the processor is still running
     */
    public boolean isRunning() {
        //set a flag to false
        boolean isRunning = false;

        //iterate through all CPUs
        for (CPU cpu : CPUs)
            //set the flag to true if a CPU is running, else leave unchanged
            isRunning |= cpu.isRunning();

        //return the flag
        return isRunning;
    }

    /**
     * Pauses the system
     */
    public void pauseSystem() {
        //individually pause each CPU
        for (CPU cpu : CPUs)
            cpu.pauseSystem();
    }

    /**
     * Unpauses the system
     */
    public void unpauseSystem() {
        //individually unpause each CPU
        for (CPU cpu : CPUs)
            cpu.unpauseSystem();
    }

    /**
     * Gets average process duration in time units
     *
     * @return Average process duration in time units
     */
    public double getAverageProcessDuration() {
        double total = 0;
        for (CPU cpu : CPUs)
            total += cpu.getAverageProcessDuration();
        total /= CPUs.size();
        return total;
    }

    /**
     * Gets process throughput in terms of processes/second
     *
     * @return Average process throughput
     */
    public double getProcessThroughputPerSecond() {
        double total = 0;
        for (CPU cpu : CPUs)
            total += cpu.getProcessThroughputPerSecond();
        return total;
    }

    /**
     * Gets boolean of if process is paused
     *
     * @return true if paused, false otherwise
     */
    public boolean getIsPaused(int n) {
        return CPUs.get(n).getIsPaused();
    }

    /**
     * Gets time remaining to complete the current process
     *
     * @return Time remaining on current process
     */
    public Integer timeRemaining(int n) {
        return CPUs.get(n).timeRemaining();
    }
}
