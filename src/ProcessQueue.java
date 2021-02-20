import java.util.*;

public class ProcessQueue {
    private Queue<Process> processes;

    public ProcessQueue(QueueOrdering q) {
        if (q == QueueOrdering.Priority)
            this.processes = new PriorityQueue<>(8, Comparator.comparingInt(Process::getPriority));
        else if (q == QueueOrdering.FIFO)
            this.processes = new LinkedList<>();
    }

    public void pushProcess(Process p) {
        synchronized(this) {
            processes.add(p);
        }
    }

    public Process popProcess() {
        synchronized(this) {
            return processes.remove();
        }
    }

    public boolean hasProcesses() {
        synchronized(this) {
            return processes.size() > 0;
        }
    }
}
