package it.polito.ezshop.data;

public interface Product {
	
	
	String getRFID();
	
	void setRFID(String RFID);
	
	public Integer getTransactionId();
	public void setTransactionId(int transactioId);

	String getBarCode();

	void setBarCode(String productType);
}
