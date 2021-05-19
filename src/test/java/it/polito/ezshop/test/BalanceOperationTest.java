package it.polito.ezshop.test;


import static org.junit.Assert.*;
import java.time.LocalDate;
import org.junit.Test;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.model.ConcreteBalanceOperation;

public class BalanceOperationTest{
	
	BalanceOperation balanceOperation = new ConcreteBalanceOperation();;

	@Test
	public void testCostructor() {
		BalanceOperation bo = new ConcreteBalanceOperation(1, LocalDate.of(2021, 1, 8), 44.6, "type");
		assertEquals(1, bo.getBalanceId());
		assertEquals(LocalDate.of(2021, 1, 8), bo.getDate());
		assertTrue(44.6 == bo.getMoney());
		assertEquals("type", bo.getType());
	}
	
	@Test
	public void testSetAndGetBalanceId() {
		balanceOperation.setBalanceId(2);
		assertEquals(2, balanceOperation.getBalanceId());
		balanceOperation.setBalanceId(-30);
		assertEquals(-30, balanceOperation.getBalanceId());
		balanceOperation.setBalanceId(0);
		assertEquals(0, balanceOperation.getBalanceId());
	}
	
	@Test
	public void testSetAndGetDate() {
		balanceOperation.setDate(LocalDate.of(2021, 1, 8));
		assertEquals(LocalDate.of(2021, 1, 8), balanceOperation.getDate());
		balanceOperation.setDate(null);
		assertEquals(null, balanceOperation.getDate());
	}
	
	@Test
	public void testSetAndGetMoney() {
		balanceOperation.setMoney(0);
		assertTrue(balanceOperation.getMoney() == 0);
		balanceOperation.setMoney(40452.05);
		assertTrue(balanceOperation.getMoney() == 40452.05);
		balanceOperation.setMoney(-23.34);
		assertTrue(balanceOperation.getMoney() == -23.34);
	}
	
	@Test
	public void testSetAndGetType() {
		balanceOperation.setType("type");
		assertEquals("type", balanceOperation.getType());
		balanceOperation.setType("");
		assertEquals("", balanceOperation.getType());
		balanceOperation.setType(null);
		assertEquals(null, balanceOperation.getType());
	}
	
}

