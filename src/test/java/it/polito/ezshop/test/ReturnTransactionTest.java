package it.polito.ezshop.test;
import static org.junit.Assert.assertTrue;

import java.util.List;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.ConcreteReturnTransaction;

public class ReturnTransactionTest {

    ReturnTransaction retTransaction = new ConcreteReturnTransaction();

     

	
	@Test
	public void testCostructor(){
		ReturnTransaction r = new ConcreteReturnTransaction(1, 1, null, 2.2, 15);
		assertEquals(Integer.valueOf(1), r.getTransactionId());
		assertEquals(Integer.valueOf(1), r.getReturnId());
		assertEquals(null, r.getEntries());
		assertTrue(2.2 == r.getPrice());
		assertTrue(15 == r.getDiscountRate());
	}
	
	@Test
	public void testReturnTransactionSetReturnId(){
		retTransaction.setReturnId(0);
		assertEquals(Integer.valueOf(0), retTransaction.getReturnId());
		retTransaction.setReturnId(-500);
		assertEquals(Integer.valueOf(-500), retTransaction.getReturnId());
		retTransaction.setReturnId(500);
		assertEquals(Integer.valueOf(500), retTransaction.getReturnId());
	}
	
    @Test
	public void testReturnTransactionSetTransactionId(){
		retTransaction.setTransactionId(0);
		assertTrue(retTransaction.getTransactionId()==0);
		retTransaction.setTransactionId(-500);
		assertTrue(retTransaction.getTransactionId()==-500);
		retTransaction.setTransactionId(500);
		assertTrue(retTransaction.getTransactionId()==500);
	}

    @Test
	public void testReturnTransactionSetEntries() {
		retTransaction.setEntries(null);
        assertEquals(null, retTransaction.getEntries());
	}

    @Test
    public void testReturnTransactionSetPrice(){
		retTransaction.setPrice(0.0);
		assertTrue(retTransaction.getPrice() == 0.0);
		retTransaction.setPrice(-10000.01);
		assertTrue(retTransaction.getPrice() == -10000.01);
		retTransaction.setPrice(10000.01);
		assertTrue(retTransaction.getPrice() == 10000.01);	
    }

	@Test
	public void testReturnTransactionSetPayed(){

		retTransaction.setPayed(true);
		assertTrue(retTransaction.getPayed()==true);
		retTransaction.setPayed(false);
		assertTrue(retTransaction.getPayed()==false);
	}

	@Test
    public void testReturnTransactionSetDiscountRate(){

        retTransaction.setDiscountRate(0.0);
        assertTrue(retTransaction.getDiscountRate() == 0.0);
		retTransaction.setDiscountRate(-299.45);
		assertTrue(retTransaction.getDiscountRate() == -299.45);
		retTransaction.setDiscountRate(299.45);
		assertTrue(retTransaction.getDiscountRate() == 299.45);
    }


}
