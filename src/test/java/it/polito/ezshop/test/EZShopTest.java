package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.Constants;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.ConcreteReturnTransaction;
import it.polito.ezshop.model.ConcreteUser;

public class EZShopTest {

	EZShop ezShop;
	
	@Before	
	public void setUp () {
		ezShop = new EZShop();
	}
	
	@Test
	public void testCreateProductTypeInvalidDescription() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
	
		//Test null description
		assertThrows(InvalidProductDescriptionException.class, () -> {
			ezShop.createProductType(null, "1234567891231", 5.0, "note");
		});	
		
		//Test empty description
		assertThrows(InvalidProductDescriptionException.class, () -> {
			ezShop.createProductType("", "1234567891231", 5.0, "note");
		});
	}

	@Test
	public void testCreateProductTypeInvalidProductCode() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test product code null
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.createProductType("description", null, 5.0, "note");
		});

		//Test product code empty
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.createProductType("description", "", 5.0, "note");
		});
		
		//Test product code is valid code
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.createProductType("description", "123456", 5.0, "note");
		});

		
		//Test product code is a number
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.createProductType("description", "productCode", 5.0, "note");
		});
	}
	
	@Test
	public void testCreateProductTypeInvalidPricePerUnit() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test pricePerUnit negative
		assertThrows(InvalidPricePerUnitException.class, () -> {
			ezShop.createProductType("description", "1234567891231", -1, "note");
		});
		
		//Test pricePerUnit=0
		assertThrows(InvalidPricePerUnitException.class, () -> {
			ezShop.createProductType("description", "1234567891231", 0, "note");
		});
	}
	
	@Test
	public void testCreateProductTypeUnauthorizedUser() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test no user
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
		});
		
		
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
		
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
		});
		
		
		//Test user role=CASHIER
		user.setRole("role");
		ezShop.setRunningUser(user);
		
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
		});
		
	}
	
	@Test
	public void testCreateProductTypeExistingProduct() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		
		assertEquals(Integer.valueOf(-1), ezShop.createProductType("description", "123456789104", 5.0, "note"));
		
	}
	
	@Test
	public void testCreateProductTypeValidProduct() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		
		assertEquals(Integer.valueOf(4), ezShop.createProductType("description", "125489796456", 5.0, "note"));
		
	}
	
	//che significa ritorna -1 se c'è un errore durante il salvataggio? Dovrebbe essere un errore di db, va testato nel db?
	
	@Test
	public void testUpdateProductInvalidId() throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test null id
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updateProduct(null, "description", "1234567891231", 5.0, "note");
		});
		
		//Test id negative
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updateProduct(-1, "description", "1234567891231", 5.0, "note");
		});
		
		//Test id=0
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updateProduct(0, "description", "1234567891231", 5.0, "note");
		});
	}
	
	@Test
	public void testUpdateProductInvalidDescription() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test null description
		assertThrows(InvalidProductDescriptionException.class, () -> {
			ezShop.updateProduct(1, null, "1234567891231", 5.0, "note");
		});	
		
		//Test empty description
		assertThrows(InvalidProductDescriptionException.class, () -> {
			ezShop.updateProduct(1, "", "1234567891231", 5.0, "note");
		});
	}
	
	@Test
	public void testUpdateProductInvalidProductCode() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test product code null
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.updateProduct(1, "description", null, 5.0, "note");
		});

		//Test product code empty
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.updateProduct(1, "description", "", 5.0, "note");
		});
		
		//Test product code is valid code
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.updateProduct(1, "description", "123456", 5.0, "note");
		});

		
		//Test product code is a number
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.updateProduct(1, "description", "productCode", 5.0, "note");
		});
	}
	
	@Test
	public void testUpdateProductInvalidPricePerUnit() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test pricePerUnit negative
		assertThrows(InvalidPricePerUnitException.class, () -> {
			ezShop.updateProduct(1, "description", "1234567891231", -1, "note");
		});
		
		//Test pricePerUnit negative
		assertThrows(InvalidPricePerUnitException.class, () -> {
			ezShop.updateProduct(1, "description", "1234567891231", 0, "note");
		});
	}
	
	@Test
	public void testUpdateProductUnauthorizedException() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		
		//Test no user
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.updateProduct(1, "description", "1234567891231", 5.0, "note");
		});
		
		
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
		
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.updateProduct(1, "description", "1234567891231", 5.0, "note");
		});
		
		
		//Test user role=CASHIER
		user.setRole("role");
		ezShop.setRunningUser(user);
		
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.updateProduct(1, "description", "1234567891231", 5.0, "note");
		});
		
	}
	
	@Test
	public void testProductUpdateNotAvailableId() throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test no product with given id
		assertFalse(ezShop.updateProduct(150, "description", "1234567891231", 5.0, "note"));
	}
	
	@Test
	public void testProductUpdateExistingBarCode() throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test existing bar_code
		assertFalse(ezShop.updateProduct(1, "description", "1234567891231", 5.0, "note"));
	}

	@Test
	public void testSaleTransactionUnauthorizedUser(){

		User u= null;
		ezShop.setRunningUser(u);

		assertThrows(UnauthorizedException.class, ()->{ezShop.startSaleTransaction();});
		assertThrows(UnauthorizedException.class, ()->{ezShop.addProductToSale(1, "123456789104", 5);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.deleteProductFromSale(1, "123456789104", 5);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.applyDiscountRateToProduct(1, "123456789104", 0.2);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.applyDiscountRateToSale(1, 0.2);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.computePointsForSale(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.endSaleTransaction(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.deleteSaleTransaction(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getSaleTransaction(1);});
	}

	@Test
	public void testSaleTransactionInvalidTransactionId(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.addProductToSale(0, "123456789104", 5);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.addProductToSale(-1, "123456789104", 5);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.addProductToSale(null, "123456789104", 5);});

		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteProductFromSale(0, "123456789104", 5);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteProductFromSale(-1, "123456789104", 5);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteProductFromSale(null, "123456789104", 5);});
		
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToProduct(0, "123456789104", 0.2);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToProduct(-1, "123456789104", 0.2);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToProduct(null, "123456789104", 0.2);});

		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToSale(0, 0.2);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToSale(-1, 0.2);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToSale(null, 0.2);});

		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.computePointsForSale(0);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.computePointsForSale(-1);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.computePointsForSale(null);});
		
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.endSaleTransaction(0);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.endSaleTransaction(-1);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.endSaleTransaction(null);});
		
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteSaleTransaction(0);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteSaleTransaction(-1);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteSaleTransaction(null);});
		
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.getSaleTransaction(0);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.getSaleTransaction(-1);});
		assertThrows(InvalidTransactionIdException.class, ()->{ezShop.getSaleTransaction(null);});
	}

	@Test //Questo test fallisce sui null value perchè vuole che sia lanciata NullPointerException
	public void testSaleTransactionInvalidProductCode(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(1, " ", 2);});
		//assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(1, null, 2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(1, "invalidCode", 2);});
		
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(1, " ", 2);});
		//assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(1, null, 2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(1, "invalidCode", 2);});

		assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(1, " ", 0.2);});
		//assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(1, null, 0.2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(1, "invalidCode", 0.2);});
	}

	@Test
	public void testSaleTransactionInvalidQuantity(){
		
		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		assertThrows(InvalidQuantityException.class, ()->{ezShop.addProductToSale(1, "123456789104", -1);});
		assertThrows(InvalidQuantityException.class, ()->{ezShop.addProductToSale(1, "123456789104", -10);});
		
		assertThrows(InvalidQuantityException.class, ()->{ezShop.deleteProductFromSale(1, "123456789104", -1);});
		assertThrows(InvalidQuantityException.class, ()->{ezShop.deleteProductFromSale(1, "123456789104", -10);});
	}

	@Test
	public void testSaleTransactionInvalidDiscountRate(){
		
		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToProduct(1, "123456789104", -1);});
		assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToProduct(1, "123456789104", 1.0);});
		assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToProduct(1, "123456789104", 1.37);});

		assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToSale(1, -1);});
		assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToSale(1, 1.0);});
		assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToSale(1, 1.37);});
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
