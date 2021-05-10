package it.polito.ezshop.persistence;

import java.util.List;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.User;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.model.ConcreteUser;

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
            String query = "SELECT * FROM user where username= '" + username + "' AND password='" + password +"'";
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
    public void insertUser(String username, String password, String role, Integer id) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            String query = "INSERT INTO user(username, password, role, id) VALUES(?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, role);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex){
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public Integer getLastUserId() throws DAOException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT MAX(id) FROM user";
            resultSet = statement.executeQuery(query);
            return (resultSet.next() ? resultSet.getInt(1) : 0);
        } catch (SQLException ex){
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public boolean removeUser(Integer id) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            String query = "DELETE FROM user WHERE id='" + id +"'";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException ex){
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public List<User> getAllUsers() throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM user";
            resultSet = statement.executeQuery(query);
            List<User> users = new ArrayList<>();
            while(resultSet.next()) {
                User u = new ConcreteUser(resultSet.getString("username"), resultSet.getInt("id"), resultSet.getString("password"),  resultSet.getString("role"));
                users.add(u);
            }
            return users;
        } catch (SQLException ex){
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
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

    
		
	

	@Override
	public ConcreteProductType getProductTypeByBarCode(String barCode) throws DAOException {
		//TODO Auto-generated method stub
		Connection connection = null;
		Statement statment = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statment = connection.createStatement();
			String query = "select * from product_type where bar_code = '" + barCode + "';";
			resultSet = statment.executeQuery(query);
			ConcreteProductType productType = null;
			while(resultSet.next()) {
				Integer id = resultSet.getInt("id");
            	Integer quantity = resultSet.getInt("quantity");
            	String location = resultSet.getString("location");
            	String notes = resultSet.getString("note");
            	String description = resultSet.getString("description");
            	String bar_code = resultSet.getString("bar_code");
            	Double pricePerUnit = resultSet.getDouble("price_per_unit");
            	Double discountRate = resultSet.getDouble("discount_rate");
            	productType = new ConcreteProductType(id, description, bar_code, notes, quantity, pricePerUnit, discountRate, location);
			}
			return productType;
		}catch (SQLException e) {
			// TODO: handle exception
			System.out.println(e);
		} finally {
			dataSource.close(connection);
		}
		return null;
	}


    public Integer insertCustomer(String customerName) throws DAOException{

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
        	connection = dataSource.getConnection();
            statement = connection.createStatement();

            //Insert
            PreparedStatement pstm;

            pstm= connection.prepareStatement("insert into customer(name) values (?)");
            pstm.setString(1, customerName);
            pstm.execute();

            //Recover the id
            String query= "SELECT id FROM customer WHERE name= '" + customerName + "';";

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

    @Override
    public User searchUserById(Integer id) throws DAOException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
        	connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM user WHERE id= '" + id + "'";
            resultSet = statement.executeQuery(query);
            User u;
            if(!resultSet.next())
                u = null;
            u = new ConcreteUser(resultSet.getString("username"), resultSet.getInt("id"), resultSet.getString("password"), resultSet.getString("role"));
            return u;
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
        	dataSource.close(connection);
        }
    }
}
