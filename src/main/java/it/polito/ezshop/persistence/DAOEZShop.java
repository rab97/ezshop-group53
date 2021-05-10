package it.polito.ezshop.persistence;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.User;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.model.ConcreteUser;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUsernameException;

public class DAOEZShop  implements IDAOEZshop {
	
	private DataSource dataSource = new DataSource();
	
	@Override
    public User searchUser(String username, String password) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
        	connection = dataSource.getConnection();
            statement = connection.createStatement();
            //logger.debug(query.toString());
            
            /*StringBuilder query = new StringBuilder();
            query.append("insert into user values (");
            query.append("'" + username + "',");
            query.append("'" + password + "');");
            //String  query="SELECT * FROM user";*/
            String query = "SELECT * FROM user where username= '" + username + "'";
            //statement.executeUpdate(query.toString());
            resultSet = statement.executeQuery(query);
            User user = null;
            while(resultSet.next()){
            	user = new ConcreteUser();
            	String name = resultSet.getString("username");
            	String pass = resultSet.getString("password");
            	String role = resultSet.getString("role");
            	Integer id= resultSet.getInt("id");
            	user.setUsername(name);
            	user.setPassword(pass);
            	user.setId(id);
            	user.setRole(role);
            }

            return user;
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
        	dataSource.close(connection);
        }
    }

	@Override
	public ArrayList<ProductType> getAllProducTypet() throws DAOException{
		Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
        	connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM product_type";
            resultSet = statement.executeQuery(query);
            ArrayList<ProductType> productTypeList = new ArrayList<>();
            while(resultSet.next()){
            	Integer id = resultSet.getInt("id");
            	Integer quantity = resultSet.getInt("quantity");
            	String location = resultSet.getString("location");
            	String notes = resultSet.getString("note");
            	String description = resultSet.getString("description");
            	String barCode = resultSet.getString("bar_code");
            	Double pricePerUnit = resultSet.getDouble("price_per_unit");
            	Double discountRate = resultSet.getDouble("discount_rate");
            	ProductType product = new ConcreteProductType(id, description, barCode, notes, quantity, pricePerUnit, discountRate, location);
            	productTypeList.add(product);
            }
            return productTypeList;
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
        	dataSource.close(connection);
        }
	}

	@Override
	public void createProductType(ProductType productType) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
        	connection = dataSource.getConnection();
            statement = connection.createStatement();
            StringBuilder query = new StringBuilder();
            query.append("insert into product_type (description, note, bar_code, price_per_unit) values (");
            query.append("'" + productType.getProductDescription() + "','");
            query.append(productType.getNote() + "','");
            query.append(productType.getBarCode() + "','");
            query.append(productType.getPricePerUnit() + "');");
            statement.executeUpdate(query.toString());
            String q = "select id from product_type where bar_code = '" + productType.getBarCode() + "'";
            resultSet = statement.executeQuery(q);
            int id = resultSet.getInt("id");
            productType.setId(id);
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
        	dataSource.close(connection);
        }
    }

    public Integer insertCustomer(String customerName) throws DAOException{

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
        	connection = dataSource.getConnection();
            statement = connection.createStatement();

            System.out.println("dentro dao prima della prima query");

            //Insert
            PreparedStatement pstm;

            pstm= connection.prepareStatement("insert into customer(name) values (?)");
            pstm.setString(1, customerName);
            pstm.execute();

            System.out.println("query di insert andata a buon fine");

            //Recover the id
            String query= "SELECT id FROM customer WHERE name= '" + customerName + "';";
            System.out.println(query);

            resultSet= statement.executeQuery(query);   
            System.out.println("id: " + resultSet.getString("id"));         
            Integer id = resultSet.getInt("id");
            
            return id;

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
        	dataSource.close(connection);
        }
    }
    
}
