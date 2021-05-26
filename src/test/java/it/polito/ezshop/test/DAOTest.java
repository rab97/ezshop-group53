package it.polito.ezshop.test;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;
import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;

import it.polito.ezshop.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

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
    
    @Test
    public void testInsertAndRemoveUser(){

        User testUser= new ConcreteUser("test1", null, "passwordTest", Constants.CASHIER);
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


}
