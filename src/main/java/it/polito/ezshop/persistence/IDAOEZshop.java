package it.polito.ezshop.persistence;

import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.User;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.data.Customer;

public interface IDAOEZshop {

    public User searchUser(String username, String password) throws DAOException;

    public ArrayList<ProductType> getAllProducTypet() throws DAOException;

    public void createProductType(ProductType productType) throws DAOException;

    public ConcreteProductType getProductTypeByBarCode(String barCode) throws DAOException;
<<<<<<< HEAD
    public List<ProductType> getProductTypeByDescription(String description) throws DAOException;
    public boolean updateQuantity(Integer productId, int toBeAdded) throws DAOException;
    public void updatePosition(Integer productId, String position) throws DAOException;
    public boolean searchPosition(String position) throws DAOException;
    
=======

>>>>>>> 8517e3d7d8cf3dfb52680e928539862378e16290
    public void insertUser(String username, String password, String role, Integer id) throws DAOException;

    public Integer getLastUserId() throws DAOException;

    public boolean removeUser(Integer id) throws DAOException;

    public java.util.List<User> getAllUsers() throws DAOException;

    public User searchUserById(Integer id) throws DAOException;

    public boolean updateRights(Integer id, String role) throws DAOException;

    public Integer insertCustomer(String customerName) throws DAOException;
    public boolean updateCustomer(Integer id, String newCustomerName, String newCustomerCard) throws DAOException;
    public boolean deleteCustomer(Integer id) throws DAOException;
    public Customer getCustomer(Integer id) throws DAOException;
    public ArrayList<Customer> getAllCustomers() throws DAOException;

}
