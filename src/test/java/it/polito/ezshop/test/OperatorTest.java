package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.polito.ezshop.model.Operator;

public class OperatorTest {

	
	
	@Test
	public void TestIsValid1() {
		Operator o = new Operator();
		String s = "123456789104";
		assertTrue(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid2() {
		Operator o = new Operator();
		String s = "4563789345138";
		assertTrue(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid3() {
		Operator o = new Operator();
		String s = "45637485902647";
		assertTrue(o.isValidCode(s));
	}
	
	
	@Test
	public void TestIsValid4() {
		Operator o = new Operator();
		String s = "12345678910";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid5() {
		Operator o = new Operator();
		String s = "456374859026475";
		assertFalse(o.isValidCode(s));
	}
}
