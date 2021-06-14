package it.polito.ezshop.model;

public class Product {

	String rfid;
	int saleTransactionId;
	
	public String getRfid () {
		return this.rfid;
	}
	
	public void setRfid (String rfid) {
		this.rfid = rfid;
	}
	
	public int getSaleTransactionId() {
		return this.saleTransactionId;
	}
	
	public void setSaleTransactionId (int saleTransactioId) {
		this.saleTransactionId = saleTransactioId;
	}
}
