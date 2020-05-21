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

	public void withDraw(int amount) throws InterruptedException {
		lock.lock();
		while(balance<amount){
			conditionOfInsufficientBalance.await();
		}
		balance-=amount;
		lock.unlock();
	}

	public void deposit(int amount){
		lock.lock();
		balance+=amount;
		conditionOfInsufficientBalance.signalAll();
		lock.unlock();
	}

	@Override
	public String toString(){
		lock.lock();
		String res="acct:"+id+" bal:"+balance+" trans:"+transactions;
		lock.unlock();
		return res;
	}
}
