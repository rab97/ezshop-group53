package it.polito.ezshop.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.ezshop.data.Product;
import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.TicketEntry;

public class ConcreteReturnTransaction implements ReturnTransaction {
	private Integer returnId;
	private Integer transactionId;
	private List<TicketEntry> entries;
	private double price;
	private double discountRate;
	private boolean payed;
	private List<Product> returnProducts = new ArrayList<Product>();
	
	public ConcreteReturnTransaction(){
		
	}
	
	public ConcreteReturnTransaction(Integer returnId, Integer transactionId, List<TicketEntry> entries, double price, double discountRate) {
		this.returnId=returnId;
		this.transactionId=transactionId;
		this.entries=entries;
		this.price=price;
		this.discountRate=discountRate;
		this.payed=false;
	}

	@Override
	public Integer getReturnId() {
		return returnId;
	}

	@Override
	public void setReturnId(Integer returnId) {
		this.returnId=returnId;
	}

	@Override
	public Integer getTransactionId() {
		return transactionId;
	}

	@Override
	public void setTransactionId(Integer transactionId) {
		this.transactionId=transactionId;
	}

	@Override
	public List<TicketEntry> getEntries() {
		return entries;
	}

	@Override
	public void setEntries(List<TicketEntry> entries) {
		this.entries=entries;	
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public void setPrice(double price) {
		this.price=price;
	}
	
	@Override
	public boolean getPayed() {
		return payed;
	}
	
	@Override
	public void setPayed(boolean payed) {
		this.payed=payed;
	}
	
	@Override
	public double getDiscountRate() {
		return discountRate;
	}

	@Override
    public void setDiscountRate(double discountRate) {
		this.discountRate=discountRate;
	}
	
	@Override
    public List<Product> getReturnProducts() {
        return returnProducts;
    }

	@Override
    public void setReturnProducts(List<Product> returnProducts) {
        this.returnProducts = returnProducts;
    }

}
