package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.polito.ezshop.model.ConcreteOrder;
import it.polito.ezshop.data.Order;

public class OrderTest {
	
	Order o = new ConcreteOrder();
	
	@Test
	public void testCostructor() {
		Order order = new ConcreteOrder(3, "1234", 90.9, 500, "ISSUED", 1);

		assertEquals(Integer.valueOf(3), order.getBalanceId());
		assertEquals("1234", order.getProductCode());
		assertTrue(90.9 == order.getPricePerUnit());
		assertEquals(500, order.getQuantity());
		assertEquals(Integer.valueOf(1), order.getOrderId());
	}
	
	@Test
	public void testOrderSetBalanceId() {
		o.setBalanceId(null);
		assertEquals(null, o.getBalanceId());
		o.setBalanceId(-21331);
		assertEquals(Integer.valueOf(-21331), o.getBalanceId());
		o.setBalanceId(21331);
		assertEquals(Integer.valueOf(21331), o.getBalanceId());
	}
	
	@Test
	public void testOrderSetProductCode() {
		o.setProductCode(null);
		assertEquals(null, o.getProductCode());
		o.setProductCode("");
		assertEquals("", o.getProductCode());
		o.setProductCode("product_code");
		assertEquals("product_code", o.getProductCode());
	}
	
	@Test
	public void testOrderSetPricePerUnit() {
		o.setPricePerUnit(0);
		assertTrue(o.getPricePerUnit() == 0);
		o.setPricePerUnit(-2000.64);
		assertTrue(o.getPricePerUnit() == -2000.64);
		o.setPricePerUnit(2000.64);
		assertTrue(o.getPricePerUnit() == 2000.64);
	}
	
	@Test
	public void testOrderSetQuantity() {
		o.setQuantity(0);
		assertTrue(o.getQuantity() == 0);
		o.setQuantity(50);
		assertTrue(o.getQuantity() == 50);
		o.setQuantity(-50);
		assertTrue(o.getQuantity() == -50);
	}
	
	@Test
	public void testOrderSetStatus() {
		o.setStatus(null);
		assertEquals(null, o.getStatus());
		o.setStatus("status");
		assertEquals("status", o.getStatus());
		o.setStatus("");
		assertEquals("", o.getStatus());
	}
	
	@Test
	public void testOrderSetOrderId() {
		o.setOrderId(null);
		assertEquals(null, o.getOrderId());
		o.setOrderId(-21331);
		assertEquals(Integer.valueOf(-21331), o.getOrderId());
		o.setOrderId(21331);
		assertEquals(Integer.valueOf(21331), o.getOrderId());
	}
}
