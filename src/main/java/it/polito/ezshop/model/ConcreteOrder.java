package it.polito.ezshop.model;

import it.polito.ezshop.data.Order;
import java.time.LocalDate;
import java.util.Date;

public class ConcreteOrder implements Order {

    Integer balanceId;
    String productCode;
    double pricePerUnit;
    int quantity;
    String status;
    Integer orderId;

    public ConcreteOrder(Integer balanceId, String productCode, double pricePerUnit, int quantity, String status, Integer orderId) {
        
        //Initializing ConcreteOrder attributes
        this.balanceId= balanceId;
        this.productCode = productCode;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.status = status;
        this.orderId = orderId;
    }

    public ConcreteOrder() {
    	
	}

	@Override
    public Integer getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public String getProductCode() {
        return productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode = productCode;
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
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Integer getOrderId() {
        return orderId;
    }

    @Override
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
