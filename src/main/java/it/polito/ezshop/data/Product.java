package it.polito.ezshop.data;

public interface Product {
	
	ProductType getProductType();
	
	void setProductType(ProductType productType);
	
	String getRFID();
	
	void setRFID(String RFID);
}
