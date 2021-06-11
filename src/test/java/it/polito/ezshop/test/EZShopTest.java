package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import it.polito.ezshop.Constants;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.ConcreteUser;
import it.polito.ezshop.model.Operator;
import it.polito.ezshop.model.ConcreteCustomer;
import it.polito.ezshop.model.ConcreteProduct;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.model.ConcreteReturnTransaction;
import it.polito.ezshop.model.ConcreteSaleTransaction;
import it.polito.ezshop.model.ConcreteTicketEntry;
import it.polito.ezshop.model.ConcreteUser;
import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;

import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EZShopTest {

	EZShop ezShop;
	IDAOEZshop dao;
	Operator o = new Operator();
	
	@Before	
	public void setUp () throws DAOException {
		ezShop = new EZShop();
		dao = new DAOEZShop();
		dao.resetApplication();
	}
	
	
	@Test
	public void testUserInvalidUsername(){

		assertThrows(InvalidUsernameException.class, ()->{ezShop.createUser("", "a_valid_password", Constants.CASHIER);});
		assertThrows(InvalidUsernameException.class, ()->{ezShop.createUser(null, "a_valid_password", Constants.CASHIER);});

		assertThrows(InvalidUsernameException.class, ()->{ezShop.login("", "a_valid_password");});
		assertThrows(InvalidUsernameException.class, ()->{ezShop.login(null, "a_valid_password");});
	}

	@Test
	public void testUserInvalidPassword(){

		assertThrows(InvalidPasswordException.class, ()->{ezShop.createUser("validUsername", "", Constants.CASHIER);});
		assertThrows(InvalidPasswordException.class, ()->{ezShop.createUser("validUsername", null, Constants.CASHIER);});

		assertThrows(InvalidPasswordException.class, ()->{ezShop.login("validUsername", "");});
		assertThrows(InvalidPasswordException.class, ()->{ezShop.login("validUsername", null);});
	}
	
	@Test
	public void testCreateUserCheckRole(){

		assertThrows(InvalidRoleException.class, ()->{ezShop.createUser("name", "password", "");});
		assertThrows(InvalidRoleException.class, ()->{ezShop.createUser("name", "password", null);});
		assertThrows(InvalidRoleException.class, ()->{ezShop.createUser("name", "password", "invalidRole");});

		//Come faccio ad avere un valore di riferimento per l'id dell'user che verrà creato? Come posso
		//testare i casi con Cashier,ShopManager e Admin?
	}

	@Test
	public void testUserAlreadyExists(){

		User running= new ConcreteUser("Admin", 20, "pw", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(running);

		User u= new ConcreteUser("validUsername", null, "validPassword", Constants.SHOP_MANAGER);

		try{
			Integer uId= ezShop.getDAO().insertUser(u.getUsername(), u.getPassword(), u.getRole());
			if(uId<=0){
				fail();
			}

			assertEquals(Integer.valueOf(-1), ezShop.createUser(u.getUsername(), u.getPassword(), u.getRole()));
			assertTrue(ezShop.deleteUser(uId));

		}catch(DAOException e){
			fail();
		}catch(InvalidUsernameException|InvalidPasswordException|InvalidRoleException e){
			System.out.print(e);
			fail();
		}catch(InvalidUserIdException| UnauthorizedException e){
			System.out.print(e);
			fail();
		}
	}

	@Test
	public void testUserNotExists(){

		User running= new ConcreteUser("Admin", 20, "pw", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(running);

		User wrongUser= new ConcreteUser("wrongName", 19, "wrongPw", Constants.CASHIER);

		try{
			assertFalse(ezShop.deleteUser(wrongUser.getId()));
			assertEquals(null, ezShop.getUser(wrongUser.getId()));
			assertFalse(ezShop.updateUserRights(wrongUser.getId(), Constants.SHOP_MANAGER));
			assertEquals(null, ezShop.login(wrongUser.getUsername(), wrongUser.getPassword()));

		}catch(InvalidUsernameException|InvalidPasswordException|InvalidUserIdException|
				InvalidRoleException|UnauthorizedException e){
			System.out.print(e);
			fail();
		}

	}

	@Test
	public void testGetAllUsers(){

		User u= new ConcreteUser("admin", 1, "adminPassword", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		try{
			assertEquals(0, ezShop.getAllUsers().size());

		}catch(UnauthorizedException e){
			fail();
		}
	}

	@Test
	public void testUserInvalidId(){

		User u= new ConcreteUser("admin", 1, "adminPassword", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(null);});
		assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(-1);});
		assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(0);});

		assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(null);});
		assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(-1);});
		assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(0);});

		assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(null, Constants.SHOP_MANAGER);});
		assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(-1, Constants.CASHIER);});
		assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(0, Constants.CASHIER);});

	}

	@Test
	public void testUserUnauthorizedUser(){

		User u= null;
		ezShop.setRunningUser(u);

		assertThrows(UnauthorizedException.class, ()->{ezShop.deleteUser(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getAllUsers();});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getUser(3);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.updateUserRights(5, Constants.SHOP_MANAGER);});
		assertFalse(ezShop.logout());

		u= new ConcreteUser("testUser", 2, "testPassword", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		assertThrows(UnauthorizedException.class, ()->{ezShop.deleteUser(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getAllUsers();});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getUser(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.updateUserRights(1, Constants.SHOP_MANAGER);});
	}

	@Test
	public void testCreateUserWithSuccess(){

		User u= new ConcreteUser("validName", null, "validPassword", Constants.SHOP_MANAGER);

		try{
			Integer newId= ezShop.createUser(u.getUsername(), u.getPassword(), u.getRole());
			if(newId<=0){
				fail();
			}

			assertEquals(newId, ezShop.getDAO().searchUser(u.getUsername(), u.getPassword()).getId());

			ezShop.getDAO().removeUser(newId);
		}catch(DAOException e){
			fail();
		}catch(InvalidUsernameException|InvalidPasswordException|InvalidRoleException e){
			fail();
		}
	}

	@Test
	public void testDeleteUserWithSuccess(){

		User u= new ConcreteUser("admin", 1, "adminPassword", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		User user_to_delete= new ConcreteUser("validName", null, "validPassword", Constants.CASHIER);

		try{
			Integer newId= ezShop.getDAO().insertUser(user_to_delete.getUsername(), user_to_delete.getPassword(), user_to_delete.getRole());
			if(newId<=0){
				fail();
			}

			assertTrue(ezShop.deleteUser(newId));

		}catch(DAOException e){
			fail();
		}catch(InvalidUserIdException|UnauthorizedException e){
			fail();
		}
	}

	@Test
	public void testGetUserWithSuccess(){

		User u= new ConcreteUser("admin", 1, "adminPassword", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		User user_to_get= new ConcreteUser("validName", null, "validPassword", Constants.CASHIER);

		try{
			Integer newId= ezShop.getDAO().insertUser(user_to_get.getUsername(), user_to_get.getPassword(), user_to_get.getRole());
			if(newId<=0){
				fail();
			}
			User returnedUser= ezShop.getUser(newId);

			assertEquals(newId, returnedUser.getId());
			assertEquals(user_to_get.getUsername(),returnedUser.getUsername());
			assertEquals(user_to_get.getPassword(), returnedUser.getPassword());
			assertEquals(user_to_get.getRole(), returnedUser.getRole());

			ezShop.getDAO().removeUser(newId);

		}catch(DAOException e){
			fail();
		}catch(InvalidUserIdException|UnauthorizedException e){
			fail();
		}
	}

	@Test
	public void testUpdateUserRightsWithSuccess(){

		User u= new ConcreteUser("admin", 1, "adminPassword", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		User user_to_update= new ConcreteUser("validName", null, "validPassword", Constants.CASHIER);

		try{
			Integer newId= ezShop.getDAO().insertUser(user_to_update.getUsername(), user_to_update.getPassword(), user_to_update.getRole());
			if(newId<=0){
				fail();
			}
			
			assertTrue(ezShop.updateUserRights(newId,Constants.SHOP_MANAGER));
			ezShop.getDAO().removeUser(newId);

		}catch(DAOException e){
			fail();
		}catch(InvalidUserIdException|UnauthorizedException|InvalidRoleException e){
			fail();
		}
	}

	@Test
	public void testLoginWithSuccess(){

		User u= new ConcreteUser("name", null, "password", Constants.SHOP_MANAGER);

		try{
			Integer newId= ezShop.getDAO().insertUser(u.getUsername(), u.getPassword(), u.getRole());
			if(newId<=0){
				fail();
			}
			User returnedUser= ezShop.login(u.getUsername(), u.getPassword());

			assertEquals(newId, returnedUser.getId());
			assertEquals(u.getUsername(),returnedUser.getUsername());
			assertEquals(u.getPassword(), returnedUser.getPassword());
			assertEquals(u.getRole(), returnedUser.getRole());

			ezShop.getDAO().removeUser(newId);

		}catch(DAOException e){
			fail();
		}catch(InvalidUsernameException|InvalidPasswordException e){
			fail();
		}
	}

	@Test
	public void testLogoutWithSuccess(){

		User u= new ConcreteUser("name", null, "password", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		assertTrue(ezShop.logout());
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
		
		try {
			//Create a product for the test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			
			//Test
			assertEquals(Integer.valueOf(-1), ezShop.createProductType("description", "1234567891231", 5.0, "note"));
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
		
	}
	
	@Test
	public void testCreateProductTypeValidProduct() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		

		assertEquals(Integer.valueOf(1), ezShop.createProductType("description", "884846564847", 5.0, "note"));

		//Reset
		try {
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
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
		
		//Reset
		try {
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
		//Test no product with given id

		assertFalse(ezShop.updateProduct(1, "description", "1234567891231", 5.0, "note"));
	}
	
	@Test
	public void testProductUpdateExistingBarCode() throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create products for the test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "785462151575", "note", null, 5.0, null));
			
			//Test
			assertFalse(ezShop.updateProduct(1, "description", "785462151575", 5.0, "note"));
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
	}
	
	@Test
	public void testProductUpdateValid() throws InvalidProductIdException, UnauthorizedException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create a product for the test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			
			//Test
			assertTrue(ezShop.updateProduct(1, "description", "785462151575", 5.0, "note"));
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
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
		
		try {
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
		assertFalse(ezShop.deleteProductType(1));
	}
	  
	@Test
	public void testDeleteProductValidProduct() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			
			//Test
			assertTrue(ezShop.deleteProductType(1));
		} catch (DAOException e) {
			fail("Unexpected exception: " + e );
		}
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
		//Create product for test	
		try {
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			products = ezShop.getAllProductTypes();
			assertEquals(1, products.size());
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
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
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			
			//Test
			assertEquals("1234567891231", ezShop.getProductTypeByBarCode("1234567891231").getBarCode());
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
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
		
		try {
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
		assertEquals(0, ezShop.getProductTypesByDescription("description").size());
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

	@Test
	public void testSaleTransactionInvalidProductCode(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(1, " ", 2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(1, null, 2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(1, "invalidCode", 2);});
		
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(1, " ", 2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(1, null, 2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(1, "invalidCode", 2);});

		assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(1, " ", 0.2);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(1, null, 0.2);});
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
	public void testGetProductTypeByDescriptionValid() throws UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			
			//Test part of description
			assertEquals(1, ezShop.getProductTypesByDescription("des").size());
			
			//Test empty description
			assertEquals(1, ezShop.getProductTypesByDescription("").size());
			
			//Test null description
			assertEquals(1, ezShop.getProductTypesByDescription(null).size());
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception " + e);
		}
		
	}

	@Test
	public void testSaleTransactionProductNotExists(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		try{
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);
			
			assertFalse(ezShop.addProductToSale(saleTransaction.getTicketNumber(), "123456789104", 1));
			assertFalse(ezShop.deleteProductFromSale(saleTransaction.getTicketNumber(), "123456789104", 1));
			assertFalse(ezShop.applyDiscountRateToProduct(saleTransaction.getTicketNumber(), "123456789104", 0.2));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException|InvalidProductCodeException|
				InvalidQuantityException| InvalidDiscountRateException e){
			System.out.println("Error message: " + e);
			fail();
		}
	}


	//TODO: add the deleteProductFromSale here
	@Test
	public void testAddProductToSaleNotEnoughProduct(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		ProductType pt= new ConcreteProductType(null, "product_test", "123456789104", null, 5, 2.0, null);
		try{
			ezShop.getDAO().createProductType(pt);
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);

			assertFalse(ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 6));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(InvalidTransactionIdException|InvalidProductCodeException|InvalidQuantityException|UnauthorizedException e){
			fail();
		}
	}

	@Test
	public void testSaleTransactionNotExist(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		SaleTransaction wrongTransaction= new ConcreteSaleTransaction(1, new ArrayList<TicketEntry>(), 0, 0);
		ezShop.setSaleTransaction(wrongTransaction);

		try{
			assertFalse(ezShop.addProductToSale(wrongTransaction.getTicketNumber(), "123456789104", 6));
			assertFalse(ezShop.deleteProductFromSale(wrongTransaction.getTicketNumber(), "123456789104", 6));
			assertFalse(ezShop.applyDiscountRateToProduct(wrongTransaction.getTicketNumber(), "123456789104", 0.25));
			//assertFalse(ezShop.applyDiscountRateToSale(wrongTransaction.getTicketNumber(), 0.25));
			assertEquals(-1, ezShop.computePointsForSale(wrongTransaction.getTicketNumber()));
			//assertFalse(ezShop.endSaleTransaction(wrongTransaction.getTicketNumber()));
			assertFalse(ezShop.deleteSaleTransaction(wrongTransaction.getTicketNumber()));

		}catch(InvalidTransactionIdException|InvalidProductCodeException|InvalidQuantityException|
				UnauthorizedException|InvalidDiscountRateException e){
			fail();
		}
	}

	@Test
	public void testStartSaleTransactionWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		try{
			Integer idNewTransaction= ezShop.startSaleTransaction();
			if(idNewTransaction<=0){
				fail();
			}
			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException e){
			fail();
		}
	}

	@Test
	public void testAddProductToSaleWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		ProductType pt= new ConcreteProductType(1, "product1", "123456789104", null, 5, 1.0, null);
		try{
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 50);
			
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}

			SaleTransaction testSt= ezShop.getDAO().searchSaleTransaction(saleTransaction.getTicketNumber());
			if(testSt==null){
				System.out.println("La transaction in addproductto sale è ancora null!!!");
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);

			assertTrue(ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 1));
			assertTrue(ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 1));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException|InvalidProductCodeException|InvalidQuantityException e){
			System.out.println("addProductToSale"+e);
			fail();
		}
	}

	@Test
	public void testDeleteProductFromSaleWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		ProductType pt= new ConcreteProductType(1, "product1", "123456789104", null, 5, 1.0, null);
		try{
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 50);
			
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}

			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);

			ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 1);
			ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 1);

			assertTrue(ezShop.deleteProductFromSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 1));
			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException|InvalidProductCodeException|InvalidQuantityException e){
			fail();
		}
	}

	@Test
	public void testEndSaleTransactionWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		try{
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);

			assertTrue(ezShop.endSaleTransaction(saleTransaction.getTicketNumber()));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException e){
			System.out.println("endSaleTransaction"+e);
			fail();
		}
	}

	@Test
	public void testApplyDiscountRateToProductWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		ProductType pt= new ConcreteProductType(1, "product1", "123456789104", null, 5, 1.0, null);
		try{
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 50);
			
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);

			ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 2);
			assertTrue(ezShop.applyDiscountRateToProduct(saleTransaction.getTicketNumber(), pt.getBarCode(), 0.3));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException|InvalidProductCodeException|
				InvalidQuantityException|InvalidDiscountRateException e){
			fail();
		}
	}

	@Test
	public void testApplyDiscountRateToSaleWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		ProductType pt= new ConcreteProductType(1, "product1", "123456789104", null, 5, 1.0, null);
		try{
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 50);
			
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.OPENED);

			ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 2);
			assertTrue(ezShop.applyDiscountRateToSale(saleTransaction.getTicketNumber(), 0.3));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException|InvalidProductCodeException|
				InvalidQuantityException|InvalidDiscountRateException e){
			fail();
		}
	}

	@Test
	public void testdeleteSaleTransactionWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);

		ProductType pt= new ConcreteProductType(1, "product1", "123456789104", null, 5, 1.0, null);
		try{
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 50);
			
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction saleTransaction = new ConcreteSaleTransaction(stId + 1, new ArrayList<TicketEntry>(), 0, 0);
			boolean inserted= ezShop.getDAO().storeSaleTransaction(saleTransaction);
			if(inserted==false){
				fail();
			}
			ezShop.setSaleTransaction(saleTransaction);
			ezShop.setSaleTransactionState(Constants.CLOSED);

			ezShop.addProductToSale(saleTransaction.getTicketNumber(), pt.getBarCode(), 2);
			assertTrue(ezShop.deleteSaleTransaction(saleTransaction.getTicketNumber()));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException|InvalidQuantityException| InvalidProductCodeException e){
			fail();
		}
	}

	@Test
	public void testUpdateQuantityUnauthorizedUser() {
		//Test no user
		ezShop.setRunningUser(null);
			assertThrows(UnauthorizedException.class, () -> {
			ezShop.updateQuantity(1, 50);
		});
									
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
								
		assertThrows(UnauthorizedException.class, () -> {
				ezShop.updateQuantity(1, 50);
		});
				
		//Test user= CASHIER
		user.setRole(Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {	
			ezShop.updateQuantity(1, 50);
		});
	}

	@Test
	public void testUpdateQuantityInvalidProductId() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test null id
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updateQuantity(null, 50);
		});
		
		//Test id negative
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updateQuantity(-1, 50);
		});
		
		//Test id=0
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updateQuantity(0, 50);
		});
	}
	
	@Test
	public void testUpdateQuantityProductLocationNull() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			//Test
			assertFalse(ezShop.updateQuantity(1, 50));
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
	}
	
	@Test
	public void testUpdateQuantityProductLocationEmpty() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			ezShop.getDAO().updatePosition(1, "");
			//Test
			assertFalse(ezShop.updateQuantity(1, 50));
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
	}
	
	@Test
	public void testUpdateQuantityProductInvalidQuantity() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 1);
			
			//Test
			assertFalse(ezShop.updateQuantity(1, -2));
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
	}

	@Test
	public void testUpdateQuantityProductNotExists() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Negative quantity
		assertFalse(ezShop.updateQuantity(1, -2));
		
		//Positive quantity
		assertFalse(ezShop.updateQuantity(1, 2));
		
			
	}

	@Test
	public void testUpdateQuantityProductValidPositive() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 0);
			
			//Test
			assertTrue(ezShop.updateQuantity(1, 20));
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdateQuantityProductValidNegative() throws InvalidProductIdException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			//Create product for test
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			ezShop.getDAO().updatePosition(1, "1-A-23");
			ezShop.getDAO().updateQuantity(1, 50);
			
			//Test
			assertTrue(ezShop.updateQuantity(1, -20));
			
			//Reset
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdatePositionUnauthorizedException() {
		//Test no user
		ezShop.setRunningUser(null);
			assertThrows(UnauthorizedException.class, () -> {
			ezShop.updatePosition(1, "1-A-23");
		});
											
		//Test generic user role
		User user = new ConcreteUser("name", 1, "123", "role");
		ezShop.setRunningUser(user);
										
		assertThrows(UnauthorizedException.class, () -> {
			ezShop.updatePosition(1, "1-A-23");
		});
						
		//Test user= CASHIER
		user.setRole(Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {	
			ezShop.updatePosition(1, "1-A-23");
		});
	}

	@Test
	public void testUpdatePositionInvalidLocation() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		assertThrows(InvalidLocationException.class, () -> {
			ezShop.updatePosition(1, "1/2/3");
		});
		
		assertThrows(InvalidLocationException.class, () -> {
			ezShop.updatePosition(1, "A-2-A");
		});
	}
	
	@Test
	public void testUpdatePositionInvalidId() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		//Test null id
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updatePosition(null, "1-A-23");
		});
		
		//Test id negative
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updatePosition(-1, "1-A-23");
		});
		
		//Test id=0
		assertThrows(InvalidProductIdException.class, () -> {
			ezShop.updatePosition(0, "1-A-23");
		});
	}

	@Test
	public void testUpdatePositionProductNotExist() throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
		assertFalse(ezShop.updatePosition(1, "1-A-23"));
	}
	
	@Test
	public void testSaleTransactionWrongTransactionStatus(){

		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		try{
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction wrongTransaction= new ConcreteSaleTransaction(stId+1, new ArrayList<TicketEntry>(), 0, 0);
			ezShop.setSaleTransaction(wrongTransaction);
			ezShop.setSaleTransactionState(Constants.CLOSED);

			assertFalse(ezShop.addProductToSale(wrongTransaction.getTicketNumber(), "123456789104", 2));
			assertFalse(ezShop.deleteProductFromSale(wrongTransaction.getTicketNumber(), "123456789104", 2));
			assertFalse(ezShop.applyDiscountRateToProduct(wrongTransaction.getTicketNumber(), "123456789104", 0.25));
			assertFalse(ezShop.endSaleTransaction(wrongTransaction.getTicketNumber()));

			//deleteSaleTransaction test
			wrongTransaction.setPayed(true);
			boolean saleUpdate= ezShop.getDAO().storeSaleTransaction(wrongTransaction);

			if(saleUpdate==false){
				fail();
			}
			assertFalse(ezShop.deleteSaleTransaction(wrongTransaction.getTicketNumber()));	

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(InvalidTransactionIdException|InvalidProductCodeException|InvalidQuantityException|
				UnauthorizedException|InvalidDiscountRateException e){
			fail();
		}

	}

	
	@Test
	public void testGetSaleTransaction(){
		
		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);

		try{
			Integer  stId= ezShop.getDAO().insertSaleTransaction();
			if(stId<0){
				fail();
			}
			SaleTransaction wrongTransaction= new ConcreteSaleTransaction(stId+1, new ArrayList<TicketEntry>(), 0, 0);
			assertEquals(null,ezShop.getSaleTransaction(wrongTransaction.getTicketNumber()));

			ezShop.getDAO().resetApplication();

		}catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidTransactionIdException e){
			fail();
		}
	}

	
	@Test
	public void testUpdatePositionAlreadyAssigned() throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "22612514516513", "note", null, 5.0, null));
			ezShop.getDAO().updatePosition(1, "1-A-23");
			
			assertFalse(ezShop.updatePosition(2, "1-A-23"));
			
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
	}
	
	@Test
	public void testUpdatePositionValid() throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		
		try {
			ezShop.getDAO().createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
			
			assertTrue(ezShop.updatePosition(1, "1-A-23"));
			
			ezShop.getDAO().resetApplication();
		} catch (DAOException e) {
			fail("Unexpected Exception" + e);
		}
	}
	
