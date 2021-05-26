package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.ConcreteReturnTransaction;
import it.polito.ezshop.model.ConcreteSaleTransaction;
import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;

public class DAOEZShopTest {

	IDAOEZshop dao = null;;
	
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
	public void insertReturnTransactionTest() {
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
