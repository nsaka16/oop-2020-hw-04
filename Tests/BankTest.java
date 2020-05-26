import org.junit.jupiter.api.Test;

import java.util.AbstractCollection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankTest {

    @Test
    public void testForSmallTxt(){
        Bank bank = new Bank();
        bank.processFile("small.txt",4);
        for(Account account : bank.getBankAccounts()){
            if(account.getId()%2==0){
                assertEquals(999,account.getBalance());
            }else{
                assertEquals(1001,account.getBalance());
            }
            assertEquals(1,account.getTransactions());
        }
    }

    @Test
    public void test5KTxt(){
        Bank bank = new Bank();
        bank.processFile("5k.txt",4);
        for(Account account : bank.getBankAccounts()) {
            assertEquals(1000,account.getBalance());
        }
    }

    @Test
    public void test100KTxt(){
        Bank bank = new Bank();
        bank.processFile("100k.txt",4);
        for(Account account : bank.getBankAccounts()) {
            assertEquals(1000,account.getBalance());
        }
    }

    //What should happen here?
    @Test
    public void testTransactionOverLimitedAmount(){
        Bank bank = new Bank();
        bank.processFile("CustomBankTransactions1",4);
    }

    //What should happen here?
    @Test
    public void testTransactionOverLimitedAmount2(){
        Bank bank = new Bank();
        bank.processFile("CustomBankTransactions2",4);
        List<Account> accountList = bank.getBankAccounts();
        for(int i=3; i<accountList.size(); i++)assertEquals(0,accountList.get(i).getTransactions());
        assertEquals(2,accountList.get(0).getTransactions());
        assertEquals(1,accountList.get(1).getTransactions());
        assertEquals(1,accountList.get(2).getTransactions());
        assertEquals(500,accountList.get(2).getBalance());
        assertEquals( 2200,accountList.get(1).getBalance());
        assertEquals( 300,accountList.get(0).getBalance());
    }

}