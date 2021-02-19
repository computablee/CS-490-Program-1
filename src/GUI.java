import javax.swing.*;

public class GUI {
    private JFrame frame;

    public GUI() {
        javax.swing.SwingUtilities.invokeLater(this::spawnGUI);
    }

    private void spawnGUI() {
        this.frame = new JFrame("CS 490 Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
