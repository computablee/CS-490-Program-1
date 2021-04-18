import Backend.Process;
import Backend.ProcessQueue;
import Backend.QueueOrdering;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that reads a file into a ProcessQueue object
 *
 * @author Lane Allen
 */
public class Reader {
    //file name inputted by user
    String fileName;

    /**
     * Constructor
     *
     * @param fileName File name to read from
     */
    public Reader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the data from the file
     *
     * @return A completed ProcessQueue object that represents the input file
     * @throws FileNotFoundException Throws if file is not found
     */
    public ArrayList<ProcessQueue> getData() throws FileNotFoundException {
        //Create a ProcessQueue object with FIFO ordering (instead of priority ordering)
        ArrayList<ProcessQueue> processQueue = new ArrayList<>();
        processQueue.add(new ProcessQueue(QueueOrdering.HRRN));
        processQueue.add(new ProcessQueue(QueueOrdering.RR));

        //Open the file
        Scanner s = new Scanner(new File(this.fileName));
        //Set the delimiters for a .csv
        s.useDelimiter(",|\\r\\n|\\n|\\r");

        //while there is data in the file
        while(s.hasNext()) {
            //input all of the data items for that line
            int at = Integer.parseInt(s.next().trim());
            String pid = s.next().trim();
            int st = Integer.parseInt(s.next().trim());
            int prty = Integer.parseInt(s.next().trim());

            //create a process with those data items
            Process p = new Process(at, pid, st, prty);
            //push the process to the process queue
            processQueue.get(0).addProcess(p);
            processQueue.get(1).addProcess(p.deepCopy());
        }

        //close the file
        s.close();

        //return the final process queue
        return processQueue;
    }
}
