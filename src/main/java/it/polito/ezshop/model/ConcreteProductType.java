package it.polito.ezshop.model;

import it.polito.ezshop.data.ProductType;

public class ConcreteProductType implements ProductType{

	Integer id;
	String productDesription;
	String barCode;
	String note;
	Integer quantity;
	Double pricePerUnit;
	Double discountRate;
	//Position location;
	String location;
	
	public ConcreteProductType(Integer id, String productDescription, String barCode, String note, Integer quantity, Double pricePerUnit, Double discountRate, String location) {
		this.id = id;
		this.productDesription = productDescription;
		this.barCode = barCode;
		this.note = note;
		this.quantity = quantity;
		this.pricePerUnit = pricePerUnit;
		this.discountRate = discountRate;
		this.location = location;
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
		// TODO Auto-generated method stub
		return this.location;
	}
	@Override
	public void setLocation(String location) {
		// TODO Auto-generated method stub
		this.location = location;
	}
	@Override
	public String getNote() {
		return this.note;
	}
	@Override
	public void setNote(String note) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getProductDescription() {
		// TODO Auto-generated method stub
		return this.productDesription;
	}
	@Override
	public void setProductDescription(String productDescription) {
		this.productDesription = productDescription;
		
	}
	@Override
	public String getBarCode() {
		// TODO Auto-generated method stub
		return this.barCode;
	}
	@Override
	public void setBarCode(String barCode) {
		// TODO Auto-generated method stub
		this.barCode = barCode;
	}
	@Override
	public Double getPricePerUnit() {
		// TODO Auto-generated method stub
		return this.pricePerUnit;
	}
	@Override
	public void setPricePerUnit(Double pricePerUnit) {
		// TODO Auto-generated method stub
		this.pricePerUnit = pricePerUnit;
	}
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		this.id = id;
	}
	
	

}
