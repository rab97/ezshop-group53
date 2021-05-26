package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.ConcreteTicketEntry;

public class TicketEntryTest {

    TicketEntry te= new ConcreteTicketEntry();

    @Test
	public void testCostructor() {
    	TicketEntry t = new ConcreteTicketEntry("1234", "desc", 0, 100.09,10);
		assertEquals("1234", t.getBarCode());
		assertEquals("desc", t.getProductDescription());
		assertEquals(0, t.getAmount());
		assertTrue(100.09 == t.getPricePerUnit());
		assertTrue(10 == t.getDiscountRate());
		
	}
   
    @Test
	public void testTicketEntrySetBarCode() {
		te.setBarCode(null);
		assertEquals(null, te.getBarCode());
		te.setBarCode("");
		assertEquals("", te.getBarCode());
		te.setBarCode("bar_code");
		assertEquals("bar_code", te.getBarCode());
	}

    @Test
	public void testTicketEntrySetProductDescription() {
		te.setProductDescription(null);
		assertEquals(null, te.getProductDescription());
		te.setProductDescription("");
		assertEquals("", te.getProductDescription());
		te.setProductDescription("note");
		assertEquals("note", te.getProductDescription());
	}

    @Test
	public void testTicketEntrySetAmount() {
		te.setAmount(-21331);
        assertTrue(te.getAmount()==-21331);
		te.setAmount(21331);
        assertTrue(te.getAmount()== 21331);
	}
    
    @Test
	public void testTicketEntrySetPricePerUnit() {
		te.setPricePerUnit(0.0);
		assertTrue(te.getPricePerUnit() == 0.0);
		te.setPricePerUnit(-299.45);
		assertTrue(te.getPricePerUnit() == -299.45);
		te.setPricePerUnit(299.45);
		assertTrue(te.getPricePerUnit() == 299.45);	
	}

    @Test
    public void testTicketEntrySetDiscountRate(){

        te.setDiscountRate(0.0);
        assertTrue(te.getDiscountRate() == 0.0);
		te.setDiscountRate(-299.45);
		assertTrue(te.getDiscountRate() == -299.45);
		te.setDiscountRate(299.45);
		assertTrue(te.getDiscountRate() == 299.45);
    }

}
