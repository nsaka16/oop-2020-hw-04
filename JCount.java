// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class JCount extends JPanel {

	private int counter;
	private JButton start;
	private JButton stop;
	private JTextField field;
	private JLabel label;
	private WorkerThread workerThread;

	public JCount() {
		// Set the JCount to use Box layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		start = new JButton("Start");
		stop = new JButton("Stop");
		field = new JTextField("100000000",10);
		label = new JLabel("0");
		add(Box.createRigidArea(new Dimension(0,40)));
		add(start);
		add(stop);
		add(field);
		add(label);
		setListenerToButton(start,this::startCounting);
		setListenerToButton(stop,this::stopCounting);
	}

	private void setListenerToButton(JButton button, Runnable behaviour){
		button.addActionListener(e->{
			behaviour.run();
		});
	}

	private void startCounting(){
		if(workerThread!=null)
			workerThread.interrupt();
		workerThread = new WorkerThread(Integer.parseInt(field.getText()));
		workerThread.start();
	}

	private void stopCounting(){
		if(workerThread!=null){
			workerThread.interrupt();
			workerThread=null;
		}
	}

	private static void createAndShowGUI() {
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}


	static public void main(String[] args)  {
		SwingUtilities.invokeLater(JCount::createAndShowGUI);
	}

	private  class WorkerThread extends Thread{
		private int countUpTo;

		public WorkerThread(int countUpTo){
			countUpTo = countUpTo;
		}

		@Override
		public  void run() {
			int counter = 0;
			while(counter<=counter && !isInterrupted()){
				if(counter%10000==0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
					int finalCounter = counter;
					SwingUtilities.invokeLater(()->label.setText(finalCounter +""));
				}
				counter++;
			}
		}
	}
}

