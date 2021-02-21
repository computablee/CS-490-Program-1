import java.util.ArrayList;
import java.util.List;

public class Processor {
    List<CPU> CPUs;

    public Processor(int CPUs, int millisecsPerTime) {
        this.CPUs = new ArrayList<>(CPUs);

        for (int i = 0; i < CPUs; i++) {
            this.CPUs.set(i, new CPU(i, millisecsPerTime));
        }
    }

    public Processor(int CPUs, int millisecsPerTime, ProcessQueue pq) {
        this(CPUs, millisecsPerTime);

        for (int i = 0; i < CPUs; i++) {
            this.CPUs.get(i).assignQueue(pq);
        }
    }

    public void assignQueue(ProcessQueue pq, int CPU) {
        this.CPUs.get(CPU).assignQueue(pq);
    }
}
