import Backend.ProcessQueue;
import Backend.Processor;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //spawn GUI
        GUI gui = new GUI();
        //create a process queue
        ProcessQueue pq;

        //output some info to user
        System.out.println("Spawned GUI.");
        System.out.println("Input filename for processes:");

        //calling Reader::getData can throw a FileNotFoundException, so we have to handle that
        try {
            //this is really dumb code that gets a console input from user for a file, then extracts the data from that file into a ProcessQueue object
            pq = new Reader(new Scanner(System.in).next()).getData();
        } catch (FileNotFoundException e) {
            //print the exception and retire the application
            System.out.println("Unable to open file. Terminating program...");
            System.exit(0);
            return; //code will not compile without this redundant return
        }

        //Create the processor with 1 CPU, 100 milliseconds per unit of time, and with all CPUs sharing the same ProcessQueue, pq
        Processor processor = new Processor(1, 100, pq);
        //start the processor
        processor.startProcessor();

        try{Thread.sleep(100);}catch(Exception e){}

        while(processor.isRunning()) {
            System.out.println("Average process duration: " + processor.getAverageProcessDuration());
            System.out.println("Processes/second: " + processor.getProcessThroughputPerSecond());
            try{Thread.sleep(100);}catch(Exception e){}
        }
        System.out.println("Average process duration: " + processor.getAverageProcessDuration());
        System.out.println("Processes/second: " + processor.getProcessThroughputPerSecond());
    }
}
