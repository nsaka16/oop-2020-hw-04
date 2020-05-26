
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TransactionTest {
    @Test
    public void TransactionsEquals(){
        Transaction transaction1 = new Transaction(10,10,10);
        Transaction transaction2 = new Transaction(10,10,10);
        assertEquals(transaction1,transaction2);
    }

    @Test
    public void TransactionsToStringEquals(){
        Transaction transaction1 = new Transaction(10,10,10);
        Transaction transaction2 = new Transaction(10,10,10);
        assertEquals(transaction1.toString(),transaction2.toString());
    }

    @Test
    public void TransactionsToStringDifferByAmount(){
        Transaction transaction1 = new Transaction(10,10,10);
        Transaction transaction2 = new Transaction(10,10,11);
        assertNotEquals(transaction1.toString(),transaction2.toString());
    }

    @Test
    public void TransactionsToStringDifferByTo(){
        Transaction transaction1 = new Transaction(10,12,10);
        Transaction transaction2 = new Transaction(10,10,10);
        assertNotEquals(transaction1.toString(),transaction2.toString());
    }


    @Test
    public void TransactionsToStringDifferByFrom(){
        Transaction transaction1 = new Transaction(11,10,10);
        Transaction transaction2 = new Transaction(10,10,10);
        assertNotEquals(transaction1.toString(),transaction2.toString());
    }
}
