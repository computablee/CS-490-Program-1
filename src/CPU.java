public class CPU implements Runnable {
    private ProcessQueue queue;
    private Integer CPUnum;
    private Thread t;
    private Process currProcess;
    private int millisecsPerTime;

    public CPU(int num, int millisecsPerTime) {
        this.queue = null;
        this.CPUnum = num;
        this.currProcess = null;
        this.t = null;
        this.millisecsPerTime = millisecsPerTime;
    }

    public void assignQueue(ProcessQueue pq) {
        this.queue = pq;
    }

    public void run() {
        while (queue.hasProcesses()) {
            currProcess = queue.popProcess();

            try {
                Thread.sleep((long)millisecsPerTime * currProcess.getServiceTime());
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "CPU" + CPUnum.toString());
            t.start();
        }
    }

    public int getCPUnum() {
        return CPUnum;
    }
}
