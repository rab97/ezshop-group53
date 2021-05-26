package it.polito.ezshop.test;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;
import scala.collection.immutable.List;
import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;

import it.polito.ezshop.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Objects;

public class DAOTest {
    
    IDAOEZshop dao =  null;

    @Before
	public void setUp() {
		dao = new DAOEZShop();
		try {
			dao.resetApplication();
		} catch (DAOException e) {
			e.printStackTrace();
		}
    }
    
    @Test
    public void testInsertAndRemoveUser(){

        User testUser= new ConcreteUser("test", null, "passwordTest", Constants.CASHIER);
        try{
            Integer newUserId= dao.insertUser(testUser.getUsername(),testUser.getPassword(), testUser.getRole());
            if(newUserId<=0){
                fail();
            }
            assertTrue(dao.removeUser(newUserId));
            assertFalse(dao.removeUser(newUserId)); //Second removal

        }catch(DAOException e){
            fail();
        }   

    }

    @Test
    public void testGetAllUsers(){

        User u1= new ConcreteUser("test1", null, "p1", Constants.CASHIER);
        User u2= new ConcreteUser("test2", null, "p2", Constants.CASHIER);
        User u3= new ConcreteUser("test3", null, "p3", Constants.CASHIER);

        try{
            Integer id1= dao.insertUser(u1.getUsername(),u1.getPassword(), u1.getRole());
            Integer id2= dao.insertUser(u2.getUsername(),u2.getPassword(), u2.getRole());
            Integer id3= dao.insertUser(u3.getUsername(),u3.getPassword(), u3.getRole());

            System.out.println("id1, id2, id3 = " + id1 + " " + id2 + " " + id3 + " ");
            if(id1<=0||id2<=0||id3<=0){
                fail();
            }
            
            //Scrivere sia il caso lista piena che vuota
            //List<User> testList= new ArrayList<User>;
            dao.removeUser(id1);
            dao.removeUser(id2);
            dao.removeUser(id3);

        }catch(DAOException e){
            fail();
        }   
    }

    @Test
    public void testSearchUserById(){

        User testUser= new ConcreteUser("test1", null, "passwordTest", Constants.CASHIER);
        try{
            Integer newUserId= dao.insertUser(testUser.getUsername(),testUser.getPassword(), testUser.getRole());
            if(newUserId<=0){
                fail();
            }
            assertEquals(newUserId, dao.searchUserById(newUserId).getId());
            
            dao.removeUser(newUserId);
        }catch(DAOException e){
            fail();
        }   
    }


    @Test
    public void testUpdateRights(){

        User testUser= new ConcreteUser("test", null, "passwordTest", Constants.CASHIER);
        try{
            assertFalse(dao.updateRights(0, Constants.ADMINISTRATOR)); //User doesn't exist

            Integer newUserId= dao.insertUser(testUser.getUsername(),testUser.getPassword(), testUser.getRole());
            if(newUserId<=0){
                fail();
            }
            assertTrue(dao.updateRights(newUserId, Constants.SHOP_MANAGER));
            
            dao.removeUser(newUserId);
        }catch(DAOException e){
            fail();
        }   
    }
    
    @Test
    public void testCreateProductTypeInvalid() {
    	//Test with a reserved word for SQL
    	assertThrows(DAOException.class, () -> {
    		dao.createProductType(new ConcreteProductType(null, "'ORDER'", null, null, null, null, null));
    	});
    }
    
    @Test
    public void testCreateProductTypeValid() throws DAOException {
    	ProductType pt = new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23");
    	
    	dao.createProductType(pt);
    	
    	assertEquals(pt.getId(), dao.getProductTypeByBarCode("1234567891231").getId());
    	
    	dao.resetApplication();
    }
    
    @Test
    public void testGetProductTypeByBarCodeInvalid() {
    	assertThrows(DAOException.class, () -> {
    		dao.getProductTypeByBarCode("'ORDER'");
    	});
    }
    
    @Test
    public void testGetProductTypeByBarCodeValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	
    	assertEquals(Integer.valueOf(1), dao.getProductTypeByBarCode("1234567891231").getId());
    	
