package it.polito.ezshop.model;

import it.polito.ezshop.data.ProductType;

public class ConcreteProductType implements ProductType {
	Integer id;
	String productDescription;
	String barCode;
	String note;
	Integer quantity;
	Double pricePerUnit;
	// Position location;
	String location;

	public ConcreteProductType(Integer id, String productDescription, String barCode, String note, Integer quantity,
			Double pricePerUnit, String location) {
		this.id = id;
		this.productDescription = productDescription;
		this.barCode = barCode;
		this.note = note;
		this.quantity = quantity;
		this.pricePerUnit = pricePerUnit;
		this.location = location;
	}

	public ConcreteProductType() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Integer getQuantity() {
		return this.quantity;
	}

	@Override
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String getLocation() {
		return this.location;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String getNote() {
		return this.note;
	}

	@Override
	public void setNote(String note) {
		this.note = note;

	}

	@Override
	public String getProductDescription() {
		return this.productDescription;
	}

	@Override
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;

	}

	@Override
	public String getBarCode() {
		return this.barCode;
	}

	@Override
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	@Override
	public Double getPricePerUnit() {
		return this.pricePerUnit;
	}

	@Override
	public void setPricePerUnit(Double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
}
