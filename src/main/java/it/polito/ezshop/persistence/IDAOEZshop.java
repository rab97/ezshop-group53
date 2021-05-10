package it.polito.ezshop.persistence;

import java.awt.List;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.User;
import it.polito.ezshop.model.ConcreteProductType;

public interface IDAOEZshop {

    public User searchUser(String username, String password) throws DAOException;
    
    public ArrayList<ProductType> getAllProducTypet()  throws DAOException;
    public void  createProductType(ProductType productType)  throws DAOException;
    
    public ConcreteProductType getProductTypeByBarCode(String barCode) throws DAOException;
    public void insertUser(String username, String password, String role, Integer id) throws DAOException;

    public Integer getLastUserId() throws DAOException;

    public boolean removeUser(Integer id) throws DAOException;

    public java.util.List<User> getAllUsers() throws DAOException;

    public Integer insertCustomer(String customerName) throws DAOException;

    public User searchUserById(Integer id) throws DAOException;


}
