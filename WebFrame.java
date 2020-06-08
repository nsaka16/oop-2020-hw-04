import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.concurrent.Semaphore;

public class WebFrame {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JTextField numThreadField;
    private JLabel runningLabel;
    private JLabel completeLabel;
    private JLabel elapsedLabel;
    private JProgressBar progressBar;
    private DefaultTableModel defaultTableModel;
    private Launcher launcher;
    private int activeThreads;
    private int finishedThreads;
    private File file;
    private Object object = new Object();   //Locking on inner created object, instead of root class instance.

    public WebFrame(String fileName){
        file = new File(fileName);
        initialiseSwingComponents();
    }

    private void initialiseSwingComponents() {
        initialiseJFrame();
        initialiseLabels();
        initialiseJTable();
        initialiseButtons();
        initialiseProgressBar();
    }

    private void initialiseProgressBar() {
        progressBar = new JProgressBar(0,defaultTableModel.getRowCount());
        panel.add(progressBar);
    }

    private void initialiseLabels() {
        runningLabel = new JLabel("Running:0");
        completeLabel = new JLabel("Completed:0");
        elapsedLabel = new JLabel("Elapsed:");
        panel.add(runningLabel);
        panel.add(completeLabel);
        panel.add(elapsedLabel);
    }

    private void initialiseButtons() {
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        initialiseSingleThreadButton();
        initialiseSeveralThreadButton();
        initialiseStopButton();
    }

    private void initialiseStopButton() {
        JButton stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(e->{
            launcher.interrupt();
        });
    }

    private void initialiseSeveralThreadButton() {
        JButton concurrentFetch = new JButton("Concurrent Fetch");
        panel.add(concurrentFetch);
        concurrentFetch.addActionListener(e->{
            startUpSeveralThreadedModel(Integer.parseInt(numThreadField.getText()));
        });
    }

    private void initialiseSingleThreadButton() {
        JButton singleThreadFetch = new JButton("Single Thread Fetch");
        panel.add(singleThreadFetch);
        singleThreadFetch.addActionListener(e->{
            startUpSingleThreadedModel();
        });
    }

    private void startUpSingleThreadedModel() {
        startUpSeveralThreadedModel(1);
    }

    private void startUpSeveralThreadedModel(int i) {
        activeThreads = 0;
        finishedThreads = 0;
        launcher = new Launcher(i);
        launcher.start();
    }

    private void initialiseJFrame() {
        frame = new JFrame();
        panel = new JPanel();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initialiseJTable() {
        defaultTableModel = new DefaultTableModel(new String[]{"url","status" },0);
        table = new JTable(defaultTableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600,300));
        panel.add(scrollpane);
    }

    public void increaseActiveThreadCount(){
        synchronized (object){
            activeThreads+=1;
            SwingUtilities.invokeLater(()->runningLabel.setText("Running:"+ activeThreads+""));
        }
    }

    public void decreaseActiveThreadCount(){
        synchronized (object){
            activeThreads-=1;
            SwingUtilities.invokeLater(()->runningLabel.setText("Running:"+ activeThreads+""));
        }
    }

    public class Launcher extends Thread {
        private Semaphore semaphore;

        public Launcher(int numWorkers){
            semaphore = new Semaphore(numWorkers);
        }

        @Override
        public void run(){
            increaseActiveThreadCount();



            decreaseActiveThreadCount();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new WebFrame("links.txt");
        });
    }

}