    	dao.resetApplication();
    }
    
    @Test
    public void testGetProductTypeByBarCodeNullProduct() throws DAOException {
    	assertEquals(null, dao.getProductTypeByBarCode("123456781231"));
    }
    
    @Test
    public void testGetProductTypeByDescriptionInvalid() {
    	assertThrows(DAOException.class, () -> {
    		dao.getProductTypeByDescription("'ORDER'");
    	});
    }
    
    @Test
    public void testGetProductTypeByDescriptionValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	
    	assertEquals(1, dao.getProductTypeByDescription("description").size());
    	
    	dao.resetApplication();
    	
    	assertEquals(0, dao.getProductTypeByDescription("des").size());
    }
    
    @Test
    public void testUpdateQuantityInvalidQuantity() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	//location == null
    	assertFalse(dao.updateQuantity(1, -50));
    	
    	//location is empty
    	dao.updatePosition(1, "");
    	assertFalse(dao.updateQuantity(1, 50));
    	
    	//value < 0
    	dao.updatePosition(1, "1-A-23");
    	assertFalse(dao.updateQuantity(1, -50));
    	
    	dao.resetApplication();
    	
    }
    
    @Test
    public void testUpdateQuantityValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.updatePosition(1, "1-A-23");
    	
    	assertTrue(dao.updateQuantity(1, 50));
    	
    	dao.resetApplication();
    }
    
	public void testInsertReturnTransactionTest() {
		try {
			assertEquals(Integer.valueOf(0), dao.insertReturnTransaction());
			dao.storeReturnTransaction(new ConcreteReturnTransaction(1,1, new ArrayList<>(), 1.0, 2.5));			
			System.out.println(dao.insertReturnTransaction());
			assertEquals(Integer.valueOf(1), dao.insertReturnTransaction());
			dao.storeReturnTransaction(new ConcreteReturnTransaction(1,1, new ArrayList<>(), 1.0, 2.5));
			assertEquals(Integer.valueOf(2), dao.insertReturnTransaction());
		} catch (DAOException e) {
			fail();
		}
		
	}

    @Test
    public void testUpdatePositionValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.updatePosition(1, "1-A-23");
    	
    	assertEquals("1-A-23", dao.getProductTypeByBarCode("1234567891231").getLocation());
    	
    	dao.resetApplication();
    }
    
    @Test
    public void testSearchPositionInvalid() {
    	assertThrows(DAOException.class, () -> {
    		dao.searchPosition("'ORDER'");
    	});
    }

    @Test
    public void testSearchPositionAlreadyExist() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.updatePosition(1, "1-A-23");
    	
    	assertTrue(dao.searchPosition("1-A-23"));
    	
    	dao.resetApplication();
    	
    }

    @Test
    public void testSearchPositionValid() throws DAOException {
    	assertFalse(dao.searchPosition("1-A-23"));
    }
    
    @Test
    public void testUpdateProductInvalid() {
    	assertThrows(DAOException.class, () -> {
    		dao.updateProduct(new ConcreteProductType(null, "'ORDER'", null, null, null, null, null));
    	});
    }
    
    @Test
    public void testUpdateProductUnsuccess() throws DAOException {
    	assertFalse(dao.updateProduct(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23")));
    }
    
    @Test
    public void testUpdateProductSuccess() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	
    	assertTrue(dao.updateProduct(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23")));
    	
    	dao.resetApplication();
    }

    @Test
    public void testDeleteProductTypeUnsucess() throws DAOException {
    	assertFalse(dao.deleteProductType(1));
    }
    
    @Test
    public void testDeleteProductTypeSuccess() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	
    	assertTrue(dao.deleteProductType(1));
    	
    	dao.resetApplication();
    }

    @Test
    public void testSearchProductByIdUnsuccess() throws DAOException {
    	assertFalse(dao.searchProductById(1));
    }
    
    @Test
    public void testSearchProductByIdSuccess() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	
    	assertTrue(dao.searchProductById(1));
    	
    	dao.resetApplication();;
    }

    @Test
    public void testGetAllProductTypetValid() throws DAOException {
    	//ArrayList empty
    	assertEquals(0, dao.getAllProducTypet().size());
    	
    	//ArrayList with 1 product
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	
    	assertEquals(1, dao.getAllProducTypet().size());
    	
    	dao.resetApplication();
    }


    @Test
    public void testInsertSaleTransaction(){

        SaleTransaction stTest= new ConcreteSaleTransaction(1, null, 0.0, 0.0);

        try{
            Integer stId= dao.insertSaleTransaction();
            if(stId<=0){
                fail();
            }
            SaleTransaction returnedTransaction = dao.searchSaleTransaction(stId);
            assertEquals(stId, returnedTransaction.getTicketNumber());
            //assertEquals(stTest.getEntries(), actual);

        }catch(DAOException e){
            fail();
        }
    }


}
