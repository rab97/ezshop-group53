package it.polito.ezshop.model;

import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

import it.polito.ezshop.data.TicketEntry;

public class ConcreteTicketEntry implements TicketEntry {
    String barCode;
    String productDescription;
    int amount;
    double pricePerUnit;
    double discountRate;

    public ConcreteTicketEntry(){}

    public ConcreteTicketEntry(String barCode, String productDescription, int amount, double pricePerUnit,
            double discountRate) {
        this.barCode = barCode;
        this.productDescription = productDescription;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discountRate;
    }

    @Override
    public String getBarCode() {
        return barCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public String getProductDescription() {
        return productDescription;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public double getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
