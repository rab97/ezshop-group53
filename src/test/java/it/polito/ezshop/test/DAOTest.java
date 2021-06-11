package it.polito.ezshop.test;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;
import scala.Array;

import java.util.List;
import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;

import it.polito.ezshop.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.time.LocalDate;
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
			System.out.println(e);
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
    
    @Test
	public void testInsertReturnTransactionTest() {
		try {
			assertEquals(Integer.valueOf(0), dao.insertReturnTransaction());
			ReturnTransaction r = new ConcreteReturnTransaction(1,1, new ArrayList<>(), 1.0, 2.5);
			r.setPayed(true);
			dao.storeReturnTransaction(r);			
			System.out.println(dao.insertReturnTransaction());
			assertEquals(Integer.valueOf(1), dao.insertReturnTransaction());
			dao.storeReturnTransaction(new ConcreteReturnTransaction(2,1, new ArrayList<>(), 1.0, 2.5));
			assertEquals(Integer.valueOf(2), dao.insertReturnTransaction());
			dao.storeReturnTransaction(new ConcreteReturnTransaction(3,1, new ArrayList<>(), 1.0, 2.5));
			assertEquals(Integer.valueOf(3), dao.insertReturnTransaction());
		} catch (DAOException e) {
			fail();
		}
		
	}
    
    @Test
	public void testDeleteReturnTransaction() {
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
			TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
			ArrayList<TicketEntry> tickets = new ArrayList<>();
			tickets.add(t1);
			tickets.add(t2);
			dao.storeReturnTransaction(new ConcreteReturnTransaction(1,1, tickets, 1.0, 2.5));
			assertTrue(dao.deleteReturnTransaction(1));
		} catch (DAOException e) {
			fail();
		}
	}
    
    @Test
	public void testDeleteReturnTransactionFailed() {
    	try {
			assertFalse(dao.deleteReturnTransaction(1));
			assertFalse(dao.deleteReturnTransaction(0));
			assertFalse(dao.deleteReturnTransaction(-1));
		} catch (DAOException e) {
			fail();
		}
	}
    
    @Test
   	public void testSearchReturnTransaction() {
       	try {
       		dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
			TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
			ArrayList<TicketEntry> tickets = new ArrayList<>();
			tickets.add(t1);
			tickets.add(t2);
			dao.storeReturnTransaction(new ConcreteReturnTransaction(1,1, tickets, 25.0, 2.5));
			assertEquals(Integer.valueOf(1), dao.searchReturnTransaction(1).getReturnId());
			assertTrue(Double.valueOf(25.0) == dao.searchReturnTransaction(1).getPrice());
			assertTrue(2.5 ==  dao.searchReturnTransaction(1).getDiscountRate());
   		} catch (DAOException e) {
   			fail();
   		}
   	}
    
    @Test
   	public void testSearchReturnTransactionNull() {
       	try {
			assertEquals(null, dao.searchReturnTransaction(1));
			} catch (DAOException e) {
   			fail();
   		}
   	}

    @Test
   	public void testInsertBalanceOperation() {
       	try {
			assertTrue(null, dao.insertBalanceOperation(50, "CREDIT",  LocalDate.of(2021, 05, 26)));
			assertTrue(null, dao.insertBalanceOperation(40, "DEBIT",  LocalDate.of(2021, 05, 22)));
			assertTrue(null, dao.insertBalanceOperation(11, "DEBIT",  LocalDate.of(2021, 03, 18)));
			assertThrows(DAOException.class, () -> {dao.insertBalanceOperation(50, null,  null);});
       	} catch (DAOException e) {
   			fail();
   		}
   	}

    @Test
   	public void testGetBalanceOperations() {
       	try {
			dao.insertBalanceOperation(50, "CREDIT",  LocalDate.of(2021, 04, 26));
			dao.insertBalanceOperation(40, "DEBIT",  LocalDate.of(2021, 05, 22));
			dao.insertBalanceOperation(11, "DEBIT",  LocalDate.of(2021, 03, 18));
			assertEquals(2, dao.getBalanceOperations(LocalDate.of(2021, 04, 1), LocalDate.of(2021, 07, 1)).size());
			assertThrows(DAOException.class, () -> {dao.insertBalanceOperation(50, null,  null);});
       	} catch (DAOException e) {
   			fail();
   		}
   	}
    
    @Test
   	public void testStoreReturnTransaction() {
       	try {
			assertTrue(dao.storeReturnTransaction(new ConcreteReturnTransaction(1,1,new ArrayList<>(),24.0, 0.0)));
       	} catch (DAOException e) {
   			fail();
   		}
   	}

    @Test
   	public void testSetReturnTransactionPaid() {
       	try {
       		dao.storeReturnTransaction(new ConcreteReturnTransaction(1,1,new ArrayList<>(),24.0, 0.0));
       		assertTrue(dao.setReturnTransactionPaid(1));
       		assertFalse(dao.setReturnTransactionPaid(2));
       		
       	} catch (DAOException e) {
   			fail();
   		}
   	}
    
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
    public void testInsertSaleTransaction() throws DAOException{

        SaleTransaction stTest= new ConcreteSaleTransaction(1, new ArrayList(), 0.0, 0.0);
        dao.storeSaleTransaction(stTest);
        try{
        	
            assertEquals(Integer.valueOf(1), dao.insertSaleTransaction());
            dao.storeSaleTransaction(new ConcreteSaleTransaction(1, new ArrayList<>(), 1.2, 0));
            assertEquals(Integer.valueOf(2), dao.insertSaleTransaction());


        }catch(DAOException e){
            fail();
        }
    }


    
    @Test 
    public void testInsertNewOrderProductNotExists() throws DAOException {
    	assertEquals(Integer.valueOf(-1), dao.insertNewOrder("1234567891231", 1, 1.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testInsertNewOrderValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	assertEquals(Integer.valueOf(1), dao.insertNewOrder("1234567891231", 1, 1.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testPayOrderDirectlyProductNotExists() throws DAOException {
    	assertEquals(Integer.valueOf(-1), dao.payOrderDirectly("1234567891231", 1, 1.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testPayOrderDirectlyValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	assertEquals(Integer.valueOf(1), dao.payOrderDirectly("1234567891231", 1, 1.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testPayOrderOrderNotExists() throws DAOException {
    	assertFalse(dao.payOrder(5, 10.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testPayOrderIssued() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.insertNewOrder("1234567891231", 1, 1.0);
    	assertTrue(dao.payOrder(1, 5.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testPayOrderPayed() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.payOrderDirectly("1234567891231", 1, 1.0);
    	assertTrue(dao.payOrder(1, 5.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testPayOrderCompleted() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.payOrderDirectly("1234567891231", 1, 1.0);
    	dao.recordArrival(1);
    	assertFalse(dao.payOrder(1, 5.0));
    	dao.resetApplication();
    }
    
    @Test 
    public void testRecordArrivalOrderNotExists() throws DAOException{
    	assertFalse(dao.recordArrival(1));
    	dao.resetApplication();
    }
    
    @Test 
    public void testRecordArrivalValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.payOrderDirectly("1234567891231", 1, 1.0);
    	assertTrue(dao.recordArrival(1));
    	dao.resetApplication();
    }
    
    @Test 
    public void testGetOrderOrderNotExists() throws DAOException {
    	assertNull(dao.getOrder(1));
    	dao.resetApplication();
    }
    
    @Test 
    public void testGetOrderValid() throws DAOException {
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.payOrderDirectly("1234567891231", 1, 1.0);
    	assertEquals("1234567891231", dao.getOrder(1).getProductCode());
    	assertEquals("PAYED", dao.getOrder(1).getStatus());
    	dao.resetApplication();
    }
    
    @Test 
    public void testGetAllOrdersValid() throws DAOException {
    	//ArrayList empty
    	assertEquals(0, dao.getAllOrders().size());
    	
    	//ArrayList with 1 product
    	dao.createProductType(new ConcreteProductType(1, "description", "1234567891231", "note", 5, 5.0, "1-A-23"));
    	dao.insertNewOrder("1234567891231", 1, 1.0);
    	assertEquals(1, dao.getAllOrders().size());
    	
    	dao.resetApplication();
    }
    
    @Test
    public void testInsertCustomerButAlreadyExists() throws DAOException {
    	dao.insertCustomer("name");
    	assertEquals(Integer.valueOf(-1), dao.insertCustomer("name"));
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testInsertCustomerValid() throws DAOException {
    	assertEquals(Integer.valueOf(1), dao.insertCustomer("name"));
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testUpdateCustomerInvalidCard() throws DAOException {
    	dao.insertCustomer("name");
    	assertFalse(dao.updateCustomer(1, "name", "0123"));	//too short
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testUpdateCustomerCardAlreadyExists() throws DAOException {
    	dao.insertCustomer("name1");
    	dao.insertCustomer("name2");
    	dao.updateCustomer(1, "name1", "0123456789");
    	assertFalse(dao.updateCustomer(2, "name2", "0123456789"));	
    	dao.deleteCustomer(1);
    	dao.deleteCustomer(2);
    }
    
    @Test
    public void testUpdateCustomerValid() throws DAOException {
    	dao.insertCustomer("name1");
    	assertTrue(dao.updateCustomer(1, "name1", "0123456789"));	
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testDeleteCustomerButNotExists() throws DAOException {
    	assertFalse(dao.deleteCustomer(5));	
    }
    
    @Test
    public void testDeleteCustomerValid() throws DAOException {
    	dao.insertCustomer("name1");
    	assertTrue(dao.deleteCustomer(1));	
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testGetCustomerButNotExists() throws DAOException {
    	assertNull(dao.getCustomer(5));	
    }
    
    @Test
    public void testGetCustomerValid() throws DAOException {
    	dao.insertCustomer("name1");
    	assertEquals("name1", dao.getCustomer(1).getCustomerName());	
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testGetAllCustomers() throws DAOException {
    	//ArrayList empty
    	assertEquals(0, dao.getAllCustomers().size());
    	
    	//ArrayList with 1 product
    	dao.insertCustomer("name1");
    	dao.insertCustomer("name2");
    	assertEquals(2, dao.getAllCustomers().size());
    	dao.deleteCustomer(1);
    	dao.deleteCustomer(2);
    }
    
    @Test
    public void testBindCardToCustomerButNotExists() throws DAOException {
    	assertFalse(dao.bindCardToCustomer("0123456789", 7));
    }
    
    @Test
    public void testBindCardToCustomerCardAlreadyExists() throws DAOException {
    	dao.insertCustomer("name1");
    	dao.insertCustomer("name2");
    	dao.updateCustomer(1, "name1", "0123456789");
    	assertFalse(dao.bindCardToCustomer("0123456789",2));  
    	dao.deleteCustomer(1);
    	dao.deleteCustomer(2);
    }
    
    @Test
    public void testBindCardToCustomerValid() throws DAOException {
    	dao.insertCustomer("name1");
    	dao.insertCustomer("name2");
    	dao.updateCustomer(1, "name1", "0123456789");
    	assertTrue(dao.bindCardToCustomer("9876543210",2));
    	dao.deleteCustomer(1);
    	dao.deleteCustomer(2);
    }
    
    @Test
    public void testUpdatePointsCustomerNotExists() throws DAOException {
    	assertFalse(dao.updatePoints("0123456789", 10));
    }
    
    @Test
    public void testUpdatePointsButNotEnough() throws DAOException {
    	dao.insertCustomer("name1");
    	dao.updateCustomer(1, "name1", "0123456789");
    	assertFalse(dao.updatePoints("0123456789", -20));
    	dao.deleteCustomer(1);
    }
    
    @Test
    public void testUpdatePointsValid() throws DAOException {
    	dao.insertCustomer("name1");
    	dao.updateCustomer(1, "name1", "0123456789");
    	assertTrue(dao.updatePoints("0123456789", 20));
    	dao.deleteCustomer(1);
    }
  
    @Test
    public void testSearchUserInvalid() {
    	assertThrows(DAOException.class, () -> {
    		dao.searchUser("'ORDER'", null);
    	});
    }
    
    @Test
    public void testSearchUserNull() throws DAOException {
    	assertEquals(null, dao.searchUser("name", "password"));
    }
    
    @Test
    public void testSetTransactionPaid() {
    	try {
			dao.storeSaleTransaction(new ConcreteSaleTransaction(1, new ArrayList(), 25.0, 2.5));
			assertTrue(dao.setSaleTransactionPaid(1));
    	} catch (DAOException e) {
			e.printStackTrace();
		}
    	
    }
    
    @Test
    public void testSetTransactionPaidInvalid() throws DAOException {
		assertFalse(dao.setSaleTransactionPaid(1));
    }
    
    @Test
    public void testRemoveSaleTransactionInvalid() {
    	try {
			assertFalse(dao.removeSaleTransaction(1));
			assertFalse(dao.removeSaleTransaction(0));
			assertFalse(dao.removeSaleTransaction(-1));
    	} catch (DAOException e) {
			e.printStackTrace();
		}
    	
    }
    
    @Test
    public void testRemoveSaleTransactionValid() {
    	try {
    		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
    		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
    		List<TicketEntry> tickets = new ArrayList<>();
    		tickets.add(t1);
    		tickets.add(t2);
    		SaleTransaction s = new ConcreteSaleTransaction(1, tickets, 25.0, 2.5);
    		dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
    		dao.storeSaleTransaction(s);
			assertTrue(dao.removeSaleTransaction(1));
    	} catch (DAOException e) {
			e.printStackTrace();
		}
    }
    
    @Test
    public void testGetAllUser() {
    	try {
			dao.insertUser("user1", "user1", Constants.ADMINISTRATOR);
			dao.insertUser("user2", "user1", Constants.ADMINISTRATOR);
			dao.insertUser("user3", "user1", Constants.ADMINISTRATOR);
			List<User> users = dao.getAllUsers();
			assertEquals(3, users.size());
			assertEquals("user1", users.get(0).getUsername());
			assertEquals("user2", users.get(1).getUsername());
			assertEquals("user3", users.get(2).getUsername());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    @Test
    public void testSearchTicketEntry() {
    	try {
    		TicketEntry t1 = new ConcreteTicketEntry("123456789104","red bic", 25, 0.5, 0.0);
    		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
    		List<TicketEntry> tickets = new ArrayList<>();
    		tickets.add(t1);
    		tickets.add(t2);
    		SaleTransaction s = new ConcreteSaleTransaction(1, tickets, 25.0, 2.5);
    		dao.createProductType( new ConcreteProductType(Integer.valueOf(1), "red bic", "123456789104", "", 50, Double.valueOf(0.5), "1-A-25"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
    		dao.storeSaleTransaction(s);
    		assertEquals("123456789104", dao.searchTicketEntry(1, 1).getBarCode());
    		
    		assertEquals(25, dao.searchTicketEntry(1, 1).getAmount());
    		assertTrue(0.0 == dao.searchTicketEntry(1, 1).getDiscountRate());
    		assertEquals("red bic", dao.searchTicketEntry(1, 1).getProductDescription());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testSearchTicketEntryEmpty() {
    	try {
    		assertEquals(null, dao.searchTicketEntry(1, 1));	
    	} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testUpdateSaleTransactionPrice() {
    	TicketEntry t1 = new ConcreteTicketEntry("123456789104","red bic", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		SaleTransaction s = new ConcreteSaleTransaction(1, tickets, 25.0, 2.5);
		
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "123456789104", "", 50, Double.valueOf(12.5), "1-A-24"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.storeSaleTransaction(s);
			assertTrue(dao.updateSaleTransactionPrice(1, 21, false));
			assertTrue(23.5 == dao.searchSaleTransaction(1).getPrice());
			assertTrue(dao.updateSaleTransactionPrice(1, 10, true));
			assertTrue(13.5 == dao.searchSaleTransaction(1).getPrice());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}

    	
    public void testSearchUserValid() throws DAOException {
    	dao.insertUser("name", "password", Constants.ADMINISTRATOR);
    	
    	assertEquals(Integer.valueOf(1), dao.searchUser("name", "password").getId());
    	
    	dao.removeUser(1);
    }
    
    @Test
    public void testUpdateSaleTransactionEntries() {
    	TicketEntry t1 = new ConcreteTicketEntry("123456789104","red bic", 25, 2.5, 2.5);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 2.5);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		SaleTransaction s = new ConcreteSaleTransaction(1, tickets, 25.0, 2.5);
		try {
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "123456789104", "", 50, Double.valueOf(12.5), "1-A-24"));
			dao.createProductType( new ConcreteProductType(Integer.valueOf(2), "bics", "4314324224124", "", 150, Double.valueOf(12.5), "1-A-24"));
			dao.updatePosition(1, "1-A-25");
			dao.updatePosition(2, "1-A-24");
			dao.updateQuantity(1, 50);
			dao.updateQuantity(2, 150);
			dao.storeSaleTransaction(s);
			t1.setAmount(20);
			t2.setAmount(1);
			assertTrue(dao.updateSaleTransactionEntries(1, tickets, true));
			assertEquals(5, dao.searchSaleTransaction(1).getEntries().get(0).getAmount());
			assertTrue(0 == dao.searchSaleTransaction(1).getEntries().get(1).getAmount());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


}
