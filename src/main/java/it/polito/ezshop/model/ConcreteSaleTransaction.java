package it.polito.ezshop.model;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;

public class ConcreteSaleTransaction implements SaleTransaction {
    Integer ticketNumber;
    List<TicketEntry> entries;
    double discountRate;
    double price;

    public ConcreteSaleTransaction(Integer ticketNumber, List<TicketEntry> entries, double discountRate, double price) {
        this.ticketNumber = ticketNumber;
        this.entries = entries;
        this.discountRate = discountRate;
        this.price = price;
    }

    @Override
    public Integer getTicketNumber() {
        return ticketNumber;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries() {
        return entries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries = entries;
    }

    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

}
