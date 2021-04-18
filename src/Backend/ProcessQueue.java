package Backend;

import java.util.*;

/**
 * Class that represents a queue of processes
 *
 * @author Lane Allen
 */
public class ProcessQueue {
    //queue object that represents the processes
    private ArrayList<Process> processes;
    private QueueOrdering queueOrdering;

    /**
     * Constructor where you can specify the type of queue
     *
     * @param q The queue ordering (Priority or FIFO)
     */
    public ProcessQueue(QueueOrdering q) {
        //assign process queue object
        this.processes = new ArrayList<>();

        //assign for later use
        this.queueOrdering = q;
    }

    /**
     * Check if process still has elements
     *
     * @return If the queue still has processes
     */
    public boolean hasProcesses() {
        //same thing as earlier, critical region such that only one thread may access this object at a time (pushing, popping, or checking process availability)
        synchronized(this) {
            return processes.size() > 0;
        }
    }

    public void addProcess(Process process) {
        this.processes.add(process);
    }

    public void removeProcessAt(int i) {
        this.processes.remove(i);
    }

    /**
     * Gets the current queue as an ArrayList, used for GUI stuff
     * This might be thread-safe; I haven't thoroughly tested, and honestly I'm kinda uncertain
     * Gets a true copy of the ProcessQueue (though we do not make copies of individual Process objects)
     *
     * @return A copy of the ProcessQueue as an ArrayList
     */
    public ArrayList<Process> getQueue() {
        //create the arraylist
        ArrayList<Process> retVal = new ArrayList<>();

        //synchronize the entire duration that we are using the process object
        synchronized (this) {
            //iterate through the processes.size
            for (int i = 0; i < processes.size(); i++) {
                //copy the location `i` from the linked list into the array list
                retVal.add(processes.get(i));
            }
        }

        return retVal;
    }

    public ProcessQueue deepCopy() {
        ProcessQueue copiedQueue = new ProcessQueue(this.queueOrdering);

        ArrayList<Process> processes = getQueue();

        for (Process p : processes) {
            copiedQueue.addProcess(p.deepCopy());
        }

        return copiedQueue;
    }
}
