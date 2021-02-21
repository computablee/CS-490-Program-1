import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GUI gui = new GUI();
        ProcessQueue pq;

        System.out.println("Spawned GUI.");
        System.out.println("Input filename for processes:");

        try {
            pq = new Reader(new Scanner(System.in).next()).getData();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file. Terminating program...");
            return;
        }

        Processor processor = new Processor(1, 100, pq);
        processor.startProcessor();
    }
}
