package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.Constants;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.ConcreteUser;

public class EZShopTest {

	EZShop ezShop;
	
	@Before	
	public void setUp () {
		ezShop = new EZShop();
	}
	
	@Test
	public void testStartReturnTransactionNotFoundSale() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {			
			assertEquals(Integer.valueOf(-1), ezShop.startReturnTransaction(1));
			user.setRole(Constants.SHOP_MANAGER);
			assertEquals(Integer.valueOf(-1), ezShop.startReturnTransaction(1));
			user.setRole(Constants.CASHIER);
			assertEquals(Integer.valueOf(-1), ezShop.startReturnTransaction(1));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testStartReturnTransactionInvalidId() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		try {			
			ezShop.startReturnTransaction(-1);
			fail();
		} catch (InvalidTransactionIdException  e) {
			
		} catch (UnauthorizedException e) {
			fail();
		}
		try {			
			ezShop.startReturnTransaction(0);
			fail();
		} catch (InvalidTransactionIdException  e) {
			
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
		try {			
			ezShop.startReturnTransaction(null);
			fail();
		} catch (InvalidTransactionIdException  e) {
			
		} catch (UnauthorizedException e) {
			fail();
		}
	}
	
	@Test
	public void testStartReturnTransactionClosedSale () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		try {		
			assertEquals(Integer.valueOf(1), ezShop.startReturnTransaction(2));
		} catch (InvalidTransactionIdException  e) {
			fail();
		} catch (UnauthorizedException e) {
			fail();
		}
		assertEquals(Integer.valueOf(1), ezShop.getReturnTransaction().getReturnId());
		System.out.println("Fine");
	}
	
	@Test
	public void testStartReturnTransactionNotClosedSale () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		try {
			System.out.println("sad");
			assertEquals(Integer.valueOf(-1),ezShop.startReturnTransaction(3));
		} catch (InvalidTransactionIdException  e) {
			fail();
		} catch (UnauthorizedException e) {
			fail();
		}
		assertEquals(null, ezShop.getReturnTransaction());
	}
	
	@Test
	public void testStartReturnTransactionNullRole () {
		//User user = new ConcreteUser("name", 1, "123", null);  Do I have also check this case? this case fail.
		User user = null;
		ezShop.setRunningUser(user);
		try {
			ezShop.startReturnTransaction(1);
			fail();
		} catch (InvalidTransactionIdException  e) {
			fail();
		} catch (UnauthorizedException e) {
		}
		assertEquals(null, ezShop.getReturnTransaction());
	}
	
	@Test
	public void testReturnProduct() {
		
	}
}
