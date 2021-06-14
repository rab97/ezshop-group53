package it.polito.ezshop.model;

import it.polito.ezshop.data.Product;
import it.polito.ezshop.data.ProductType;

public class ConcreteProduct implements Product {
	private String RFID;
	private String barCode;
	private Integer transactionId;
	
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
	public String getBarCode() {
		return this.barCode;
	}

	@Override
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	
	@Override
	public Integer getTransactionId() {
		return this.transactionId;
	}
	
	@Override
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	
}
