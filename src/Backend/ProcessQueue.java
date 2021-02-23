package Backend;

import java.util.*;

/**
 * Class that represents a queue of processes
 *
 * @author Lane Allen
 */
public class ProcessQueue {
    //queue object that represents the processes
    private Queue<Process> processes;
    private QueueOrdering queueOrdering;

    /**
     * Constructor where you can specify the type of queue
     *
     * @param q The queue ordering (Priority or FIFO)
     */
    public ProcessQueue(QueueOrdering q) {
        //if priority queue
        if (q == QueueOrdering.Priority)
            //create a PriorityQueue object that orders according to the getPriority method of a Process object
            this.processes = new PriorityQueue<>(8, Comparator.comparingInt(Process::getPriority));
        //if FIFO queue
        else if (q == QueueOrdering.FIFO)
            //create a LinkedList object
            this.processes = new LinkedList<>();
        //assign for later use
        this.queueOrdering = q;
    }

    /**
     * Push a process to the queue
     *
     * @param p The process to push to the queue
     */
    public void pushProcess(Process p) {
        //create a critical region essentially based on "this," such that only one thread may access this object at a time
        synchronized(this) {
            processes.add(p);
        }
    }

    /**
     * Pop a process from the queue
     *
     * @return The process popped from the queue
     */
    public Process popProcess() {
        //like the pushProcess method, we synchronize on "this" so that only one thread may access this object (pushing or popping) at a time
        synchronized(this) {
            //make sure we check if the queue has an object before we remove
            if (processes.size() != 0)
                return processes.remove();
            else
                return null;
        }
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
                //if FIFO
                if (queueOrdering == QueueOrdering.FIFO)
                    //copy the location `i` from the linked list into the array list
                    retVal.add(((LinkedList<Process>)processes).get(i));
                //if priority
                else if (queueOrdering == QueueOrdering.Priority)
                    ;//TODO: Implement a way to add processes to retVal from a Priority Queue
            }
        }

        return retVal;
    }
}
