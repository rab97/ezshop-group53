package it.polito.ezshop.test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;;


import org.junit.Test;

import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.model.ConcreteReturnTransaction;

public class ReturnTransactionTest {

    ReturnTransaction retTransaction = new ConcreteReturnTransaction();

    //id, transaction id, entries, price, payed, discount rate 

    @Test
    public void testReturnTransactionSetPrice(){
        retTransaction.setPrice(null);
		assertEquals(null, retTransaction.getPrice());
		retTransaction.setPrice(0.0);
		assertTrue(retTransaction.getPrice() == 0.0);
		retTransaction.setPrice(-10000.01);
		assertTrue(retTransaction.getPrice() == -10000.01);
		retTransaction.setPrice(10000.01);
		assertTrue(retTransaction.getPrice() == 10000.01);	
    }


    @Test
	public void testReturnTransactionSetEntries() {
		retTransaction.setEntries(null);
		assertEquals(null, pt.getQuantity());
		pt.setQuantity(-21331);
		assertEquals(Integer.valueOf(-21331), pt.getQuantity());
		pt.setQuantity(21331);
		assertEquals(Integer.valueOf(21331), pt.getQuantity());
	}
    
}
