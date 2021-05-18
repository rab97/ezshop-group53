package it.polito.ezshop.model;

import it.polito.ezshop.data.Customer;

public class ConcreteCustomer implements Customer {
    Integer id;
    String customerName;
    String customerCard;
    Integer points;
    // Card customerCard or String customerCard;

    public ConcreteCustomer() {
     
    }
    
    public ConcreteCustomer(Integer id, String customerName, String customerCard, Integer points) {
        this.id = id;
        this.customerName = customerName;
        this.customerCard = customerCard;
        this.points = points;
    }

    @Override
    public String getCustomerName() {
        return customerName;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String getCustomerCard() {
        // return customerCard.getCustomerCard();
        return customerCard;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.customerCard = customerCard;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points = points;
    }

}
