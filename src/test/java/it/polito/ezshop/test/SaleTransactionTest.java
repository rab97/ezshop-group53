package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.ConcreteSaleTransaction;

public class SaleTransactionTest {

    SaleTransaction st= new ConcreteSaleTransaction();

    @Test
    public void testConstructor(){
    	SaleTransaction t = new ConcreteSaleTransaction(0, null, 0, 1.0);
        assertEquals(Integer.valueOf(0), t.getTicketNumber());
        assertEquals(null, t.getEntries());
        assertTrue(0 == t.getDiscountRate());
        assertTrue(1 == t.getPrice());
    }
    
    @Test
    public void testSaleTransacionSetTicketNumber(){

        st.setTicketNumber(null);
		assertEquals(null, st.getTicketNumber());
		st.setTicketNumber(-21331);
		assertEquals(Integer.valueOf(-21331), st.getTicketNumber());
		st.setTicketNumber(21331);
		assertEquals(Integer.valueOf(21331), st.getTicketNumber());
    }

    @Test
	public void testSaleTransactionSetEntries() {
		st.setEntries(null);
        assertEquals(null, st.getEntries());
        st.setEntries(new ArrayList<>());
        assertTrue(st.getEntries().isEmpty());
	}
    
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
