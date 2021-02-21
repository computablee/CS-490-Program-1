public class CPU implements Runnable {
    private ProcessQueue queue;
    private Integer CPUnum;
    private Thread t;
    private Process currProcess;
    private int millisecsPerTime;
    private boolean isRunning;

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
        this.isRunning = true;
        while (queue.hasProcesses()) {
            currProcess = queue.popProcess();

            try {
                System.out.println("CPU" + CPUnum.toString() + " now executing \"" + currProcess.getProcessID() + "\" for " + millisecsPerTime * currProcess.getServiceTime() + " milliseconds.");

                Thread.sleep((long)millisecsPerTime * currProcess.getServiceTime());
            } catch (InterruptedException e) {
                return;
            }
        }
        currProcess = null;
        this.isRunning = false;
        System.out.println("CPU" + CPUnum.toString() + " retired.");
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

    public String getExecutingProcess() {
        if (currProcess == null)
            return "No Process Executing";
        else
            return currProcess.getProcessID();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
