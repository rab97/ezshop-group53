package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.model.ConcreteProductType;

public class ProductTypeTest {
	
	ProductType pt = new ConcreteProductType();
	
	@Test
	public void testProductTypeSetQuantity() {
		pt.setQuantity(null);
		assertEquals(null, pt.getQuantity());
		pt.setQuantity(-21331);
		assertEquals(Integer.valueOf(-21331), pt.getQuantity());
		pt.setQuantity(21331);
		assertEquals(Integer.valueOf(21331), pt.getQuantity());
	}
	
	@Test
	public void testProductTypeSetLocation() {
		pt.setLocation(null);
		assertEquals(null, pt.getLocation());
		pt.setLocation("");
		assertEquals("", pt.getLocation());
		pt.setLocation("location");
		assertEquals("location", pt.getLocation());
	}
	
	@Test
	public void testProductTypeSetNote() {
		pt.setNote(null);
		assertEquals(null, pt.getNote());
		pt.setNote("");
		assertEquals("", pt.getNote());
		pt.setNote("note");
		assertEquals("note", pt.getNote());
	}
	
	@Test
	public void testProductTypeSetProductDescription() {
		pt.setProductDescription(null);
		assertEquals(null, pt.getProductDescription());
		pt.setProductDescription("");
		assertEquals("", pt.getProductDescription());
		pt.setProductDescription("note");
		assertEquals("note", pt.getProductDescription());
	}
	
	@Test
	public void testProductTypeSetBarCode() {
		pt.setBarCode(null);
		assertEquals(null, pt.getBarCode());
		pt.setBarCode("");
		assertEquals("", pt.getBarCode());
		pt.setBarCode("bar_code");
		assertEquals("bar_code", pt.getBarCode());
	}
	
	@Test
	public void testProductTypeSetPricePerUnit() {
		pt.setPricePerUnit(null);
		assertEquals(null, pt.getPricePerUnit());
		pt.setPricePerUnit(0.0);
		assertTrue(pt.getPricePerUnit() == 0.0);
		pt.setPricePerUnit(-256.35);
		assertTrue(pt.getPricePerUnit() == -256.35);
		pt.setPricePerUnit(256.35);
		assertTrue(pt.getPricePerUnit() == 256.35);	
	}
	
	@Test
	public void testProductTypeSetId() {
		pt.setId(0);
		assertEquals(Integer.valueOf(0), pt.getId());
		pt.setId(-21331);
		assertEquals(Integer.valueOf(-21331), pt.getId());
		pt.setId(21331);
		assertEquals(Integer.valueOf(21331), pt.getId());
	}
	
	
}
