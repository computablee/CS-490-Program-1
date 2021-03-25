package Backend;

public class ProcessStatistics {
    private Process process;
    private int arrivalTime;
    private int serviceTime;
    private Integer finishTime;
    private Integer tat;
    private Float ntat;

    public ProcessStatistics(Process process) {
        this.process = process;
        this.arrivalTime = process.getArrivalTime();
        this.serviceTime = process.getServiceTime();
        this.finishTime = null;
        this.tat = null;
        this.ntat = null;
    }

    public void setFinishTime(Integer finishTime) {
        this.finishTime = finishTime;
    }

    public void setTat(Integer tat) {
        this.tat = tat;
    }

    public void setNtat(Float ntat) {
        this.ntat = ntat;
    }

    public Integer getFinishTime() {
        return finishTime;
    }

    public Integer getTat() {
        return tat;
    }

    public Float getNtat() {
        return ntat;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public Process getProcess() {
        return process;
    }
}
