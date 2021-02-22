package Backend;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Class that represents a queue of processes
 *
 * @author Lane Allen
 */
public class ProcessQueue {
    //queue object that represents the processes
    private Queue<Process> processes;

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
}
