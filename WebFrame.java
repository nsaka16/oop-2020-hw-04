import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//I needed help for this part of homework
//Couldn't complete it. :(
public class WebFrame {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JTextField numThreadField;
    private JLabel labelOfCurrentlyRunning;
    private JLabel labelOfAlreadyFinished;
    private JLabel labelOfElapsedTime;
    private JProgressBar progressBar;
    private DefaultTableModel defaultTableModel;
    private Thread launcherThread   ;
    private int activeThreads;
    private int finishedThreads;
    private Lock lock = new ReentrantLock();
    //Which one to use?
    private AtomicInteger atomicInteger = new AtomicInteger();

    public WebFrame(String fileName) throws FileNotFoundException {
        initialiseSwingComponents();
        readFile(fileName);
    }

    private void readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(new FileReader(file));
        while (scanner.hasNext()) {
            defaultTableModel.addRow(new String[]{scanner.next(), ""});
        }
        scanner.close();
    }

    private void initialiseSwingComponents() {
        initialiseJFrame();
        initialiseLabels();
        initialiseJTable();
        initialiseButtons();
        initialiseProgressBar();
    }

    private void initialiseProgressBar() {
        progressBar = new JProgressBar(0, defaultTableModel.getRowCount());
        panel.add(progressBar);
    }

    private void initialiseLabels() {
        labelOfCurrentlyRunning = new JLabel("Running:0");
        labelOfAlreadyFinished = new JLabel("Completed:0");
        labelOfElapsedTime = new JLabel("Elapsed:");
        panel.add(labelOfCurrentlyRunning);
        panel.add(labelOfAlreadyFinished);
        panel.add(labelOfElapsedTime);
    }

    private void initialiseButtons() {
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        initialiseSingleThreadButton();
        initialiseSeveralThreadButton();
        initialiseStopButton();
    }

    private void initialiseStopButton() {
        JButton stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(e -> {
            launcherThread.interrupt();
        });
    }

    private void initialiseSeveralThreadButton() {
        JButton concurrentFetch = new JButton("Concurrent Fetch");
        panel.add(concurrentFetch);
        concurrentFetch.addActionListener(e -> {
            startUpSeveralThreadedModel(Integer.parseInt(numThreadField.getText()));
        });
    }

    private void initialiseSingleThreadButton() {
        JButton singleThreadFetch = new JButton("Single Thread Fetch");
        panel.add(singleThreadFetch);
        singleThreadFetch.addActionListener(e -> {
            startUpSingleThreadedModel();
        });
    }

    private void startUpSingleThreadedModel() {
        startUpSeveralThreadedModel(1);
    }

    private void startUpSeveralThreadedModel(int i) {
        activeThreads = 0;
        finishedThreads = 0;
        launcherThread = new Launcher(i,defaultTableModel.getRowCount());
        launcherThread.start();
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
        defaultTableModel = new DefaultTableModel(new String[]{"url", "status"}, 0);
        table = new JTable(defaultTableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600, 300));
        panel.add(scrollpane);
    }

    public void increaseActiveThreadCount() {
        lock.lock();
        incrementAndUpdateActiveThread(1);
        lock.unlock();
    }

    public void decreaseActiveThreadCount() {
        lock.lock();
        incrementAndUpdateActiveThread(-1);
        lock.unlock();
    }

    private void incrementAndUpdateActiveThread(int delta) {
        activeThreads += delta;
        SwingUtilities.invokeLater(() -> labelOfCurrentlyRunning.setText("Running:" + activeThreads + ""));
    }

    public void activeThreadFinished(int row, String status) {
        lock.lock();
        incrementAndUpdateFinishedThreads(row, status);
        lock.unlock();
    }

    private void incrementAndUpdateFinishedThreads(int row, String status) {
        finishedThreads += 1;
        labelOfAlreadyFinished.setText("Completed: " + finishedThreads + "");
        progressBar.setValue(finishedThreads);
        defaultTableModel.setValueAt(status, row, 1);
    }

    public class Launcher extends Thread {
        private Semaphore semaphore;
        private int rows;

        public Launcher(int numWorkers,int defaultModelRows) {
            semaphore = new Semaphore(numWorkers);
            this.rows = defaultModelRows;
        }

        @Override
        public void run() {
            try {
                tryToRun();
            } catch (InterruptedException e) { }
        }

        private void tryToRun() throws InterruptedException {
            runWorkerThreads();
        }

        private void runWorkerThreads() throws InterruptedException {
            increaseActiveThreadCount();
            int row = 0;
            while (row < rows) {
                useWorkerThreads(row++);
            }
            decreaseActiveThreadCount();
        }

        private void useWorkerThreads(int row) throws InterruptedException {
            WebWorker worker = new WebWorker(defaultTableModel.getValueAt(row, 0).toString(),
                    row,
                    WebFrame.this,
                    ()->tryToAcquire(),
                    ()->semaphore.release());
            worker.start();
        }

        private void tryToAcquire() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            tryToCreateWebFrame();
        });
    }

    private static void tryToCreateWebFrame() {
        try {
            new WebFrame("links.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
