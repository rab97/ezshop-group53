package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.Constants;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.ConcreteReturnTransaction;
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
			assertEquals(Integer.valueOf(1), ezShop.getReturnTransaction().getReturnId());
		} catch (InvalidTransactionIdException  e) {
			fail();
		} catch (UnauthorizedException e) {
			fail();
		}
		assertEquals(Integer.valueOf(1), ezShop.getReturnTransaction().getReturnId());
	}
	
	@Test
	public void testStartReturnTransactionNotClosedSale () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		try {
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
	public void testReturnProductExceedAmount () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(2);
		r.setTransactionId(3);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.returnProduct(2, "123456789104", 51));
		} catch (Exception e) {
			fail();
		}
		user.setRole(Constants.SHOP_MANAGER);
		assertEquals(Constants.SHOP_MANAGER, ezShop.getRunningUser().getRole());
		try {
			assertFalse(ezShop.returnProduct(2, "123456789104", 51));
		} catch (Exception e) {
			fail();
		}
		user.setRole(Constants.CASHIER);
		assertEquals(Constants.CASHIER, ezShop.getRunningUser().getRole());
		try {
			assertFalse(ezShop.returnProduct(2, "123456789104", 51));
		} catch (Exception e) {
			fail();
		}
	}
	
	
	@Test
	public void testReturnProductTransactioNotExists () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(2);
		r.setTransactionId(12);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.returnProduct(2, "123456789104", 1));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testReturnProductReturnNotExists () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(2);
		r.setTransactionId(3);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.returnProduct(2, "4314324224131", 1));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReturnProductProductNotExists () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(2);
		r.setTransactionId(3);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.returnProduct(1, "4314324224131", 1));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReturnProductAmountTooBig () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);		
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(2);
		r.setTransactionId(3);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.returnProduct(3, "4314324224124", 22));
		} catch (Exception e) {
			fail();
		}
		assertTrue(r.getEntries().isEmpty());
	}
	
	@Test
	public void testReturnProductCodeInvalid() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		try {
			ezShop.returnProduct(2, "", 51);
		} catch (InvalidProductCodeException e) {
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			ezShop.returnProduct(2, null, 51);
		} catch (InvalidProductCodeException e) {
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			ezShop.returnProduct(2, "21321", 51);
		} catch (InvalidProductCodeException e) {
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReturnProductNullUser() {
		User user = null;
		ezShop.setRunningUser(user);
		try {
			ezShop.returnProduct(2, "123456789104", 10);
			fail();
		} catch (UnauthorizedException e) {
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReturnProductNegativeQuanitity() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		try {
			ezShop.returnProduct(2, "123456789104", 0);
			fail();
		} catch (InvalidQuantityException e) {
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			ezShop.returnProduct(2, "123456789104", -1);
		} catch (InvalidQuantityException e) {
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReturnProductTransactionIdError () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		try {
			ezShop.returnProduct(-1, "123456789104", 51);
			fail();
		} catch (InvalidTransactionIdException e) {
			
		} catch (Exception e) {
			fail();
		}
		
		try {
			ezShop.returnProduct(0, "123456789104", 51);
			fail();
		} catch (InvalidTransactionIdException e) {
			
		} catch (Exception e) {
			fail();
		}
		
		try {
			ezShop.returnProduct(null, "123456789104", 51);
			fail();
		} catch (InvalidTransactionIdException e) {
			
		} catch (Exception e) {
			fail();
		}
	}
	
	
	@Test
	public void testReturnProductValidData() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(1);
		r.setTransactionId(2);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(ezShop.returnProduct(1, "123456789104", 1));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		assertEquals(1, r.getEntries().size());
		try {
			assertEquals(Integer.valueOf(50), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		System.out.println(r.getPrice());
		//assertTrue(0.5 == r.getPrice()); // inseire controllo anche alla fine?
		try {
			assertTrue(ezShop.returnProduct(1, "123456789104", 19));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		assertEquals(1, r.getEntries().size());
		try {
			assertEquals(Integer.valueOf(50), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			assertTrue(ezShop.returnProduct(1, "4314324224124", 2));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		assertEquals(2, r.getEntries().size());
		try {
			assertEquals(Integer.valueOf(150), ezShop.getProductTypeByBarCode("4314324224124").getQuantity());
		} catch (Exception e) {
			fail();
		}
	}

	
}
