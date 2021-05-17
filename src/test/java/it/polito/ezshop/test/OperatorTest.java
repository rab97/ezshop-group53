package it.polito.ezshop.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.polito.ezshop.model.Operator;

public class OperatorTest {

	Operator o = new Operator();
	
	@Test
	public void testIsValid1() {
		String s = "123456789104";
		assertTrue(o.isValidCode(s));
	}
	
	@Test
	public void testIsValid2() {
		String s = "4563789345138";
		assertTrue(o.isValidCode(s));
	}
	
	@Test
	public void testIsValid3() {
		String s = "45637485902647";
		assertTrue(o.isValidCode(s));
	}
	
	
	@Test
	public void testIsValid4() {
		String s = "12345678910";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void testIsValid5() {
		String s = "456374859026475";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void testIsValid6() {
		String s = "";
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void testIsValid7() {
		String s = null;
		assertFalse(o.isValidCode(s));
	}
	
	@Test
	public void testLuhnCheck1() {
		String s = "4485370086510891";
		assertTrue(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck2() {
		String s = "4716258050958645";
		assertTrue(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck3() {
		String s = "45637485902647";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck4() {
		String s = "123456789102345";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck5() {
		String s = "1234567812345678";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck6() {
		String s = "";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck7() {
		String s = null;
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void testLuhnCheck8() {
		String s = "ashudsallidò";
		assertFalse(o.luhnCheck(s));
	}
	
	@Test
	public void testCheckCreditCardAmountWithValidAmount() {
		assertTrue(o.checkCreditCardAmount("4485370086510891", 10.5, true));
		assertTrue(o.checkCreditCardAmount("4485370086510891", 10.5, false));
		assertTrue(o.checkCreditCardAmount("100293991053009", 9.5, true));
		assertTrue(o.checkCreditCardAmount("100293991053009", 9.5, false));
 
	}
	
	@Test
	public void testCheckCreditCardAmountWithInvalidCreditCard() {
		assertFalse(o.checkCreditCardAmount("4485370346510892", 10.5, true));
		assertFalse(o.checkCreditCardAmount("4485370346510892", 10.5, false));
		assertFalse(o.checkCreditCardAmount(null, 10.4, true));
		assertFalse(o.checkCreditCardAmount("", 2.0, false));
	}
	
	@Test
	public void testCheckCreditCardAmountWithAmountTooBig() {
		assertFalse(o.checkCreditCardAmount("100293991053009", 155.4, true));
		//assertTrue(o.checkCreditCardAmount("100293991053009", 155.4, false));
	}
	
	@Test
	public void testCheckCreditCardAmountWithNegativeAmount() {
		assertFalse(o.checkCreditCardAmount("100293991053009", -10.4, true));
		assertFalse(o.checkCreditCardAmount("100293991053009", -10.4, false));
	}
	
	@Test
	public void testUpdateCreditCardAmountWithValidValue() {
		assertTrue(o.updateCreditCardAmount("4485370086510891", 10.5, true));
		assertTrue(o.updateCreditCardAmount("4485370086510891", 10.5, false));
		assertTrue(o.updateCreditCardAmount("4716258050958645", 22.5, false));
		assertTrue(o.updateCreditCardAmount("4716258050958645", 22.5, true));
	}

	
	@Test
	public void testUpdateCreditCardAmountWithInvalidCode() {
		assertFalse(o.updateCreditCardAmount(null, 10.5, true));
		assertFalse(o.updateCreditCardAmount(null, 10.5, false));
		assertFalse(o.updateCreditCardAmount("", 10.5, false));
		assertFalse(o.updateCreditCardAmount("", 10.5, true));
	}
	
	@Test
	public void testUpdateCreditCardWithInvalidAmount() {
		assertFalse(o.updateCreditCardAmount("4716258050958645", -10.5, true));
		assertFalse(o.updateCreditCardAmount("4716258050958645", -10.5, false));
		assertFalse(o.updateCreditCardAmount("4716258050958645", 0.0, false));
		assertFalse(o.updateCreditCardAmount("4716258050958645", 0.0, true));
	}
	
	@Test
	public void testUpdateCreditCardWithCodeNotFound() {
		assertFalse(o.updateCreditCardAmount("21231321312322", 210.5, true));
		assertFalse(o.updateCreditCardAmount("21431431314311", 13210.5, false));
		assertFalse(o.updateCreditCardAmount("adsdsad212121sad", 2110.5, false));
	}
	
}
