import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    @Test
    public void testWithdraw(){
        Bank bank = new Bank();
        Account account = new Account(bank,0,100);
        account.withDraw(10);
        assertEquals(90,account.getBalance());
    }

    @Test
    public void testDeposit(){
        Bank bank = new Bank();
        Account account = new Account(bank,0,100);
        account.deposit(10);
        assertEquals(110,account.getBalance());
    }

    @Test
    public void testWithdrawTooMuch(){
        Bank bank = new Bank();
        Account account = new Account(bank,0,100);
        account.withDraw(101);
        assertEquals(-1,account.getBalance());
    }

    @Test
    public void accountsToStringEqual(){
        Bank bank = new Bank();
        Account account1 = new Account(bank,0,100);
        Account account2 = new Account(bank,0,100);
        assertEquals(account1.toString(),account2.toString());
    }
}
