package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.polito.ezshop.model.Operator;

public class OperatorTest {

	Operator o = new Operator();
	
	@Test
	public void TestIsValid1() {
		String s = "123456789104";
		assertTrue(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid2() {
		String s = "4563789345138";
		assertTrue(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid3() {
		String s = "45637485902647";
		assertTrue(o.isValidCode(s));
	}
	
	
	@Test
	public void TestIsValid4() {
		String s = "12345678910";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid5() {
		String s = "456374859026475";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid6() {
		String s = "";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void TestIsValid7() {
		String s = null;
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void TestLuhnCheck1() {
		String s = "4485370086510891";
		assertTrue(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck2() {
		String s = "4716258050958645";
		assertTrue(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck3() {
		String s = "45637485902647";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck4() {
		String s = "123456789102345";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck5() {
		String s = "1234567812345678";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck6() {
		String s = "";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck7() {
		String s = null;
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void TestLuhnCheck8() {
		String s = "ashudsallidò";
		assertFalse(o.luhnCheck(s));
	}
	
	
	
}
