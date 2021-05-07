package it.polito.ezshop.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.polito.ezshop.data.User;
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
            	String pass = resultSet .getString("password");
            	String role = resultSet .getString("role");
            	Integer id= resultSet.getInt("id");
            	user.setUsername(username);
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
}
