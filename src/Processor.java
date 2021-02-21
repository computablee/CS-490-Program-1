import java.util.ArrayList;
import java.util.List;

public class Processor {
    List<CPU> CPUs;

    public Processor(int CPUs, int millisecsPerTime) {
        this.CPUs = new ArrayList<>();

        for (int i = 0; i < CPUs; i++)
            this.CPUs.add(new CPU(i, millisecsPerTime));
    }

    public Processor(int CPUs, int millisecsPerTime, ProcessQueue processQueue) {
        this(CPUs, millisecsPerTime);

        for (CPU cpu : this.CPUs)
            cpu.assignQueue(processQueue);
    }

    public void assignQueue(ProcessQueue pq, int CPU) {
        this.CPUs.get(CPU).assignQueue(pq);
    }

    public void startProcessor() {
        for (CPU cpu : CPUs)
            cpu.start();
    }

    public String getExecutingProcess(int CPU) {
        return CPUs.get(CPU).getExecutingProcess();
    }

    public boolean isRunning() {
        boolean isRunning = false;
        for (CPU cpu : CPUs)
            isRunning |= cpu.isRunning();

        return isRunning;
    }
}
