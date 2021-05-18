package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.model.ConcreteCustomer;

public class CustomerTest {

	Customer c = new ConcreteCustomer();
	
	
	@Test
	public void testCostructor() {
		Customer c = new ConcreteCustomer(33, "name", "1231212312321", 22);
		assertEquals(Integer.valueOf(33), c.getId());
		assertEquals("name", c.getCustomerName());
		assertEquals("1231212312321", c.getCustomerCard());
		assertEquals(Integer.valueOf(22), c.getPoints());
	}
	
	@Test
	public void testSetAndGetCostumerName() {
		c.setCustomerName(null);
		assertEquals(null, c.getCustomerName());
		c.setCustomerName("customer");
		assertEquals("customer", c.getCustomerName());
		c.setCustomerName("");
		assertEquals("", c.getCustomerName());
	}
	
	@Test
	public void testSetAndGetCostumerCard() {
		c.setCustomerCard(null);
		assertEquals(null, c.getCustomerCard());
		c.setCustomerCard("customerCard12313");
		assertEquals("customerCard12313", c.getCustomerCard());
		c.setCustomerCard("");
		assertEquals("", c.getCustomerCard());
	}
	
	@Test
	public void testSetAndGetId() {
		c.setId(null);
		assertEquals(null, c.getId());
		c.setId(213131);
		assertEquals(Integer.valueOf(213131), c.getId());
		c.setId(-21331);
		assertEquals(Integer.valueOf(-21331), c.getId());
	}
	
	@Test
	public void testSetAndGetPoints() {
		c.setId(null);
		assertEquals(null, c.getId());
		c.setId(21);
		assertEquals(Integer.valueOf(21), c.getId());
		c.setId(0);
		assertEquals(Integer.valueOf(0), c.getId());
	}
}
