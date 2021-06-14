package it.polito.ezshop.persistence;

import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.ReturnTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.data.User;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.Order;
import it.polito.ezshop.data.Product;
import it.polito.ezshop.data.TicketEntry;


public interface IDAOEZshop {

    public User searchUser(String username, String password) throws DAOException;

    public ArrayList<ProductType> getAllProducTypet() throws DAOException;
    public void createProductType(ProductType productType) throws DAOException;
    public ConcreteProductType getProductTypeByBarCode(String barCode) throws DAOException;
    public List<ProductType> getProductTypeByDescription(String description) throws DAOException;
    public boolean updateQuantity(Integer productId, int toBeAdded) throws DAOException;
    public void updatePosition(Integer productId, String position) throws DAOException;
    public boolean searchPosition(String position) throws DAOException;
    public boolean updateProduct(ProductType productType) throws DAOException;
    public boolean deleteProductType(Integer id) throws DAOException;

    public Integer insertUser(String username, String password, String role) throws DAOException;
    public boolean removeUser(Integer id) throws DAOException;
    public java.util.List<User> getAllUsers() throws DAOException;
    public User searchUserById(Integer id) throws DAOException;
    public boolean updateRights(Integer id, String role) throws DAOException;

    public Integer insertNewOrder(String productCode, int quantity, double pricePerUnit) throws DAOException;
    public Integer payOrderDirectly(String productCode, int quantity, double pricePerUnit) throws DAOException;
    public boolean payOrder(Integer orderId, double money) throws DAOException;
    public boolean recordArrival(Integer orderId) throws DAOException;
    public boolean recordProductArrivalRFID(Integer orderId, Integer orderQuantity, String RFIDfrom, String productCode) throws DAOException;
    public Order getOrder(Integer orderId) throws DAOException;
    public ArrayList<Order> getAllOrders() throws DAOException;

    public Integer insertCustomer(String customerName) throws DAOException;
    public boolean updateCustomer(Integer id, String newCustomerName, String newCustomerCard) throws DAOException;
    public boolean deleteCustomer(Integer id) throws DAOException;
    public Customer getCustomer(Integer id) throws DAOException;
    public ArrayList<Customer> getAllCustomers() throws DAOException;
    public boolean bindCardToCustomer(String card, Integer customerId) throws DAOException;
    public boolean updatePoints(String customerCard, int pointsToBeAdded) throws DAOException;
    
    
    public Integer insertReturnTransaction() throws DAOException;
    public ReturnTransaction searchReturnTransaction(Integer returnId) throws DAOException;
    public boolean deleteReturnTransaction(Integer returnId) throws DAOException;
    public boolean setReturnTransactionPaid(Integer returnId) throws DAOException;
    public List<TicketEntry> getReturnEntries(Integer returnId) throws DAOException;
    public boolean storeReturnTransaction(ReturnTransaction returnTransaction) throws DAOException;
    
    public boolean insertBalanceOperation(double amount, String type, LocalDate date) throws DAOException;    
    public List<BalanceOperation> getBalanceOperations(LocalDate from, LocalDate to) throws DAOException;

    public Integer insertSaleTransaction() throws DAOException;
    //public Integer insertSaleTransactionIntoDb() throws DAOException;
    public boolean storeSaleTransaction(SaleTransaction saleTransaction) throws DAOException;
    public SaleTransaction searchSaleTransaction(Integer transactionId) throws DAOException;
    public List<TicketEntry> getEntries(Integer transactionId) throws DAOException;
    public boolean setSaleTransactionPaid(Integer transactionId) throws DAOException;
    public boolean removeSaleTransaction(Integer saleNumber) throws DAOException;
    public boolean updateSaleTransactionPrice(Integer transactionId, double price, boolean committed) throws DAOException;
    public boolean updateSaleTransactionEntries (Integer transactionId, List<TicketEntry> returnEntries, boolean committed) throws DAOException;
    public TicketEntry searchTicketEntry (Integer transactionId, Integer productId) throws DAOException;

	public void resetApplication() throws DAOException;

	public boolean searchProductById(Integer productId) throws DAOException;

	public Product getProductByRFID(String RFID) throws DAOException;

    public boolean check_RFID_existance(String rfidRFIDFrom, Integer interval) throws DAOException;
    public List<Product> getSoldProducts(Integer transactionId) throws DAOException;

	void storeProduct(Product product) throws DAOException;

}
