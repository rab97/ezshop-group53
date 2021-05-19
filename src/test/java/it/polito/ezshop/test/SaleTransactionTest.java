package it.polito.ezshop.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.model.ConcreteSaleTransaction;

public class SaleTransactionTest {

    SaleTransaction st= new ConcreteSaleTransaction();

    @Test
    public void testSaleTransacionSetTicketNumber(){

        st.setTicketNumber(null);
		assertEquals(null, st.getTicketNumber());
		st.setTicketNumber(-21331);
		assertEquals(Integer.valueOf(-21331), st.getTicketNumber());
		st.setTicketNumber(21331);
		assertEquals(Integer.valueOf(21331), st.getTicketNumber());
    }

    //Le entries sono Ticket! Non posso usare altre cla)ssi nello unit test!
	/*
    @Test
	public void testSaleTransactionSetEntries() {
		st.setEntries(null);
        assertEquals(null, st.getEntries());
        //Altri casi

	}
    */

    @Test
    public void testSaleTransactionSetDiscountRate(){

        st.setDiscountRate(0.0);
        assertTrue(st.getDiscountRate() == 0.0);
		st.setDiscountRate(-299.45);
		assertTrue(st.getDiscountRate() == -299.45);
		st.setDiscountRate(299.45);
		assertTrue(st.getDiscountRate() == 299.45);
    }

    @Test
    public void testSaleTransactionSetPrice(){
	
		st.setPrice(0.0);
		assertTrue(st.getPrice() == 0.0);
		st.setPrice(-10000.01);
		assertTrue(st.getPrice() == -10000.01);
		st.setPrice(10000.01);
		assertTrue(st.getPrice() == 10000.01);	
    }

    @Test
	public void testSaleTransactionSetPayed(){

		st.setPayed(true);
		assertTrue(st.getPayed()==true);
		st.setPayed(false);
		assertTrue(st.getPayed()==false);
	}
    
}
