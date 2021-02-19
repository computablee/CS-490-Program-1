import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class ProcessQueue {
    private Queue<Process> processes;

    public ProcessQueue() {
        this.processes = new PriorityQueue<>(8, Comparator.comparingInt(Process::getPriority));
    }

    public void pushProcess(Process p) {
        processes.add(p);
    }

    public Process popProcess() {
        return processes.remove();
    }
}