//	@Test
//	public void testAddProductNotEnoughProduct(){
//
//		User u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
//		ezShop.setRunningUser(u);
//		IDAOEZshop dao= new DAOEZShop();
//		ezShop.setDAO(dao);
//
//		try{
//
//		}catch(DAOException e){
//
//		}
//
//	}
	
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
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.startReturnTransaction(-1);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.startReturnTransaction(0);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.startReturnTransaction(null);});
	}
	
	@Test
	public void testStartReturnTransactionClosedSale () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertEquals(Integer.valueOf(1), ezShop.startReturnTransaction(2));
 			assertEquals(Integer.valueOf(1), ezShop.getReturnTransaction().getReturnId());
		} catch (Exception e) {
			fail();
		}
		assertEquals(Integer.valueOf(1), ezShop.getReturnTransaction().getReturnId());
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStartReturnTransactionNotClosedSale () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertEquals(Integer.valueOf(-1),ezShop.startReturnTransaction(1));
		} catch (InvalidTransactionIdException  e) {
			fail();
		} catch (UnauthorizedException e) {
			fail();
		}
		assertEquals(null, ezShop.getReturnTransaction());
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStartReturnTransactionNullRole () {
		//User user = new ConcreteUser("name", 1, "123", null);  Do I have also check this case? this case fail.
		User user = null;
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.startReturnTransaction(1);});
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
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
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
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t1);
		tickets.add(t2);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			assertFalse(ezShop.returnProduct(1, "4314324224131", 1));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t1);
		tickets.add(t2);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			assertFalse(ezShop.returnProduct(3, "4314324224124", 22));
		} catch (Exception e) {
			fail();
		}
		assertTrue(r.getEntries().isEmpty());
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReturnProductCodeInvalid() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(InvalidProductCodeException.class, () -> {ezShop.returnProduct(2, "", 51);});
		assertThrows(InvalidProductCodeException.class, () -> {ezShop.returnProduct(2, null, 51);});
		assertThrows(InvalidProductCodeException.class, () -> {ezShop.returnProduct(2, "21321", 51);});

	}
	
	@Test
	public void testReturnProductNullUser() {
		User user = null;
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.returnProduct(2, "123456789104", 10);;});
	}
	
	@Test
	public void testReturnProductNegativeQuanitity() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(InvalidQuantityException.class, () -> {ezShop.returnProduct(2, "123456789104", 0);});
		assertThrows(InvalidQuantityException.class, () -> {ezShop.returnProduct(2, "123456789104", -1);});
		assertThrows(InvalidQuantityException.class, () -> {ezShop.returnProduct(2, "123456789104", 0);});
	}
	
	@Test
	public void testReturnProductTransactionIdError () {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnProduct(-1, "123456789104", 51);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnProduct(0, "123456789104", -1);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnProduct(null, "123456789104", 0);});
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
		
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t1);
		tickets.add(t2);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			assertTrue(ezShop.returnProduct(1, "123456789104", 1));
			assertEquals(Integer.valueOf(50), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
			assertEquals(1, r.getEntries().size());
			assertTrue(ezShop.returnProduct(1, "123456789104", 19));
			assertEquals(1, r.getEntries().size());
			assertEquals(Integer.valueOf(50), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
			assertTrue(ezShop.returnProduct(1, "4314324224124", 1));
			assertEquals(2, r.getEntries().size());
			assertEquals(Integer.valueOf(150), ezShop.getProductTypeByBarCode("4314324224124").getQuantity());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	@Test
	public void testEndReturnTransactionInvalidTransactionId(){
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(-1);
		r.setTransactionId(1);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.endReturnTransaction(0, true);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.endReturnTransaction(-2, true);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.endReturnTransaction(0, true);});
	}
	
	@Test
	public void testEndReturnTransactionUserNull(){
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		//ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(-1);
		r.setTransactionId(1);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		assertThrows(UnauthorizedException.class, () -> {ezShop.endReturnTransaction(1, true);});
	}
	
	@Test
	public void testEndReturnTransactionInactiveReturnTransaction(){
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(5);
		r.setTransactionId(1);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.endReturnTransaction(4, true));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testEndReturnTransactionCommitReturn(){
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(1);
		r.setTransactionId(2);
		List<TicketEntry> entries = new ArrayList<>();
		TicketEntry t1 = new ConcreteTicketEntry("123456789104", "", 21, 0.5,0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124", "", 1, 32,0.0);
		entries.add(t1);
		entries.add(t2);
		r.setEntries(entries);
		ezShop.setReturnTransaction(r);

		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t3);
		tickets.add(t4);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			assertTrue(ezShop.endReturnTransaction(1, true));
			assertEquals(Integer.valueOf(71), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
			assertEquals(Integer.valueOf(151), ezShop.getProductTypeByBarCode("4314324224124").getQuantity());
			List<TicketEntry> l = dao.getEntries(2);
			assertEquals(4, l.get(0).getAmount());
			assertEquals(0, l.get(1).getAmount());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEndReturnTransactionCommitReturnFalse(){
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(1);
		r.setTransactionId(2);
		List<TicketEntry> entries = new ArrayList<>();
		TicketEntry t1 = new ConcreteTicketEntry("123456789104", "", 21, 0.5,0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124", "", 1, 32,0.0);
		entries.add(t1);
		entries.add(t2);
		r.setEntries(entries);
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t3);
		tickets.add(t4);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		
		try {
			assertTrue(ezShop.endReturnTransaction(1, false));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {			
			assertEquals(Integer.valueOf(50), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
			assertEquals(Integer.valueOf(150), ezShop.getProductTypeByBarCode("4314324224124").getQuantity());
			IDAOEZshop dao = new DAOEZShop();
			List<TicketEntry> l = dao.getEntries(2);
			assertEquals(25, l.get(0).getAmount());
			assertEquals(1, l.get(1).getAmount());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteReturnTransactionInvalidTransaction() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(-1);
		r.setTransactionId(1);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.deleteReturnTransaction(0);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.deleteReturnTransaction(-2);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.deleteReturnTransaction(null);});
	}
	
	@Test
	public void testDeleteReturnTransactionInvalidUser(){
		User user = null;
		//ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(1);
		r.setTransactionId(1);
		r.setEntries(new ArrayList<>());
		ezShop.setReturnTransaction(r);
		assertThrows(UnauthorizedException.class, () -> {ezShop.deleteReturnTransaction(1);});
		user = new ConcreteUser("name", 1, "123", "role");
		assertThrows(UnauthorizedException.class, () -> {ezShop.deleteReturnTransaction(1);});
	}
	
	
	@Test
	public void testDeleteReturnTransactionInvalid(){
		ezShop.reset();
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.deleteReturnTransaction(10));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		
	}
	
	@Test
	public void testDeleteReturnTransactionValid(){
		ezShop.reset();
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);;
		ezShop.setRunningUser(user);
		ezShop.reset();
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setReturnId(1);
		r.setTransactionId(1);
		r.setEntries(tickets);
		r.setTransactionId(1);  
		r.setPrice(5.21);
		ezShop.setReturnTransaction(r);
		
		/*
		ReturnTransaction r2 = new ConcreteReturnTransaction();
		r2.setReturnId(2);
		r2.setDiscountRate(0);
		r2.setPrice(10.4);
		r2.setPayed(true);
		r2.setEntries(tickets);
		r2.setTransactionId(1);*/
		SaleTransaction s1 = new ConcreteSaleTransaction(1, tickets, 0 , 32.5);
		//SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(true);
		//s2.setPayed(true);

		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeReturnTransaction(r);
			//dao.storeReturnTransaction(r2);
			dao.storeSaleTransaction(s1);
			//dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			System.out.println("ciao ciao " + dao.searchReturnTransaction(1).getTransactionId());
		} catch (DAOException e) {
			System.out.println(e);  
		}
	
		try {
			assertTrue(ezShop.deleteReturnTransaction(1));
			//assertEquals(Integer.valueOf(50), ezShop.getProductTypeByBarCode("123456789104").getQuantity());
			//assertEquals(Integer.valueOf(150), ezShop.getProductTypeByBarCode("4314324224124").getQuantity());
			IDAOEZshop dao = new DAOEZShop();
			List<TicketEntry> l = dao.getEntries(2);
			//assertEquals(25, l.get(0).getAmount());
			//assertEquals(1, l.get(1).getAmount());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}	
		//dao.resetApplication();
	}
	
	@Test
	public void testReturnCashInvalidReutrnId() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCashPayment(-1);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCashPayment(0);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCashPayment(null);});
	}
	
	@Test
	public void testReturnCashPaymentInvalidUser() {
		User user = null;
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(UnauthorizedException.class, () -> {ezShop.returnCashPayment(-1);});
	}
	
	@Test
	public void testReturnCashPaymentReturnTransactionInexistent() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1 == ezShop.returnCashPayment(10));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReturnCashPaymentReturnTransactionNotEnded() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1 == ezShop.returnCashPayment(44));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testReturnCashPaymentReturnTransactionEndedAndNotPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 44.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t3);
		tickets.add(t4);
		
		ReturnTransaction r1 = new ConcreteReturnTransaction();
		r1.setReturnId(1);
		r1.setDiscountRate(0);
		r1.setPrice(5.21);
		r1.setPayed(false);
		r1.setEntries(new ArrayList());
		ReturnTransaction r2 = new ConcreteReturnTransaction();
		r2.setReturnId(2);
		r2.setDiscountRate(0);
		r2.setPrice(10.4);
		r2.setPayed(true);
		TicketEntry t = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		List<TicketEntry> returntickets = new ArrayList<>();
		returntickets.add(t);
		r2.setEntries(returntickets);
		
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
			dao.storeReturnTransaction(r1);
			dao.storeReturnTransaction(r2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertTrue(5.21 == ezShop.returnCashPayment(1));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println(e);
		}
	}

	
	@Test
	public void testReturnCashPaymentReturnTransactionAlreadyPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t3);
		tickets.add(t4);
		ReturnTransaction r1 = new ConcreteReturnTransaction();
		r1.setDiscountRate(0);
		r1.setEntries(tickets);
		r1.setPayed(true);
		r1.setPrice(13.2);
		r1.setReturnId(2);
		r1.setTransactionId(2);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
			dao.storeReturnTransaction(r1);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertTrue(-1 == ezShop.returnCashPayment(2));
		
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testReturnCreditCardPaymentInvalidReuturnId() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCreditCardPayment(-1, "123456789104" );});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCreditCardPayment(0, "123456789104" );});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCreditCardPayment(null, "123456789104" );});
	}
	
	
	@Test
	public void testReturnCreditCardPaymentInvalidUser() {
		User user = null;
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(UnauthorizedException.class, () -> {ezShop.returnCreditCardPayment(1, "123456789104");});
	}
	
	
	@Test
	public void testReturnCreditCardPaymentInvalidCard() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidCreditCardException.class, () -> {ezShop.returnCreditCardPayment(1, "1234131"); });
		assertThrows(InvalidCreditCardException.class, () -> {ezShop.returnCreditCardPayment(1, ""); });
		assertThrows(InvalidCreditCardException.class, () -> {ezShop.returnCreditCardPayment(1, null); });
	}
	
	@Test
	public void testReturnCreditCardPaymentReturnTransactionInexistent() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1.0 == ezShop.returnCreditCardPayment(44, "4716258050958645"));	
		} catch (Exception e) {
			fail();
		}
	}
	
	
	@Test
	public void testReturnCreditCardPaymentReturnTransactionNotEnded() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1 == ezShop.returnCreditCardPayment(44, "4716258050958645"));
		} catch (Exception e) {
			fail();
		}
	}

	
	@Test
	public void testReturnCreditCardPaymentReturnTransactionEndedAndNotPayed() {
		
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 44.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		tickets.add(t3);
		tickets.add(t4);
		
		ReturnTransaction r1 = new ConcreteReturnTransaction();
		r1.setReturnId(1);
		r1.setDiscountRate(0);
		r1.setPrice(5.21);
		r1.setPayed(false);
		r1.setEntries(new ArrayList());
		ReturnTransaction r2 = new ConcreteReturnTransaction();
		r2.setReturnId(2);
		r2.setDiscountRate(0);
		r2.setPrice(10.4);
		r2.setPayed(true);
		TicketEntry t = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		List<TicketEntry> returntickets = new ArrayList<>();
		returntickets.add(t);
		r2.setEntries(returntickets);
		
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
			dao.storeReturnTransaction(r1);
			dao.storeReturnTransaction(r2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			//System.out.println(ezShop.returnCreditCardPayment(1, "4485370086510891"));
			assertTrue(5.21 == ezShop.returnCreditCardPayment(1, "4485370086510891"));	
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		o.updateCreditCardAmount("4485370086510891", 5.21, true);
	}
	
	@Test
	public void testReturnCreditCardPaymentReturnCardNotRegistered() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1 == ezShop.returnCreditCardPayment(1, "1002939910217"));
		} catch (Exception e) {
			fail();
		}
	}
	
	
	@Test
	public void testReturnCreditCardPaymentReturnTransactionAlreadyPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		ReturnTransaction r2 = new ConcreteReturnTransaction();
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		SaleTransaction s1 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		tickets.add(t1);
		r2.setReturnId(2);
		r2.setDiscountRate(0);
		r2.setPrice(10.4);
		r2.setPayed(true);
		r2.setTransactionId(2);
		TicketEntry t2 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		List<TicketEntry> returnTickets = new ArrayList<>();
		returnTickets.add(t2);
		r2.setEntries(returnTickets);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeReturnTransaction(r2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		
		try {
			assertTrue(-1 == ezShop.returnCreditCardPayment(2, "4485370086510891"));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRecordBalanceUpdateCashierUser() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.recordBalanceUpdate(500);});
	}
	
	@Test
	public void testRecordBalanceUpdateNullUser() {
		User user = null;
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.recordBalanceUpdate(500);});
	}
	
	
	@Test
	public void testRecordBalanceUpdateAddCredit() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		try {
			assertTrue(ezShop.recordBalanceUpdate(500));
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testRecordBalanceUpdateAddDebit() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			ezShop.recordBalanceUpdate(500);
			assertTrue(ezShop.recordBalanceUpdate(-400));
		} catch (UnauthorizedException e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRecordBalanceUpdateNegativeTotal() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			ezShop.recordBalanceUpdate(400);
			assertFalse(ezShop.recordBalanceUpdate(-450));
		} catch (UnauthorizedException e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetCreditsAndDebitInvalidUser() {
		User user = null;
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.getCreditsAndDebits(LocalDate.of(2021, 10, 1), LocalDate.of(2021, 10, 29));});
	}
	
	@Test
	public void testGetCreditsAndDebitNullList() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertTrue(ezShop.getCreditsAndDebits(LocalDate.of(2021, 10, 1), LocalDate.of(2021, 10, 29)).isEmpty());
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testzGetCreditsAndDebitCorrectValue() {
		User user = new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		ezShop.reset();
		try {
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 2, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 23));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 31));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 5, 30));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 7, 1));
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			System.out.println(LocalDate.now());
			for (BalanceOperation b : ezShop.getCreditsAndDebits(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 6, 29))) {
				System.out.println(b.getDate() + " - " + b.getBalanceId());
			}
			assertEquals(4, ezShop.getCreditsAndDebits(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 6, 29)).size());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println();
		}
	}
	
	@Test
	public void testGetCreditsAndDebitWithDateExchanged() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		
		try {
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 2, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 23));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 31));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 5, 30));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 7, 1));
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		
		try {
			assertEquals(4, ezShop.getCreditsAndDebits(LocalDate.of(2021, 6, 29), LocalDate.of(2021, 2, 1)).size());
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println();
		}
	}
	
	@Test
	public void testGetCreditsAndDebitFromDateNull() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		
		try {
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 2, 25));
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 5, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 2, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 23));
			dao.insertBalanceOperation(50, "DEBIT", LocalDate.of(2021, 3, 31));
			dao.insertBalanceOperation(50, "DEBIT", LocalDate.of(2021, 5, 30));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 7, 1));
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			assertEquals(6, ezShop.getCreditsAndDebits(null, LocalDate.of(2021, 6, 25)).size());
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println();
		}
	}
	
	@Test
	public void testGetCreditsAndDebitFromToNull() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		try {
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 2, 25));
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 5, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 2, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 23));
			dao.insertBalanceOperation(50, "DEBIT", LocalDate.of(2021, 3, 31));
			dao.insertBalanceOperation(50, "DEBIT", LocalDate.of(2021, 5, 30));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 7, 1));
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertEquals(3, ezShop.getCreditsAndDebits(LocalDate.of(2021, 3, 25), null).size());
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println();
		}
	}
	
	@Test
	public void testGetCreditsAndDebitFromAndToNull() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		try {
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 2, 25));
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 5, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 2, 25));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 3, 23));
			dao.insertBalanceOperation(50, "DEBIT", LocalDate.of(2021, 3, 31));
			dao.insertBalanceOperation(50, "DEBIT", LocalDate.of(2021, 5, 30));
			dao.insertBalanceOperation(50, "CREIT", LocalDate.of(2021, 7, 1));
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			assertEquals(7, ezShop.getCreditsAndDebits(null, null).size());
		} catch (UnauthorizedException e) {
			System.out.println(e);
			fail();
		}
		
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println();
		}
	}
	
	@Test
	public void testComputeBalanceUserNull() {
		User user = null;
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.computeBalance();});
	}
	
	@Test
	public void testComputeBalance() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		try {
			dao.insertBalanceOperation(1000, "CREDIT", LocalDate.of(2012, 2, 25));
			dao.insertBalanceOperation(100, "CREDIT", LocalDate.of(2012, 5, 25));
			dao.insertBalanceOperation(50.5, "CREDIT", LocalDate.of(2021, 2, 25));
			dao.insertBalanceOperation(50, "CREDIT", LocalDate.of(2021, 3, 23));
			dao.insertBalanceOperation(50, "CREDIT", LocalDate.of(2021, 7, 1));
			dao.insertBalanceOperation(900.50, "DEBIT", LocalDate.of(2021, 5, 30));
			dao.insertBalanceOperation(100.99, "DEBIT", LocalDate.of(2021, 3, 31));
		} catch (DAOException e) {
			System.out.println(e);
		}
		try { 
			assertTrue(249.01 == ezShop.computeBalance());
		} catch (UnauthorizedException e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testOrderUnauthorizedUser() {
		User u= null;
		ezShop.setRunningUser(u);
		
		assertThrows(UnauthorizedException.class, ()->{ezShop.issueOrder("4314324224124",2,2.0);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.payOrderFor("4314324224124",2,2.0);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.payOrder(3);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.recordOrderArrival(3);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.recordOrderArrivalRFID(3, "000000001000");});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getAllOrders();});
		
		
		u= new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(u);
		assertThrows(UnauthorizedException.class, ()->{ezShop.issueOrder("4314324224124",2,2.0);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.payOrderFor("4314324224124",2,2.0);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.payOrder(3);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.recordOrderArrival(3);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.recordOrderArrivalRFID(3, "000000001000");});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getAllOrders();});
	}
	
	@Test 
	public void testOrderInvalidProductCode() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);  
		ezShop.setRunningUser(u);

		assertThrows(InvalidProductCodeException.class, ()->{ezShop.issueOrder("", 2, 2.0);});
		//assertThrows(InvalidProductCodeException.class, ()->{ezShop.issueOrder(null, 2, 2.0);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.issueOrder("invalidCode", 2, 2.0);});
		
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.payOrderFor("", 2, 2.0);});
		//assertThrows(InvalidProductCodeException.class, ()->{ezShop.payOrderFor(null, 2, 2.0);});
		assertThrows(InvalidProductCodeException.class, ()->{ezShop.payOrderFor("invalidCode", 2, 2.0);});
	}
	
	@Test
	public void testOrderInvalidQUantity() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		assertThrows(InvalidQuantityException.class, ()->{ezShop.issueOrder("4314324224124", -2, 2.0);});
		assertThrows(InvalidQuantityException.class, ()->{ezShop.issueOrder("4314324224124", 0, 2.0);});
		
		assertThrows(InvalidQuantityException.class, ()->{ezShop.payOrderFor("4314324224124", -2, 2.0);});
		assertThrows(InvalidQuantityException.class, ()->{ezShop.payOrderFor("4314324224124", 0, 2.0);});
	}
	
	@Test 
	public void testOrderInvalidPricePerQty() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		assertThrows(InvalidPricePerUnitException.class, ()->{ezShop.issueOrder("4314324224124", 2, -2.0);});
		assertThrows(InvalidPricePerUnitException.class, ()->{ezShop.issueOrder("4314324224124", 2, 0);});
		
		assertThrows(InvalidPricePerUnitException.class, ()->{ezShop.payOrderFor("4314324224124", 2, -2.0);});
		assertThrows(InvalidPricePerUnitException.class, ()->{ezShop.payOrderFor("4314324224124", 2, 0);});
	}
	
	@Test 
	public void testOrderInvalidOrderId() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		assertThrows(InvalidOrderIdException.class, ()->{ezShop.payOrder(-1);});
		assertThrows(InvalidOrderIdException.class, ()->{ezShop.payOrder(0);});
		//assertThrows(InvalidOrderIdException.class, ()->{ezShop.payOrder(null);});
		
		assertThrows(InvalidOrderIdException.class, ()->{ezShop.recordOrderArrival(-1);});
		assertThrows(InvalidOrderIdException.class, ()->{ezShop.recordOrderArrival(0);});
		//assertThrows(InvalidOrderIdException.class, ()->{ezShop.recordOrderArrival(null);});	

		assertThrows(InvalidOrderIdException.class, ()->{ezShop.recordOrderArrivalRFID(-1, "000000001000");});
		assertThrows(InvalidOrderIdException.class, ()->{ezShop.recordOrderArrivalRFID(0, "000000001000");});
	}
	
	@Test 
	public void testRecordOrderArrivalInvalidLocation() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		//devo avere ordine riferito a prodotto senza location nel db -> inserisco sia prodotto che ordine
		try{
			ezShop.getDAO().createProductType(pt);
			Integer ordId = ezShop.getDAO().payOrderDirectly("4314324224124", 1, 1.0);
			if(ordId<0){
				fail();
			}
			
			assertThrows(InvalidLocationException.class, () ->{ezShop.recordOrderArrival(ordId);});
			assertThrows(InvalidLocationException.class, () ->{ezShop.recordOrderArrivalRFID(ordId, "000000001000");});


		}catch(DAOException e){
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}

	}
	

	@Test
    public void testRecordOrderArrivalRFIDInvalidRFID(){

		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);  
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().createProductType(pt);
			Integer ordId = ezShop.getDAO().payOrderDirectly("4314324224124", 1, 1.0);
			if(ordId<0){
				fail();
			}
			ezShop.getDAO().updatePosition(1, "4-A-4");

			//Case: Invalid format
			assertThrows(InvalidRFIDException.class, () ->{ezShop.recordOrderArrivalRFID(ordId, "64701000");});
			assertThrows(InvalidRFIDException.class,() ->{ezShop.recordOrderArrivalRFID(ordId, "dgd9876543gd");});

			//Case: RFID not unique
			
			Product p= new ConcreteProduct();
			p.setBarCode("4314324224124");
			p.setRFID("010006001000");
			p.setTransactionId(1);
			ezShop.getDAO().storeProduct(p);

			assertThrows(InvalidRFIDException.class,() ->{ezShop.recordOrderArrivalRFID(ordId, p.getRFID());});
			

		}catch(DAOException e){
			System.out.println("Exception:  " + e);
			fail();
		}

		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
        
    }


	@Test
    public void testRecordOrderArrivalRFIDWithSuccess(){

		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);  
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 3, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt);		
			ezShop.getDAO().updatePosition(1, "4-A-4");

			Integer ordId = ezShop.getDAO().payOrderDirectly("4314324224124", 3, 1.0);
			ezShop.recordOrderArrivalRFID(ordId, "000000001000");
			
		}catch(DAOException e){
			System.out.println("Exception:  " + e);
			fail();
		}catch(UnauthorizedException|InvalidLocationException| InvalidOrderIdException| InvalidRFIDException e){
			System.out.println("Exception:  " + e);
			fail();
		}

		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
        

	@Test 
	public void testOrderProductNotExists() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());							//otherwise the test fails for the balance and not for the product
			assertEquals(Integer.valueOf(-1), ezShop.issueOrder("4314324224124", 1, 1.0));			//this product should not exist since I've not inserted it
			assertEquals(Integer.valueOf(-1), ezShop.payOrderFor("4314324224124", 1, 1.0));
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidProductCodeException|InvalidQuantityException|InvalidPricePerUnitException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testIssueOrderValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u); 
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().createProductType(pt);
			assertEquals(Integer.valueOf(1), ezShop.issueOrder("4314324224124", 100, 1.0));		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidProductCodeException|InvalidQuantityException|InvalidPricePerUnitException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	
	@Test 
	public void testPayOrderForBalanceNotEnough() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().createProductType(pt);
			assertEquals(Integer.valueOf(-1), ezShop.payOrderFor("4314324224124", 100, 1.0));			//the balance should be 0 with an empty db
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidProductCodeException|InvalidQuantityException|InvalidPricePerUnitException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testPayOrderForValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u); 
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt);
			assertEquals(Integer.valueOf(1), ezShop.payOrderFor("4314324224124", 100, 1.0));		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidProductCodeException|InvalidQuantityException|InvalidPricePerUnitException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		} 
	}
	
	@Test 
	public void testOrderOrderNotExists() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {	
			assertFalse(ezShop.payOrder(3));
			assertFalse(ezShop.recordOrderArrival(3));
		} catch(UnauthorizedException|InvalidOrderIdException|InvalidLocationException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	
	//ORDERED = PAYED! SO THERE IS RETURN FALSE ONLY IF THE ORDER IS ALREADY COMPLETED
	@Test 
	public void testPayOrderOrderNotIssuedOrOrdered() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt);										
			ezShop.getDAO().payOrderDirectly("4314324224124", 1, 1.0);
			ezShop.getDAO().recordArrival(1);
			assertFalse(ezShop.payOrder(1));		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidOrderIdException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	
	@Test 
	public void testPayOrderValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);  
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().insertNewOrder("4314324224124", 1, 1.0);
			assertTrue(ezShop.payOrder(1));		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidOrderIdException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	
	//ORDERED=PAYED SO THERE IS RETURN FALSE ONLY IF THE ORDER IS JUST ISSUED
	@Test 
	public void testRecordOrderArrivalOrderNotOrderedOrCompleted() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().insertNewOrder("4314324224124", 1, 1.0);
			assertFalse(ezShop.recordOrderArrival(1));		
			assertFalse(ezShop.recordOrderArrivalRFID(1, "000000001000"));		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidOrderIdException|InvalidLocationException|InvalidRFIDException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testRecordOrderArrivalValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);  
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt);
			ezShop.getDAO().updatePosition(1, "4-A-4");
			ezShop.getDAO().payOrderDirectly("4314324224124", 1, 1.0); 
			assertTrue(ezShop.recordOrderArrival(1));		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException|InvalidOrderIdException|InvalidLocationException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testGetAllOrdersValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);  
		
		ProductType pt = new ConcreteProductType(null, "prova", "4314324224124", "prova", 1, 1.0, null);
		
		try {	
			ezShop.getDAO().insertBalanceOperation(100, Constants.CREDIT, LocalDate.now());	
			ezShop.getDAO().createProductType(pt); 
			ezShop.getDAO().insertNewOrder("4314324224124", 1, 1.0); 
			ezShop.getDAO().insertNewOrder("4314324224124", 3, 1.0); 
			ezShop.getDAO().insertNewOrder("4314324224124", 4, 1.0); 
			assertEquals(3, ezShop.getAllOrders().size());		
		}catch(DAOException e){
			fail();
		} catch(UnauthorizedException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	
	//CUSTOMERS
	
	@Test 
	public void testCustomerUnauthorizedUser() {
		User u= null;
		ezShop.setRunningUser(u);
		
		assertThrows(UnauthorizedException.class, ()->{ezShop.defineCustomer("name");});
		assertThrows(UnauthorizedException.class, ()->{ezShop.modifyCustomer(1,"newName","0123456789");});
		assertThrows(UnauthorizedException.class, ()->{ezShop.deleteCustomer(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getCustomer(1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.getAllCustomers();});
		assertThrows(UnauthorizedException.class, ()->{ezShop.createCard();});
		assertThrows(UnauthorizedException.class, ()->{ezShop.attachCardToCustomer("0123456789",1);});
		assertThrows(UnauthorizedException.class, ()->{ezShop.modifyPointsOnCard("0123456789",10);});
	}
	
	@Test 
	public void testCustomerInvalidCustomerName() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);

		assertThrows(InvalidCustomerNameException.class, ()->{ezShop.defineCustomer("");});
		//assertThrows(InvalidCustomerNameException.class, ()->{ezShop.defineCustomer(null);});
		
		assertThrows(InvalidCustomerNameException.class, ()->{ezShop.modifyCustomer(1,"","0123456789");});
		//assertThrows(InvalidCustomerNameException.class, ()->{ezShop.modifyCustomer(1,null,"0123456789");});
	}
	
	@Test 
	public void testCustomerInvalidCustomerCard() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		//assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyCustomer(1,"name",null);});
		assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyCustomer(1,"name","corta");});
		
		assertThrows(InvalidCustomerCardException.class, ()->{ezShop.attachCardToCustomer("",1);});
		//assertThrows(InvalidCustomerCardException.class, ()->{ezShop.attachCardToCustomer(null,1);});
		assertThrows(InvalidCustomerCardException.class, ()->{ezShop.attachCardToCustomer("corta",1);});
		
		assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyPointsOnCard("",10);});
		//assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyPointsOnCard(null,10);});
		assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyPointsOnCard("corta",10);});	
	}
	
	@Test 
	public void testCustomerInvalidCustomerId() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.modifyCustomer(0,"name","0123456789");});
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.modifyCustomer(-1,"name","0123456789");});
		//assertThrows(InvalidCustomerIdException.class, ()->{ezShop.modifyCustomer(null,"name","0123456789");});
		
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.deleteCustomer(0);});
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.deleteCustomer(-1);});
		//assertThrows(InvalidCustomerIdException.class, ()->{ezShop.deleteCustomer(null);});
		
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.getCustomer(0);});
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.getCustomer(-1);});
		//assertThrows(InvalidCustomerIdException.class, ()->{ezShop.getCustomer(null);});
		
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.attachCardToCustomer("0123456789",0);});
		assertThrows(InvalidCustomerIdException.class, ()->{ezShop.attachCardToCustomer("0123456789",-1);});
		//assertThrows(InvalidCustomerIdException.class, ()->{ezShop.attachCardToCustomer("0123456789",null);});
	}
	
	@Test 
	public void testDefineCustomerNameAlreadyInUse() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().insertCustomer("name2");	
			assertEquals(Integer.valueOf(-1), ezShop.defineCustomer("name1"));			
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidCustomerNameException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
			ezShop.getDAO().deleteCustomer(2);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testCustomerCardAlreadyInUse() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().insertCustomer("name2");
			ezShop.getDAO().updateCustomer(1,"name1", "0123456789");	
			assertFalse(ezShop.modifyCustomer(2,"name2","0123456789"));		
			assertFalse(ezShop.attachCardToCustomer("0123456789", 2));
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidCustomerNameException|InvalidCustomerCardException|InvalidCustomerIdException e) {
			System.out.println("Error message: " + e);
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
			ezShop.getDAO().deleteCustomer(2);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testCustomerCustomerNotExists() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			assertFalse(ezShop.deleteCustomer(2));		
			assertNull(ezShop.getCustomer(2));
			assertFalse(ezShop.attachCardToCustomer("0123456789", 2));
		} catch(UnauthorizedException|InvalidCustomerCardException|InvalidCustomerIdException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testModifyPointsOnCardInexistentCard() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {			
			assertFalse(ezShop.modifyPointsOnCard("0123456789", 10));
		} catch(UnauthorizedException|InvalidCustomerCardException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testModifyPointsOnCardNotEnoughPoints() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().updateCustomer(1,"name1", "0123456789");		
			assertFalse(ezShop.modifyPointsOnCard("0123456789", -10));
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidCustomerCardException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testDefineCustomerValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {			
			assertEquals(Integer.valueOf(1), ezShop.defineCustomer("name1"));			
		} catch(UnauthorizedException|InvalidCustomerNameException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testModifyCustomerValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			assertTrue(ezShop.modifyCustomer(1,"name1","0123456789"));		
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidCustomerNameException|InvalidCustomerCardException|InvalidCustomerIdException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testDeleteCustomerValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			assertTrue(ezShop.deleteCustomer(1));		
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException| InvalidCustomerIdException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testGetCustomerValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		Customer c = new ConcreteCustomer(1, "name1", "0123456789", 0);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().updateCustomer(1, "name1", "0123456789");
			Customer res=ezShop.getCustomer(1);
			assertEquals(c.getId(), res.getId());
			assertEquals(c.getCustomerName(), res.getCustomerName());
			assertEquals(c.getCustomerCard(), res.getCustomerCard());
			assertEquals(c.getPoints(), res.getPoints());
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException|InvalidCustomerIdException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testGetAllCustomersValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().insertCustomer("name2");
			ezShop.getDAO().insertCustomer("name3");
			ezShop.getDAO().insertCustomer("name4");
			ezShop.getDAO().insertCustomer("name5");
			assertEquals(5, ezShop.getAllCustomers().size());
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
			ezShop.getDAO().deleteCustomer(2);
			ezShop.getDAO().deleteCustomer(3);
			ezShop.getDAO().deleteCustomer(4);
			ezShop.getDAO().deleteCustomer(5);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testCreateCardValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			assertEquals(10, ezShop.createCard().length());
		} catch(UnauthorizedException e) {
			System.out.println("Error message: " + e);
			fail();
		}
	}
	
	@Test
	public void testAttachCardToCustomerValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			assertTrue(ezShop.attachCardToCustomer("0123456789", 1));
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException | InvalidCustomerIdException | InvalidCustomerCardException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testModifyPointsOnCardValidData() {
		User u= new ConcreteUser("name", 1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().updateCustomer(1, "name1", "0123456789");
			assertTrue(ezShop.modifyPointsOnCard("0123456789", 20));
			assertTrue(ezShop.modifyPointsOnCard("0123456789", -10));
			assertTrue(ezShop.modifyPointsOnCard("0123456789", -10));
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException | InvalidCustomerCardException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testReceiveCashPaymentInvalidTransactionId() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.receiveCashPayment(-1, 12);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.receiveCashPayment(0, 2);});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.receiveCashPayment(null, 400);});
	}
	
	@Test
	public void testReceiveCashPaymentInvalidUser() {
		User user = null;
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(UnauthorizedException.class, () -> {ezShop.receiveCashPayment(1, 12);});
	}
	
	@Test
	public void testReceiveCashPaymentInvalidCash() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidPaymentException .class, () -> {ezShop.receiveCashPayment(1, -12);});
		assertThrows(InvalidPaymentException .class, () -> {ezShop.receiveCashPayment(1, 0);});
	}
	
	@Test
	public void testReceiveCashPaymentReturnTransactionInexistent() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1 == ezShop.receiveCashPayment(1, 12));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testReceiveCashPaymentReturnTransactionNotEnded() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		SaleTransaction s = new ConcreteSaleTransaction();
		s.setTicketNumber(1);
		s.setPrice(23);
		s.setEntries(new ArrayList());
		s.setPayed(false);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(-1 == ezShop.receiveCashPayment(1, 1));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testReceveCashPaymentSaleTransactionEndedAndNotPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 44.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType(new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType(new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertTrue(5.5 == ezShop.receiveCashPayment(1, 50));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println(e);
		}
	}

	
	@Test
	public void testReceiveCashPaymentTransactionAlreadyPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 44.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType(new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType(new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertTrue(-1 == ezShop.receiveCashPayment(2, 50));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		}catch (DAOException e) {
			System.out.println(e);
		}
	}
	@Test
	public void testReceivenCreditCardPaymentInvalidReuturnId() {
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.receiveCreditCardPayment(-1, "4485370086510891" );});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCreditCardPayment(0, "4485370086510891" );});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnCreditCardPayment(null, "4485370086510891" );});
	}
	
	
	@Test
	public void testReceiveCreditCardPaymentInvalidUser() {
		User user = null;
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(UnauthorizedException.class, () -> {ezShop.receiveCreditCardPayment(1, "4485370086510891" );});
	}
	
	
	@Test
	public void testReceiveCreditCardPaymentInvalidCard() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		assertThrows(InvalidCreditCardException.class, () -> {ezShop.receiveCreditCardPayment(1, "1234131"); });
		assertThrows(InvalidCreditCardException.class, () -> {ezShop.receiveCreditCardPayment(1, ""); });
		assertThrows(InvalidCreditCardException.class, () -> {ezShop.receiveCreditCardPayment(1, null); });
	}
	
	@Test
	public void testReceiveCreditCardPaymentTransactionInexistent() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.receiveCreditCardPayment(1, "4716258050958645"));	
		} catch (Exception e) {
			fail();
		}
	}
	
	
	@Test
	public void testReceiveCreditCardPaymentTransactionNotEnded() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setEntries(new ArrayList<>());
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setTransactionId(1);
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.receiveCreditCardPayment(1, "4716258050958645"));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testReceiveCreditCardPaymentTransactionNotEnoughMoney() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		r.setEntries(new ArrayList<>());
		r.setPayed(false);
		r.setDiscountRate(0);
		r.setTransactionId(1);
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 44.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType(new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType(new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		ezShop.setReturnTransaction(r);
		try {
			assertFalse(ezShop.receiveCreditCardPayment(1, "4716258050958645"));
		} catch (Exception e) {
			fail();
		}
	}

	
	@Test
	public void testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed() {
		
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 43.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType(new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType(new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		try {
			//System.out.println(ezShop.returnCreditCardPayment(1, "4485370086510891"));
			assertTrue(ezShop.receiveCreditCardPayment(1, "4485370086510891"));	
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		o.updateCreditCardAmount("4485370086510891", 43.5, false);
	}
	
	@Test
	public void testReceiveCreditCardPaymentCardNotRegistered() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 43.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType(new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType(new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		
		
		try {
			assertFalse(ezShop.receiveCreditCardPayment(1, "1002939910217"));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testReceievCreditCardPaymentReturnTransactionAlreadyPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		
		TicketEntry t3 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t4 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 43.5);
		SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 32.5);
		s1.setPayed(false);
		s2.setPayed(true);
		try {
			dao.createProductType(new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType(new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
		} catch (DAOException e) {
			System.out.println(e);
		}
		try {
			assertFalse(ezShop.receiveCreditCardPayment(2, "4485370086510891"));
		} catch (Exception e) {
			fail();
		}
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test

	public void testCustomerDetachCard() throws InvalidCustomerNameException {
		User u= new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
		ezShop.setRunningUser(u);
		
		try {		
			ezShop.getDAO().insertCustomer("name1");
			ezShop.getDAO().bindCardToCustomer("0123456789", 1);
			assertTrue(ezShop.modifyCustomer(1, "name1", ""));
		} catch(DAOException e){
			fail();
		}catch(UnauthorizedException | InvalidCustomerIdException | InvalidCustomerCardException e) {
			System.out.println("Error message: " + e);
			fail();
		}
		try {
			ezShop.getDAO().deleteCustomer(1);
		} catch (DAOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testUpdatePositionNull() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		ezShop.getDAO().createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
		ezShop.getDAO().updatePosition(1, "1-A-23");
		
		assertTrue(ezShop.updatePosition(1, null));
		
		ezShop.getDAO().resetApplication();
	}
	
	@Test
	public void testUpdatePositionEmpty() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		ezShop.getDAO().createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
		ezShop.getDAO().updatePosition(1, "1-A-23");
		
		assertTrue(ezShop.updatePosition(1, ""));
		
		ezShop.getDAO().resetApplication();
	}
	
	@Test
	public void testAddProudctToSaleRFIDInvalidTransactionID() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.addProductToSaleRFID(0,"000000000001");});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.addProductToSaleRFID(-1,"000000000001");});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.addProductToSaleRFID(null,"000000000001");});
	}
	
	@Test
	public void testAddProudctToSaleRFIDInvalidRFID() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		assertThrows(InvalidRFIDException.class, () -> {ezShop.addProductToSaleRFID(1,"");});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.addProductToSaleRFID(1, null);});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.addProductToSaleRFID(1,"00000000001");});
	}
	
	@Test
	public void testAddProudctToSaleRFIDInvalidUser() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		User user = new ConcreteUser("nome",1, "123", "ciao");
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.addProductToSaleRFID(1,"000000000001");});
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {ezShop.addProductToSaleRFID(1, "000000000001");});
	}
	
	@Test
	public void testAddProudctToSaleRFIDNotExists() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		ezShop.setSaleTransaction(saleTransaction);
		User user = new ConcreteUser("nome",1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.addProductToSaleRFID(1, "000000000001"));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testAddProudctToSaleRFIDNotOpenSale() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		ezShop.setSaleTransaction(new ConcreteSaleTransaction());
		User user = new ConcreteUser("nome",1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.addProductToSaleRFID(1, "000000000001"));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testAddProudctToSaleRFIDValid() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(1, new ArrayList<TicketEntry>(), 0, 21);
		Product p = new ConcreteProduct();
		p.setBarCode("1234567891231");
		p.setRFID("000000000001");
		p.setTransactionId(1);
		saleTransaction.setTicketNumber(1);
		dao.createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
		dao.updatePosition(1, "1-A-1");
		dao.updateQuantity(1, 12);
		ezShop.setSaleTransaction(saleTransaction);
		dao.storeProduct(p);
		User user = new ConcreteUser("nome",1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertTrue(ezShop.addProductToSaleRFID(1, "000000000001"));
			dao.resetApplication();
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	//
	@Test
	public void testdeleteProudctToSaleRFIDInvalidTransactionID() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.deleteProductFromSaleRFID(0,"000000000001");});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.deleteProductFromSaleRFID(-1,"000000000001");});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.deleteProductFromSaleRFID(null,"000000000001");});
	}
	
	@Test
	public void testDeleteProudctToSaleRFIDInvalidRFID() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		assertThrows(InvalidRFIDException.class, () -> {ezShop.deleteProductFromSaleRFID(1,"");});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.deleteProductFromSaleRFID(1, null);});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.deleteProductFromSaleRFID(1,"00000000001");});
	}
	
	@Test
	public void testDeleteProudctToSaleRFIDInvalidUser() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		User user = new ConcreteUser("nome",1, "123", "ciao");
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.deleteProductFromSaleRFID(1,"000000000001");});
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {ezShop.deleteProductFromSaleRFID(1, "000000000001");});
	}
	
	@Test
	public void testDeleteProudctToSaleRFIDNotExists() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(0, new ArrayList<TicketEntry>(), 0, 0);
		ezShop.setSaleTransaction(saleTransaction);
		User user = new ConcreteUser("nome",1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.deleteProductFromSaleRFID(1, "000000000001"));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testDeleteProudctFromSaleRFIDNotOpenSale() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		ezShop.setSaleTransaction(new ConcreteSaleTransaction());
		User user = new ConcreteUser("nome",1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertFalse(ezShop.deleteProductFromSaleRFID(1, "000000000001"));
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testDeleteProudctToSaleRFIDValid() throws DAOException, InvalidProductIdException, InvalidLocationException, UnauthorizedException {
		SaleTransaction saleTransaction = new ConcreteSaleTransaction(1, new ArrayList<TicketEntry>(), 0, 21);
		Product p = new ConcreteProduct();
		p.setBarCode("1234567891231");
		p.setRFID("000000000001");
		p.setTransactionId(1);
		dao.createProductType(new ConcreteProductType(null, "description", "1234567891231", "note", null, 5.0, null));
		dao.updatePosition(1, "1-A-1");
		dao.updateQuantity(1, 12);
		List<Product> products = new ArrayList<>();
		products.add(p);
		saleTransaction.setTicketNumber(1);
		saleTransaction.setSaleProducts(products);
		ezShop.setSaleTransaction(saleTransaction);
		dao.storeProduct(p);
		User user = new ConcreteUser("nome",1, "123", Constants.ADMINISTRATOR);
		ezShop.setRunningUser(user);
		try {
			assertTrue(ezShop.deleteProductFromSaleRFID(1, "000000000001"));
			assertTrue(ezShop.getSaleTransaction().getSaleProducts().isEmpty());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
		dao.resetApplication();
	}
	
	@Test
	public void testReturnProductRFIDInvalidReturnID() {
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnProductRFID(null, "000000000001");});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnProductRFID(-1, "000000000001");});
		assertThrows(InvalidTransactionIdException.class, () -> {ezShop.returnProductRFID(0, "000000000001");});
	}
	
	@Test
	public void testReturnProductRFIDInvalidRFID() {
		assertThrows(InvalidRFIDException.class, () -> {ezShop.returnProductRFID(1, null);});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.returnProductRFID(1, "");});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.returnProductRFID(1, "000001111");});
		assertThrows(InvalidRFIDException.class, () -> {ezShop.returnProductRFID(1, "abcdefghijkl");});
	}
	
	@Test
	public void testReturnProductRFIDInvalidUser() {
		User user = new ConcreteUser("username", 1, "password", "role");
		ezShop.setRunningUser(user);
		assertThrows(UnauthorizedException.class, () -> {ezShop.returnProductRFID(1, "000000000001");});
		ezShop.setRunningUser(null);
		assertThrows(UnauthorizedException.class, () -> {ezShop.returnProductRFID(1, "000000000001");});
	}
	
	@Test
	public void testReturnProductRFIDInvalidReturnTransaction() throws DAOException {
		try {
			User user = new ConcreteUser("username", 1, "password", Constants.ADMINISTRATOR);
			ezShop.setRunningUser(user);
			ReturnTransaction rt = new ConcreteReturnTransaction();
			rt.setReturnId(null);
			assertFalse(ezShop.returnProductRFID(1, "000000000001"));
		} catch(InvalidRFIDException e) {
			fail();
		} catch(UnauthorizedException e) {
			fail();
		} catch(InvalidTransactionIdException e) {
			fail();
		}
		
		try {
			User user = new ConcreteUser("username", 1, "password", Constants.SHOP_MANAGER);
			ezShop.setRunningUser(user);
			ReturnTransaction rt = new ConcreteReturnTransaction();
			rt.setReturnId(1);
			assertFalse(ezShop.returnProductRFID(2, "000000000001"));
		} catch(InvalidRFIDException e) {
			fail();
		} catch(UnauthorizedException e) {
			fail();
		} catch(InvalidTransactionIdException e) {
			fail();
		}
		dao.resetApplication();
	}

	@Test
	public void testReturnProductRFIDInvalidProductNull() {
		try {
			User user = new ConcreteUser("username", 1, "password", Constants.CASHIER);
			ezShop.setRunningUser(user);
			Product p = new ConcreteProduct();
			p.setBarCode("1234567891231");
			p.setRFID("000000000001");
			ReturnTransaction rt = new ConcreteReturnTransaction(1, 1, null, 0, 0);
			ezShop.setReturnTransaction(rt);
			ProductType pt = new ConcreteProductType(1, "description", "1234567891231", "note", 10, 5.0, "1-A-1");
			dao.createProductType(pt);
			dao.recordProductArrivalRFID(1, 1, "000000000001", "1234567891231");
			assertFalse(ezShop.returnProductRFID(1, "000000000001"));
			dao.resetApplication();
		} catch(DAOException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidTransactionIdException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidRFIDException e) {
			e.printStackTrace();
			fail();
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			fail();
		}
		;
	}
	
	@Test
	public void testReturnProductRFIDInvalidProduct() {
		try {
			User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
			ezShop.setRunningUser(user);
			ReturnTransaction r = new ConcreteReturnTransaction();
			r.setPayed(false);
			r.setDiscountRate(0);
			r.setReturnId(1);
			r.setTransactionId(2);
			r.setEntries(new ArrayList<>());
			ezShop.setReturnTransaction(r);
			
			TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
			TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
			List<TicketEntry> tickets = new ArrayList<>();
			SaleTransaction s1 = new ConcreteSaleTransaction(3, new ArrayList<>(), 0 , 32.5);
			SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
			s1.setPayed(false);
			s2.setPayed(true);
			tickets.add(t1);
			tickets.add(t2);
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			dao.recordProductArrivalRFID(1, 50, "0000000000001", "123456789104");
			dao.recordProductArrivalRFID(1, 10, "000011111111", "4314324224124");
			assertFalse(ezShop.returnProductRFID(1, "000000000001"));
			dao.resetApplication();
		} catch (DAOException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidTransactionIdException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidRFIDException e) {
			e.printStackTrace();
			fail();
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReturnProductRFIDValid() {
		try {
			dao.resetApplication();
			User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);
			ezShop.setRunningUser(user);
			ReturnTransaction r = new ConcreteReturnTransaction();
			r.setPayed(false);
			r.setDiscountRate(0);
			r.setReturnId(1);
			r.setTransactionId(2);
			r.setEntries(new ArrayList<>());
			ezShop.setReturnTransaction(r);
			
			TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
			TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
			List<TicketEntry> tickets = new ArrayList<>();
			SaleTransaction s2 = new ConcreteSaleTransaction(2, tickets, 0 , 44.5);
			s2.setPayed(true);
			tickets.add(t1);
			tickets.add(t2);
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.updateQuantity(3, 150);
			Product p1 = new ConcreteProduct();
			Product p2 = new ConcreteProduct();
			p1.setBarCode("123456789104");
			p1.setRFID("000000000001");
			p1.setTransactionId(s2.getTicketNumber());
			s2.getSaleProducts().add(p1);
			dao.storeProduct(p1);
			p2.setBarCode("4314324224124");
			p2.setRFID("000011111111");
			p2.setTransactionId(s2.getTicketNumber());
			s2.getSaleProducts().add(p2);
			dao.storeProduct(p2);
			dao.storeSaleTransaction(s2);
			assertTrue(ezShop.returnProductRFID(1, "000000000001"));
			dao.resetApplication();
		} catch (DAOException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidTransactionIdException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidRFIDException e) {
			e.printStackTrace();
			fail();
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			fail();
		}
		
	}
}



