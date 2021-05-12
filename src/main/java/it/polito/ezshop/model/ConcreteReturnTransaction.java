package it.polito.ezshop.model;

import java.util.List;

import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.TicketEntry;

public class ConcreteReturnTransaction implements ReturnTransaction {
	private Integer returnId;
	private Integer transactionId;
	private List<TicketEntry> entries;
	private double price;
	
	public ConcreteReturnTransaction(Integer returnId, Integer transactionId, List<TicketEntry> entries, double price) {
		this.returnId=returnId;
		this.transactionId=transactionId;
		this.entries=entries;
		this.price=price;
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

}
