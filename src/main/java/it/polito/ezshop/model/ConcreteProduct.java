package it.polito.ezshop.model;

import it.polito.ezshop.data.Product;
import it.polito.ezshop.data.ProductType;

public class ConcreteProduct implements Product {
	private String RFID;
	private ProductType productType;
	
	public ConcreteProduct() {
		
	}
	
	@Override
	public String getRFID() {
		return this.RFID;
	}
	
	@Override
	public void setRFID(String RFID) {
		this.RFID = RFID;
	}

	@Override
	public ProductType getProductType() {
		return this.productType;
	}

	@Override
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	
}
