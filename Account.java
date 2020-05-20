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
	private Condition balanceCondition = lock.newCondition();

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

	public void withDraw(int amount){  }

	public void deposit(int amount){ }

	@Override
	public String toString(){
		return "acct:"+id+" bal:"+balance+" trans:"+transactions;
	}
}
