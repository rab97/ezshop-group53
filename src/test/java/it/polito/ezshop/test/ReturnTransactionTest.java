package it.polito.ezshop.test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


import org.junit.Test;

import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.model.ConcreteReturnTransaction;

public class ReturnTransactionTest {

    ReturnTransaction retTransaction = new ConcreteReturnTransaction();

    //id, transaction id, entries, price, payed, discount rate 

	@Test
	public void testReturnTransactionSetTransactionId(){

		retTransaction.setTransactionId(0);
		assertTrue(retTransaction.getTransactionId()==0);
		retTransaction.setTransactionId(-500);
		assertTrue(retTransaction.getTransactionId()==-500);
		retTransaction.setTransactionId(500);
		assertTrue(retTransaction.getTransactionId()==500);
	}

	//Le entries sono Ticket! Non posso usare altre classi nello unit test!
	/*
    @Test
	public void testReturnTransactionSetEntries() {
		retTransaction.setEntries(null);
        assertEquals(null, retTransaction.getEntries());
        //Altri casi

	}
    */

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
