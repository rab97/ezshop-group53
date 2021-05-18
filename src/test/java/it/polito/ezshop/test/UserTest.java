package it.polito.ezshop.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.polito.ezshop.data.User;
import it.polito.ezshop.model.ConcreteUser;

public class UserTest {
	
	User u = new ConcreteUser();
	
	@Test
	public void testUserSetId() {
		u.setId(0);
		assertEquals(Integer.valueOf(0), u.getId());
		u.setId(-21331);
		assertEquals(Integer.valueOf(-21331), u.getId());
		u.setId(21331);
		assertEquals(Integer.valueOf(21331), u.getId());
	}
	
	@Test
	public void testUserSetUsername() {
		u.setUsername(null);
		assertEquals(null, u.getUsername());
		u.setUsername("");
		assertEquals("", u.getUsername());
		u.setUsername("username");
		assertEquals("username", u.getUsername());
	}
	
	@Test
	public void testUserSetPassword() {
		u.setPassword(null);
		assertEquals(null, u.getPassword());
		u.setPassword("");
		assertEquals("", u.getPassword());
		u.setPassword("password");
		assertEquals("password", u.getPassword());
	}
	
	@Test
	public void testUserSetRole() {
		u.setRole(null);
		assertEquals(null, u.getRole());
		u.setRole("");
		assertEquals("", u.getRole());
		u.setRole("role");
		assertEquals("role", u.getRole());
	}


}
