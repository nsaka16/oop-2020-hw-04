// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {
	public static final int ACCOUNTS = 20;	 // number of accounts
	public static final int INITIAL_ACCOUNT_BALANCE = 1000;
	private  int NUM_WORKERS = 0;
	private final Transaction nullTransaction = new Transaction(-1,0,0);
	private CountDownLatch countDownLatch;
	private List<Account> accounts = new ArrayList<>(ACCOUNTS);
	private BlockingQueue<Transaction> transactions;
	public boolean TESTS_CHECK = false;
	/*
	 Reads transaction data (from/to/amt) from a file for processing.
	 (provided code)
	 */
	public void readFile(String file) {
			try {
				tryToReadFileAndFillTransactions(file);
			}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void tryToReadFileAndFillTransactions(String file) throws IOException, InterruptedException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		// Use stream tokenizer to get successive words from file
		StreamTokenizer tokenizer = new StreamTokenizer(reader);

		while (true) {
			int read = tokenizer.nextToken();
			if (read == StreamTokenizer.TT_EOF) break;  // detect EOF
			int from = (int)tokenizer.nval;

			tokenizer.nextToken();
			int to = (int)tokenizer.nval;

			tokenizer.nextToken();
			int amount = (int)tokenizer.nval;

			Transaction newTransaction = new Transaction(from,to,amount);
			transactions.put(newTransaction);
		}
		addNullTransactionsForEachWorkerThread();
	}

	public List<Account> getBankAccounts(){ return accounts; }

	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public void processFile(String file, int numWorkers) {
		NUM_WORKERS = numWorkers;
		initializeBankAccounts();
		countDownLatch = new CountDownLatch(numWorkers);
		transactions = new ArrayBlockingQueue<>( numWorkers);
		startUpWorkerThreads(numWorkers);
		readFile(file);
		generateAccountDetails();
	}

	private void generateAccountDetails(){
		try {
			tryToGenerateAccountResults();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void tryToGenerateAccountResults() throws InterruptedException {
		countDownLatch.await();
		if(!TESTS_CHECK	 )accounts.forEach(e->System.out.println(e.toString()));
	}

	private void initializeBankAccounts(){
		for(int i=0; i<ACCOUNTS; i++){
			accounts.add(new Account(this,i,INITIAL_ACCOUNT_BALANCE));
		}
	}

	private void startUpWorkerThreads(int numberOfWorkerThreads){
		for(int workerThreadIndex=0; workerThreadIndex<numberOfWorkerThreads; workerThreadIndex++){
			new WorkerThread().start();
		}
	}

	private void addNullTransactionsForEachWorkerThread() throws InterruptedException {
		for(int workerThreadIndex=0; workerThreadIndex<NUM_WORKERS; workerThreadIndex++)
			transactions.put(nullTransaction);
	}
	/*
	 Looks at commandline args and calls Bank processing.
	*/
	public static void main(String[] args) {
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			System.exit(1);
		}

		String file = args[0];

		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
		}
		//We have file and number of workers here.
		Bank bank = new Bank();
		bank.processFile(file,numWorkers);
	}

	private class WorkerThread extends Thread{
		@Override
		public void run(){
			try {
				tryToProcessTransaction();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void tryToProcessTransaction() throws InterruptedException {
		Transaction transaction = transactions.take();
		while(! (transaction == nullTransaction)){
			//Should this whole process be atomic?
			Account from = accounts.get(transaction.getFrom());
			Account to = accounts.get(transaction.getTo());
			from.withDraw(transaction.getAmount());
			to.deposit(transaction.getAmount());
			transaction=transactions.take();
		}
		countDownLatch.countDown();
	}
}

