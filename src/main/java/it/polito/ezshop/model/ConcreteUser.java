package it.polito.ezshop.model;

import it.polito.ezshop.data.User;

public class ConcreteUser implements User {

	private String username;
	private Integer id;
	private String password;
	private String role;

	public ConcreteUser() {

	}

	public ConcreteUser(String username, Integer id, String password, String role) {
		this.username = username;
		this.id = id;
		this.password = password;
		this.role = role;
	}

	public ConcreteUser(User user) {
		this.username = user.getUsername();
		this.id = user.getId();
		this.password = user.getPassword();
		this.role = user.getRole();
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;

	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getRole() {
		return this.role;
	}

	@Override
	public void setRole(String role) {
		this.role = role;
	}

}
