package it.polito.ezshop.data;

import java.util.List;

public interface SaleTransaction {

    Integer getTicketNumber();

    void setTicketNumber(Integer ticketNumber);

    List<TicketEntry> getEntries();

    void setEntries(List<TicketEntry> entries);

    double getDiscountRate();

    void setDiscountRate(double discountRate);

    double getPrice();

    void setPrice(double price);
    
    public boolean getPayed();
    
    public void setPayed(boolean payed);

	List<Product> getSaleProducts();

	void setSaleProducts(List<Product> saleProducts);

}
