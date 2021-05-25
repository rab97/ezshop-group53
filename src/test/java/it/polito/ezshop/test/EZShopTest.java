package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.Constants;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;
import it.polito.ezshop.exceptions.InvalidProductIdException;
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
		
		ezShop.createProductType("description", "1234567891231", 5.0, "note");
		assertEquals(Integer.valueOf(-1), ezShop.createProductType("description", "1234567891231", 5.0, "note"));
		ezShop.reset();
		
	}
	
	@Test
	public void testCreateProductTypeValidProduct() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		assertEquals(Integer.valueOf(1), ezShop.createProductType("description", "884846564847", 5.0, "note"));
		ezShop.reset();
		
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
		ezShop.reset();
		assertFalse(ezShop.updateProduct(1, "description", "1234567891231", 5.0, "note"));
	}
	
	@Test
	public void testProductUpdateExistingBarCode() throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test existing bar_code
		ezShop.createProductType("description", "1234567891231", 5.0, "note");
		ezShop.createProductType("description", "785462151575", 5.0, "note");
		assertFalse(ezShop.updateProduct(1, "description", "785462151575", 5.0, "note"));
		ezShop.reset();
	}
	
	@Test
	public void testProductUpdateValid() throws InvalidProductIdException, UnauthorizedException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test valid update
		ezShop.createProductType("description", "1234567891231", 5.0, "note");
		assertTrue(ezShop.updateProduct(1, "description", "785462151575", 5.0, "note"));
		ezShop.reset();
	}
	
	@Test
	public void testDeleteProductTypeInvalidId() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test null id
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.deleteProductType(null);
		});
		
		//Test id negative
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.deleteProductType(-1);
		});
		
		//Test id=0
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.deleteProductType(0);
		});
	} 
	
	@Test
	public void testDeleteProductTypeUnauthorizedException() throws InvalidProductIdException, UnauthorizedException {
		//Test no user
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.deleteProductType(1);
		});
				
				
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
				
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.deleteProductType(1);
		});
				
				
		//Test user role=CASHIER
		user.setRole("role");
		ezShop.setRunningUser(user);
				
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.deleteProductType(1);
		});
	} 
	
	@Test
	public void testDeleteProductNotExisting() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		ezShop.reset();
		assertFalse(ezShop.deleteProductType(1));
	}
	
	@Test
	public void testDeleteProductValidProduct() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
		} catch (InvalidProductDescriptionException e) {
			fail("Unexpected exception: " + e );
		} catch (InvalidProductCodeException e) {
			fail("Unexpected exception: " + e );
		} catch (InvalidPricePerUnitException e) {
			fail("Unexpected exception: " + e );
		} catch (UnauthorizedException e) {
			fail("Unexpected exception: " + e );
		}
		assertTrue(ezShop.deleteProductType(1));	
	}
	
	@Test
	public void testGetAllProductTypeUnauthorizedException() throws UnauthorizedException {
		//Test no user
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.getAllProductTypes();
		});
				
				
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
				
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.getAllProductTypes();
		});
	} 
	
	@Test
	public void testGetAllProductTypeValid() throws UnauthorizedException{
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		List<ProductType> products = new ArrayList<>();
		
		//Test no products
		products = ezShop.getAllProductTypes();
		assertEquals(0, products.size());
		
		//Test products list with 1 one product
		try {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
			products = ezShop.getAllProductTypes();
			assertEquals(1, products.size());
			ezShop.reset();
		} catch (InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException
				| UnauthorizedException e) {
			fail("Unexpected Exception" + e);
		}
		
	}
	
	@Test 
	public void testGetProductTypeByBarCodeUnauthorizedException() throws UnauthorizedException, InvalidProductCodeException{
		//Test no user
		ezShop.setRunningUser(null);
			assertThrows(UnauthorizedException.class, () -> {
				ezShop.getProductTypeByBarCode("1234567891231");
		});
							
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
						
		assertThrows(UnauthorizedException.class, () -> {
				ezShop.getProductTypeByBarCode("1234567891231");
		});
		
		//Test user= CASHIER
		user.setRole(Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {	
			ezShop.getProductTypeByBarCode("1234567891231");
		});
	}
	
	@Test 
	public void testGetProductTypeByBarCodeInvalidBarCode() throws UnauthorizedException, InvalidProductCodeException {

		//Test product code null
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.getProductTypeByBarCode(null);
		});

		//Test product code empty
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.getProductTypeByBarCode("");
		});
				
		//Test product code is valid code
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.getProductTypeByBarCode("123456");
		});
	
		//Test product code is a number
		assertThrows(InvalidProductCodeException.class, () -> {
			ezShop.getProductTypeByBarCode("productCode");
		});
	}
	
	@Test
	public void testGetProductTypeByBarCodeProductNotExists() throws UnauthorizedException, InvalidProductCodeException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		assertEquals(null, ezShop.getProductTypeByBarCode("1234567891231"));
	}
	
	@Test
	public void testGetProductTypeByBarCodeValidProduct() throws UnauthorizedException, InvalidProductCodeException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
			assertEquals("1234567891231", ezShop.getProductTypeByBarCode("1234567891231").getBarCode());
			ezShop.reset();
		} catch (InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException
				| UnauthorizedException e) {
			fail("Unexpected Exception" + e);
		}
	}
	
	@Test
	public void testGetProductTypeByDescriptionUnauthorizedException() throws UnauthorizedException {
		//Test no user
		ezShop.setRunningUser(null);
			assertThrows(UnauthorizedException.class, () -> {
			ezShop.getProductTypesByDescription("description");
		});
									
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
								
		assertThrows(UnauthorizedException.class, () -> {
				ezShop.getProductTypesByDescription("description");
		});
				
		//Test user= CASHIER
		user.setRole(Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {	
			ezShop.getProductTypesByDescription("description");
		});
	}
	
	@Test
	public void testGetProductTypeByDescriptionProductsNotExist() throws UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		assertEquals(0, ezShop.getProductTypesByDescription("description").size());
	}
	
	@Test
	public void testGetProductTypeByDescriptionValid() throws UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.createProductType("description", "1234567891231", 5.0, "note");
		} catch (InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException
				| UnauthorizedException e) {
			fail("Unexpected Exception" + e);
		}
		
		//Test part of description
		assertEquals(1, ezShop.getProductTypesByDescription("des").size());
		
		//Test empty description
		assertEquals(1, ezShop.getProductTypesByDescription("").size());
		
		//Test null description
		assertEquals(1, ezShop.getProductTypesByDescription(null).size());
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
