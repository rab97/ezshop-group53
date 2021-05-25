package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestMethodOrder;

import it.polito.ezshop.Constants;
import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidCreditCardException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
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
	@Before	
	public void setUp () {
		ezShop = new EZShop();
		dao = new DAOEZShop();
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
		//System.out.println(r.getPrice());
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
			assertTrue(ezShop.returnProduct(1, "4314324224124", 1));
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
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
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
	
	/*@Test
	public void testDeleteReturnTransactionValid(){
		User user = new ConcreteUser("name", 1, "123", Constants.SHOP_MANAGER);;
		ezShop.setRunningUser(user);
		dao.resetApplication();
		TicketEntry t1 = new ConcreteTicketEntry("123456789104","", 25, 0.5, 0.0);
		TicketEntry t2 = new ConcreteTicketEntry("4314324224124","", 1, 32.0, 0.0);
		List<TicketEntry> tickets = new ArrayList<>();
		tickets.add(t1);
		tickets.add(t2);
		
		ReturnTransaction r1 = new ConcreteReturnTransaction();
		r1.setReturnId(1);
		r1.setDiscountRate(0);
		r1.setPrice(5.21);
		r1.setPayed(false);
		r1.setEntries(tickets);
		r1.setTransactionId(2);
		ReturnTransaction r2 = new ConcreteReturnTransaction();
		r2.setReturnId(2);
		r2.setDiscountRate(0);
		r2.setPrice(10.4);
		r2.setPayed(true);
		r2.setEntries(tickets);
		r2.setTransactionId(1);
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
			dao.updateQuantity(3, 150);
			dao.storeReturnTransaction(r1);
			dao.storeReturnTransaction(r2);
			dao.storeSaleTransaction(s1);
			dao.storeSaleTransaction(s2);
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
	}*/
	
	@Test
	public void testReturnCashPaymentInvalidReutrnId() {
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
	}
	
	/*@Test
	public void testReturnCashPaymentReturnTransactionEndedAndNotPayed() {
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
		ezShop.setRunningUser(user);
		ReturnTransaction r = new ConcreteReturnTransaction();
		ezShop.setReturnTransaction(r);
		try {
			assertTrue(5.21 == ezShop.returnCashPayment(1));
		} catch (Exception e) {
			fail();
		}
	}*/

	
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
			assertTrue(-1 == ezShop.returnCashPayment(2));
		
		} catch (Exception e) {
			System.out.println("apjdfppjfaepièfdaèièadf" + e);
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
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
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
		User user = new ConcreteUser("name", 1, "123", Constants.CASHIER);
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
	
}
