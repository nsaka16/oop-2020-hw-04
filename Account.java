// Account.java

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 Simple, thread-safe Account class encapsulates
 a balance and a transaction count.
*/
public class Account {
	private int id;
	private int balance;
	private int transactions;
	private Lock lock = new ReentrantLock();
	private Condition conditionOfInsufficientBalance = lock.newCondition();

	// It may work out to be handy for the account to
	// have a pointer to its Bank.
	// (a suggestion, not a requirement)
	private Bank bank;  

	public Account(Bank bank, int id, int balance) {
		this.bank = bank;
		this.id = id;
		this.balance = balance;
		transactions = 0;
	}

	///All methods below need to be synchronized.

	public int getId() {
		return id;
	}

	public int getBalance() {
		return balance;
	}

	public int getTransactions() {
		return transactions;
	}

	public void withDraw(int amount) {
		lock.lock();
		System.out.println(balance);
		System.out.println(amount);
//		while(balance<amount){
//			System.out.println("Account "+id);
//			System.out.println("waiting.......");
//			conditionOfInsufficientBalance.await();
//		}
		//System.out.println("Account: "+id+" withdrawed: "+amount);
		balance-=amount;
		transactions++;
		lock.unlock();
	}

	public void deposit(int amount){
		lock.lock();
		balance+=amount;
//		System.out.println("Account "+id);
//		System.out.println("Account "+id+" depositing: "+amount);
//		System.out.println("Signal all");
//		conditionOfInsufficientBalance.signalAll();
		transactions++;
		lock.unlock();
	}

	@Override
	public String toString(){
		//This lock was needed while printing account during processing.
		lock.lock();
		String res="acct:"+id+" bal:"+balance+" trans:"+transactions;
		lock.unlock();
		return res;
	}
}
