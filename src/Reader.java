import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
    String fileName;

    public Reader(String fileName) {
        this.fileName = fileName;
    }

    public ProcessQueue getData() throws FileNotFoundException {
        ProcessQueue processQueue = new ProcessQueue();
        Scanner s = new Scanner(new File(this.fileName));
        s.useDelimiter(",");

        while(s.hasNext()) {
            int at = Integer.parseInt(s.next());
            String pid = s.next().trim();
            int st = Integer.parseInt(s.next());
            int prty = Integer.parseInt(s.next());

            Process p = new Process(at, pid, st, prty);
            processQueue.pushProcess(p);
        }

        s.close();

        return processQueue;
    }
}
