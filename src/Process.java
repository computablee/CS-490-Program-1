public class Process {
    private int arrivalTime;
    private String processID;
    private int serviceTime;
    private int priority;

    public Process(int arrivalTime, String processID, int serviceTime, int priority) {
        this.arrivalTime = arrivalTime;
        this.processID = processID;
        this.serviceTime = serviceTime;
        this.priority = priority;
    }

    public int getArrivalTime() { return this.arrivalTime; }
    public int getServiceTime() { return this.serviceTime; }
    public String getProcessID() { return this.processID; }
    public int getPriority() { return this.priority; }
}
