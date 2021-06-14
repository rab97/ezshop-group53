package it.polito.ezshop.persistence;

import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import it.polito.ezshop.data.*;
import it.polito.ezshop.data.Product;
import it.polito.ezshop.model.*;

public class DAOEZShop implements IDAOEZshop {

    private DataSource dataSource = new DataSource();

    @Override
    public User searchUser(String username, String password) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        User user = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM user where username= '" + username + "' AND password='" + password + "'";

            resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                user = new ConcreteUser();
                String name = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String role = resultSet.getString("role");
                Integer id = resultSet.getInt("id");
                user.setUsername(name);
                user.setPassword(pass);
                user.setId(id);
                user.setRole(role);
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        
        return user;
    }

    @Override
    public ArrayList<ProductType> getAllProducTypet() throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM product_type";
            resultSet = statement.executeQuery(query);
            ArrayList<ProductType> productTypeList = new ArrayList<>();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer quantity = resultSet.getInt("quantity");
                String location = resultSet.getString("location");
                String notes = resultSet.getString("note");
                String description = resultSet.getString("description");
                String barCode = resultSet.getString("bar_code");
                Double pricePerUnit = resultSet.getDouble("price_per_unit");
                ProductType product = new ConcreteProductType(id, description, barCode, notes, quantity, pricePerUnit,
                        location);
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
    public Integer insertUser(String username, String password, String role) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer id = -1;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM user WHERE username= '" + username + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return -1;

            query = "INSERT INTO user(username, password, role) VALUES(?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, role);

            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next())
                id = resultSet.getInt(1);
            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        
        return id;
    }

    @Override
    public boolean removeUser(Integer id) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean state = false;
        try {
            connection = dataSource.getConnection();
            String query = "DELETE FROM user WHERE id='" + id + "'";
            preparedStatement = connection.prepareStatement(query);
            if(preparedStatement.executeUpdate() > 0)
            	state = true;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        
        return state;
    }

    @Override
    public List<User> getAllUsers() throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM user";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                User u = new ConcreteUser(resultSet.getString("username"), resultSet.getInt("id"),
                        resultSet.getString("password"), resultSet.getString("role"));
                users.add(u);
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return users;
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
        Connection connection = null;
        Statement statment = null;
        ResultSet resultSet = null;
        ConcreteProductType pt = null;
        try {
            connection = dataSource.getConnection();
            statment = connection.createStatement();
            String query = "select * from product_type where bar_code = '" + barCode + "';";
            resultSet = statment.executeQuery(query);
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer quantity = resultSet.getInt("quantity");
                String location = resultSet.getString("location");
                String notes = resultSet.getString("note");
                String description = resultSet.getString("description");
                String bar_code = resultSet.getString("bar_code");
                Double pricePerUnit = resultSet.getDouble("price_per_unit");
                pt = new ConcreteProductType(id, description, bar_code, notes, quantity, pricePerUnit,
                        location);
            }
        } catch (SQLException ex) {
        	throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return pt;
    }

    @Override
    public User searchUserById(Integer id) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        User u = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM user WHERE id= '" + id + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                u = new ConcreteUser(resultSet.getString("username"), resultSet.getInt("id"),
                        resultSet.getString("password"), resultSet.getString("role"));
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        
        return u;
    }

    @Override
    public boolean updateRights(Integer id, String role) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean state = false;
        try {
            connection = dataSource.getConnection();
            String query = "UPDATE user SET role= ? WHERE id= ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, role);
            preparedStatement.setInt(2, id);
            if(preparedStatement.executeUpdate() > 0){
            	state = true;
            }else{
                return false;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return state;
    }

    @Override
    public Integer insertNewOrder(String productCode, int quantity, double pricePerUnit) throws DAOException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer id = -1;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            // Check if the product exists
            String query = "SELECT * FROM product_type WHERE bar_code= '" + productCode + "';";
            resultSet = statement.executeQuery(query);

            if (!resultSet.next()) {
                System.out.println("The selected product type doesn't exist");
                return -1;
            }

            // Insert
            PreparedStatement pstm;

            pstm = connection.prepareStatement("INSERT INTO 'order' (product_code, price_per_unit, quantity, status) values (?,?,?,?)");
            pstm.setString(1, productCode);
            pstm.setDouble(2, pricePerUnit);
            pstm.setInt(3, quantity);
            pstm.setString(4, "ISSUED");
            pstm.execute();

            // Recover the id
            ResultSet rs = pstm.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }

            System.out.println("last inserted id: " + id);

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return id;
    }

    @Override
    public Integer payOrderDirectly(String productCode, int quantity, double pricePerUnit) throws DAOException {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            // Check if the product exists
            String query = "SELECT * FROM product_type WHERE bar_code= '" + productCode + "';";
            ResultSet rs = statement.executeQuery(query);

            if (!rs.next()) {
                System.out.println("The selected product type doesn't exist");
                return -1;
            }

            // Insert of a BalanceOperation
            PreparedStatement prstm = connection.prepareStatement("INSERT INTO balance_operation (date, money, type) VALUES (?,?,?);");

            prstm.setString(1, LocalDate.now().toString());
            prstm.setDouble(2, pricePerUnit * quantity);
            prstm.setString(3, "DEBIT");

            prstm.execute();
            rs = prstm.getGeneratedKeys();

            if (!rs.next()) {
                return -1;
            }

            // Insert of an Order
            prstm = connection.prepareStatement(
                    "INSERT INTO 'order'(balance_id, product_code, price_per_unit, quantity, status) values (?,?,?,?,?)");
            prstm.setInt(1, rs.getInt(1));
            prstm.setString(2, productCode);
            prstm.setDouble(3, pricePerUnit);
            prstm.setInt(4, quantity);
            prstm.setString(5, "PAYED");
            prstm.execute();

            // return the generated id
            rs = prstm.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

    }

    @Override
    public boolean payOrder(Integer orderId, double money) throws DAOException {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM 'order' WHERE id= '" + orderId + "';";
            ResultSet rs = statement.executeQuery(query);

            if (!rs.next()) { // The are no orders with given id
                System.out.println("entro qui? ");

                return false;
            }

            String orderStatus = rs.getString("status");
            System.out.println("Status= " + orderStatus);
            if (orderStatus.equals("PAYED")) { // It doesn't need a modification
                return true;
            }
            if (orderStatus.equals("ISSUED")==false && orderStatus.equals("ORDERED")==false) { // Not valid status
                return false;
            }

            ConcreteOrder order = new ConcreteOrder(rs.getInt("balance_id"), rs.getString("product_code"),
                    rs.getDouble("price_per_unit"), rs.getInt("quantity"), orderStatus, rs.getInt("id"));
            if(money <= order.getPricePerUnit() * order.getQuantity())
            	return false;

            // Insert BalanceOperation
            PreparedStatement prstm = connection.prepareStatement("INSERT INTO balance_operation (date, money, type) VALUES (?,?,?);");
            
            prstm.setString(1, LocalDate.now().toString());
            prstm.setDouble(2, order.getPricePerUnit() * order.getQuantity());
            prstm.setString(3, "DEBIT");

            prstm.execute();
            rs = prstm.getGeneratedKeys();

            // Update Order
            //query = "UPDATE 'order' SET balance_id= '" + rs.getInt(1) + "' , status = 'PAYED' WHERE id= '" + orderId + "';";

            prstm = connection.prepareStatement("UPDATE 'order' SET balance_id= ?, status = 'PAYED' WHERE id= ?;");
            prstm.setInt(1, rs.getInt(1));
            prstm.setInt(2, orderId);

            int update = prstm.executeUpdate();
            //System.out.println("update= "+ update);
            if (update!=1) {
                return false;
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;

    }

    @Override
    public boolean recordArrival(Integer orderId) throws DAOException{

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query= "UPDATE 'order' SET status = 'COMPLETED' WHERE id = '" + orderId + "';";

            int update = statement.executeUpdate(query);
            System.out.println("update variable= "+ update);
            if(update!=1){
                return false;
            
            }else{
                return true;
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

    }


    @Override

    public boolean recordProductArrivalRFID(Integer orderId, Integer orderQuantity, String RFIDfrom, String productCode) throws DAOException{

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            
            Long parsedRFID= Long.valueOf(RFIDfrom);
            System.out.print("dao: recordProductArrival: parsedRFID= " + parsedRFID);
            String baseRFID= "";

            for(Long i=parsedRFID; i<(parsedRFID+orderQuantity); i++){

                //Built the correct base RFID

                int length = String.valueOf(i).length();
                if(length<12){  //Create a string with all 0
                
                    for(int j=0; j<(12-length); j++){
                         baseRFID= baseRFID+ "0";
                    }
                 }
                String newRFID= baseRFID + i;
                System.out.println("dao: recordProductArrival: newRFID= " + newRFID);


                //Insert into db
                String query= "INSERT INTO product (rfid, bar_code) VALUES ('" + newRFID +"', '"+  productCode +"');";
                int update = statement.executeUpdate(query);

                if(update!=1){
                    return false;
                }
                baseRFID= "";
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }                

        return true;

    }



    @Override
    public Order getOrder(Integer orderId) throws DAOException{

        Connection connection = null;
        Statement statement = null;
        Order order = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM 'order' WHERE id = '" + orderId + "';";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                order = new ConcreteOrder(rs.getInt("balance_id"), rs.getString("product_code"), rs.getDouble("price_per_unit"), 
                    rs.getInt("quantity"), rs.getString("status"), rs.getInt("id"));
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return order;
    }


    @Override
    public ArrayList<Order> getAllOrders() throws DAOException {

        Connection connection = null;
        Statement statement = null;
        ArrayList<Order> orders = new ArrayList<>();

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM 'order'";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
            	
                Order o = new ConcreteOrder(resultSet.getInt("balance_id"), resultSet.getString("product_code"),
                          resultSet.getDouble("price_per_unit"), resultSet.getInt("quantity"),resultSet.getString("status"), resultSet.getInt("id"));
                orders.add(o);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return orders;
    }

    @Override
    public Integer insertCustomer(String customerName) throws DAOException {

        Connection connection = null;
        Statement stm= null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            stm= connection.createStatement();

            //Check if the customer already exists
            String query= "SELECT * FROM customer WHERE name= '" + customerName + "';";
            resultSet= stm.executeQuery(query);

            if(resultSet.next()){
                return -1;
            }

            // Insert
            PreparedStatement pstm;

            pstm = connection.prepareStatement("INSERT INTO customer(name) VALUES (?)");
            pstm.setString(1, customerName);
            pstm.execute();

            // Recover the id
            resultSet= pstm.getGeneratedKeys();
            Integer id= resultSet.getInt(1);

            return id;

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public boolean updateCustomer(Integer id, String newCustomerName, String newCustomerCard) throws DAOException {

        Connection connection = null;
        Statement statement = null;
        int update;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            // update query creation
            String query = "UPDATE customer SET name= '" + newCustomerName + "'";

            if (newCustomerCard.isEmpty()) { // remove previous card and its points
                query = query + ", card= '" + null + "', points= '" + 0 + "'";

            } else if (newCustomerCard != null) { // numeric value: create new card with 0 points

                if(newCustomerCard.length()!=10){
                    return false;
                }
                
              //Check if the card already exists
                String query2= "SELECT * FROM customer WHERE card= '" + newCustomerCard + "';";
                ResultSet resultSet= statement.executeQuery(query2);

                if(resultSet.next()){
                    return false; 
                }
                
                query = query + ", card= '" + newCustomerCard + "', points= '" + 0 + "'";
            }

            query = query + "WHERE customer.id= '" + id + "';";

            //update execution
            update = statement.executeUpdate(query);

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        if (update != 1) { // something goes wrong
            return false;
        }

        // exactly one row was affected by the update
        return true;

    }

    @Override
    public boolean deleteCustomer(Integer id) throws DAOException {

        Connection connection = null;
        PreparedStatement prstm = null;
        boolean result = false;

        try {
            connection = dataSource.getConnection();
            prstm = connection.prepareStatement("DELETE FROM customer WHERE id=?;");
            prstm.setInt(1, id);

            int del = prstm.executeUpdate();
            
            System.out.println(del);
            
            if (del != 1) { // Something goes wrong
                result = false;
            } else {
                result = true;
            }            

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return result;

    }

    @Override
    public Customer getCustomer(Integer id) throws DAOException {

        Connection connection = null;
        Statement statement = null;
        Customer c = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM customer WHERE id = '" + id + "';";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                c = new ConcreteCustomer(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("card"), resultSet.getInt("points"));
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return c;
    }

    @Override
    public ArrayList<Customer> getAllCustomers() throws DAOException {

        Connection connection = null;
        Statement statement = null;
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM customer";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Customer c = new ConcreteCustomer(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("card"), resultSet.getInt("points"));
                customers.add(c);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return customers;
    }

    @Override
    public List<ProductType> getProductTypeByDescription(String description) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<ProductType> productTypeList = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "select * from product_type where description LIKE '%" + description + "%';";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer quantity = resultSet.getInt("quantity");
                String location = resultSet.getString("location");
                String notes = resultSet.getString("note");
                String desc = resultSet.getString("description");
                String bar_code = resultSet.getString("bar_code");
                Double pricePerUnit = resultSet.getDouble("price_per_unit");
                ProductType productType = new ConcreteProductType(id, desc, bar_code, notes, quantity, pricePerUnit,
                        location);
                productTypeList.add(productType);
            }
        } catch (SQLException ex) {
        	throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return productTypeList;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {

            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "select quantity, location from product_type where id = " + productId;
            resultSet = statement.executeQuery(query);
            int quantity = 0;
            String location = null;
            if (resultSet.next()) {
                quantity = resultSet.getInt("quantity");
                location = resultSet.getString("location");
            }
            int value = quantity + toBeAdded;
            if (value < 0 || location == null || location.isEmpty()) {
                return false;
            }
            query = "update product_type  set quantity = " + value + " where id = " + productId;
            statement.executeUpdate(query.toString());
            return true;
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public Integer insertSaleTransaction() throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer id = -1;
        try {
            connection = dataSource.getConnection();
            String query = "SELECT MAX(id) FROM sale_transaction";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            id = resultSet.next() ? resultSet.getInt(1) : 1;
            
            System.out.println(id);

        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return id;
    }
    
    @Override
    public Integer insertReturnTransaction() throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer id = -1;
        try {
            connection = dataSource.getConnection();
            String query = "SELECT MAX(id) FROM return_transaction";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            
            //System.out.println("query ok and return_transaction created with id: " + id);

            id = resultSet.next() ? resultSet.getInt(1) : 1;
            
            System.out.println("id" + id);
            //System.out.println("query ok and return_transaction created with id: " + id);
            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return id;
    }
    
    
    public boolean deleteReturnTransaction(Integer returnId) throws DAOException {
    	
    	Connection connection = null;

        try {
            connection = dataSource.getConnection();

            // Search for return transaction
            String query = "DELETE FROM return_transaction WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, returnId);
            if (pstm.executeUpdate() == 0) {
            	System.out.println("TRANSACTION NOT FOUND");
                return false;
            }
            
            // Delete product entry from return_ticket_entry
            query = "DELETE FROM return_ticket_entry WHERE returnId=?";
            pstm = connection.prepareStatement(query);
            pstm.setInt(1, returnId);
            if(pstm.executeUpdate() <= 0) {
            	System.out.println("ENTRIES NOT FOUND");
            	return false;
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;
    }
    
    
    public ReturnTransaction searchReturnTransaction(Integer returnId) throws DAOException {
    	Connection connection = null;
        Statement statment = null;
        ResultSet resultSet = null;
        ReturnTransaction returnTransaction;
        try {
            connection = dataSource.getConnection();
            statment = connection.createStatement();
            String query = "select * from return_transaction where id = '" + returnId + "';";
            resultSet = statment.executeQuery(query);
            
            if (!resultSet.next()) {
            	return null;
            }

            List<TicketEntry> entries = getReturnEntries(returnId);         
            returnTransaction = new ConcreteReturnTransaction(returnId, -1, entries, resultSet.getDouble("amount"), resultSet.getDouble("discountRate"));
            
            
            System.out.println(returnId);
            System.out.println(resultSet.getInt("payed"));
            if(resultSet.getInt("payed") == 1) {
            	returnTransaction.setPayed(true);
            } else {
            	returnTransaction.setPayed(false);
            }

            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return returnTransaction;
    }


    @Override
    public boolean bindCardToCustomer(String card, Integer customerId) throws DAOException {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM customer WHERE id= '" + customerId + "' OR card= '" + card + "';";
            ResultSet rs = statement.executeQuery(query);

            boolean customerExistance = false;
            while (rs.next()) { // check all the rows to find a non-existing user or an already assigned card

                if (rs.getInt("id") == customerId) {
                    customerExistance = true;
                }
                if (rs.getString("card")!=null && rs.getString("card").equals(card)) {
                    System.out.println("This card is already attached to a customer");
                    return false;
                }
            }

            if (customerExistance != true) {
                System.out.println("The given customer doesn't exist");   
                return false;
            }

            query = "UPDATE customer SET card= '" + card + "' WHERE id= '" + customerId + "';";
            statement.executeUpdate(query);

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;
    }

    @Override
    public void updatePosition(Integer productId, String position) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "update product_type set location = '" + position + "' where id = '" + productId + "'";
            statement.executeUpdate(query);
        } catch (SQLException ex) {
        	throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public boolean searchPosition(String position) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean state = false;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "select * from product_type where location = '" + position + "'";
            resultSet = statement.executeQuery(query);
            if(resultSet.next())
            	state = true;
        } catch (SQLException ex) {
        	throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return state;
    }

    @Override
    public boolean updateProduct(ProductType productType) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            StringBuilder query = new StringBuilder();
            query.append("update product_type set ");
            query.append("description ='" + productType.getProductDescription() + "',");
            query.append("note = '" + productType.getNote() + "', ");
            query.append("bar_code = '" + productType.getBarCode() + "', ");
            query.append("price_per_unit = '" + productType.getPricePerUnit() + "' ");
            query.append("where id = '").append(productType.getId() + "'");
            
            int i = statement.executeUpdate(query.toString());
            return (i == 0 ? false : true);
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public boolean deleteProductType(Integer id) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "delete from product_type where id= '" + id + "'";
            int i = statement.executeUpdate(query.toString());
            return (i == 0 ? false : true);
        } catch (SQLException e) {
            throw new DAOException("Impossibile to execute query: " + e.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }

    public boolean updatePoints(String customerCard, int pointsToBeAdded) throws DAOException {

        Connection connection = null;
        Statement statement = null;
        ResultSet rs;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM customer WHERE card= '" + customerCard + "';";
            rs = statement.executeQuery(query);

            if (!rs.next()) { // if doesn't exist a customer with this card
                System.out.println("A customer with the inserted card doesn't exist");
                return false;
            }

            if (pointsToBeAdded < 0) { // Check if the previous points are enough
                if (rs.getInt("points") < (0 - pointsToBeAdded)) {
                    System.out.println("There are not enough points on the card to be subtracted");
                    return false;
                }
            }

            // UPDATE POINTS ON CARD
            // String updateQuery= "UPDATE customer SET points= '"+ (rs.getInt("points")+
            // pointsToBeAdded)+ "' WHERE id = '" + rs.getInt("id")+ "';";
            PreparedStatement prstm = connection.prepareStatement("UPDATE customer SET points= ? WHERE id = ?;");
            int totalPoints= rs.getInt("points") + pointsToBeAdded;
            System.out.println("total points = " + totalPoints);
            prstm.setInt(1, totalPoints);
            prstm.setInt(2, rs.getInt("id"));

            int result = prstm.executeUpdate();
            if (result != 1) { // Something goes wrong
                return false;
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;
    }

    @Override
    public boolean insertBalanceOperation(double amount, String type, LocalDate date) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer id = -1;
        String now;
        if(date == null) {        	
        	now = LocalDate.now().toString();
        } else {
        	now = date.toString();
        }
        try {
            connection = dataSource.getConnection();
            String query = "INSERT INTO balance_operation(id,date,money,type) VALUES(null, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, now);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, type);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return true;
    }

    @Override
    public List<BalanceOperation> getBalanceOperations(LocalDate from, LocalDate to) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ArrayList<BalanceOperation> balanceOperations = new ArrayList<>();

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM balance_operation WHERE date>= '" + from + "' AND date<= '" + to + "'";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                BalanceOperation bo = new ConcreteBalanceOperation(resultSet.getInt("id"),
                        LocalDate.parse(resultSet.getString("date")), resultSet.getDouble("money"),
                        resultSet.getString("type"));
                balanceOperations.add(bo);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return balanceOperations;
    }

    @Override
    public boolean storeSaleTransaction(SaleTransaction saleTransaction) throws DAOException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            // Update sale_transaction entry
            String query = "INSERT INTO sale_transaction(discountRate, price, payed) VALUES(?, ?, ?)";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, saleTransaction.getDiscountRate());
            pstm.setDouble(2, saleTransaction.getPrice());
            if(saleTransaction.getPayed()) {            	
            	pstm.setInt(3, 1);
            } else {
            	pstm.setInt(3, 0);
            }
            int v = pstm.executeUpdate();
            if(v == 0) {
            	return false;
            }
            // Update ticket_entry entries
            query = "INSERT INTO ticket_entry(transactionId, productId, bar_code, price_per_unit, amount, discount_rate, product_description) VALUES(?, ?, ?, ?, ?, ?, ?)";
            for (TicketEntry te : saleTransaction.getEntries()) {
            	pstm = connection.prepareStatement(query);
                pstm.setInt(1, saleTransaction.getTicketNumber());
                pstm.setInt(2, getProductTypeByBarCode(te.getBarCode()).getId());
                pstm.setString(3, te.getBarCode());
                pstm.setDouble(4, te.getPricePerUnit());
                pstm.setInt(5, te.getAmount());
                pstm.setDouble(6, te.getDiscountRate());
                pstm.setString(7, te.getProductDescription());
                pstm.executeUpdate();
            }
          
            Statement s = connection.createStatement();
            ResultSet resulSet = null;
            for (Product p : saleTransaction.getSaleProducts()) {
            	query = "update product set transaction_id = '" + saleTransaction.getTicketNumber() + "' where rfid = '" + p.getRFID() + "'";
            	s.executeUpdate(query);
            }
            
        } catch (SQLException ex) {
        	System.out.println(ex);
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;
    }
    
    
    
    
    
    
    @Override
    public boolean storeReturnTransaction(ReturnTransaction returnTransaction) throws DAOException {
        Connection connection = null;

        try {
            connection = dataSource.getConnection();

            // Update sale_transaction entry
            String query = "INSERT INTO return_transaction(amount, payed, discountRate) VALUES(?, ?, ?)";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, returnTransaction.getPrice());
            pstm.setDouble(3, returnTransaction.getDiscountRate());
            if(returnTransaction.getPayed()){
            	pstm.setInt(2, 1);
            } else {
            	pstm.setInt(2, 0);
            }
            pstm.executeUpdate();

            // Update ticket_entry entries
            query = "INSERT INTO return_ticket_entry(returnId, productId, bar_code, price_per_unit, amount, discount_rate, product_description) VALUES(?, ?, ?, ?, ?, ?, ?)";
            for (TicketEntry te : returnTransaction.getEntries()) {
                pstm = connection.prepareStatement(query);
                pstm.setInt(1, returnTransaction.getReturnId());
                pstm.setInt(2, getProductTypeByBarCode(te.getBarCode()).getId());
                pstm.setString(3, te.getBarCode());
                pstm.setDouble(4, te.getPricePerUnit());
                pstm.setInt(5, te.getAmount());
                pstm.setDouble(6, te.getDiscountRate());
                pstm.setString(7, te.getProductDescription());
                pstm.executeUpdate();
            }

            query = "update product set transaction_id = '" + 0 + "' where rfid = '?'";
            for (Product p : returnTransaction.getReturnProducts()) {
            	pstm = connection.prepareStatement(query);
                pstm.setString(1, p.getRFID());
                pstm.executeUpdate();
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;
    }

    @Override
    public List<TicketEntry> getEntries(Integer transactionId) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<TicketEntry> entries = new ArrayList<TicketEntry>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            System.out.println(transactionId);
            String query = "SELECT * FROM ticket_entry WHERE transactionId= '" + transactionId + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                TicketEntry te = new ConcreteTicketEntry(resultSet.getString("bar_code"),
                        resultSet.getString("product_description"), resultSet.getInt("amount"),
                        resultSet.getDouble("price_per_unit"), resultSet.getDouble("discount_rate"));
                entries.add(te);
                //System.out.println("add");
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return entries;
    }
    
    @Override
    public List<TicketEntry> getReturnEntries(Integer returnId) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<TicketEntry> entries = new ArrayList<TicketEntry>();

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM return_ticket_entry WHERE returnId= '" + returnId + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                TicketEntry te = new ConcreteTicketEntry(resultSet.getString("bar_code"),
                        resultSet.getString("product_description"), resultSet.getInt("amount"),
                        resultSet.getDouble("price_per_unit"), resultSet.getDouble("discount_rate"));
                entries.add(te);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return entries;
    }

    @Override
    public SaleTransaction searchSaleTransaction(Integer transactionId) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        SaleTransaction saleTransaction;
        
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM sale_transaction WHERE id= '" + transactionId + "'";
            resultSet = statement.executeQuery(query);
           
            if (!resultSet.next()) {
                System.out.print("Sono nella searchSaleTransaction che ritorna null");
                return null;
            }
            List<TicketEntry> entries = getEntries(transactionId);
            saleTransaction = new ConcreteSaleTransaction(transactionId, entries, resultSet.getDouble("discountRate"),
            		resultSet.getDouble("price"));

            if(resultSet.getInt("payed") == 1) {
            	saleTransaction.setPayed(true);
            } else {
            	saleTransaction.setPayed(false);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return saleTransaction;
    }

    @Override
    public boolean removeSaleTransaction(Integer saleNumber) throws DAOException {
        Connection connection = null;

        try {
            connection = dataSource.getConnection();

            // Search for sale transaction
            String query = "DELETE FROM sale_transaction WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, saleNumber);
            if (pstm.executeUpdate() <= 0)
                return false;
            
            // Delete product entry from ticket_entry
            query = "DELETE FROM ticket_entry WHERE transactionId=?";
            pstm = connection.prepareStatement(query);
            pstm.setInt(1, saleNumber);
            
            return true;
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }
    
    @Override
    public boolean setSaleTransactionPaid(Integer transactionId) throws DAOException {
    	Connection connection = null;
        Statement statement = null;
        int update;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "UPDATE sale_transaction SET payed=1 WHERE id= '" + transactionId + "';";
            update = statement.executeUpdate(query);
            System.out.println("Update query executed succesfully?--> update= " + update);

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        if (update != 1) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean setReturnTransactionPaid(Integer returnId) throws DAOException {
    	Connection connection = null;
        Statement statement = null;
        int update;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "UPDATE return_transaction SET payed=1 WHERE id= '" + returnId + "';";
            update = statement.executeUpdate(query);
            System.out.println("Update query executed succesfully?--> update= " + update);

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        if (update != 1) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean updateSaleTransactionPrice(Integer transactionId, double price, boolean committed) throws DAOException {
    	Connection connection = null;
        PreparedStatement preparedStatement = null;
        int update;
        double finalPrice;
        
        SaleTransaction s = this.searchSaleTransaction(transactionId);
        if(committed)
        	finalPrice= s.getPrice()-price;
        else
        	finalPrice = s.getPrice()+price;
        try {
            connection = dataSource.getConnection();
            String query = "UPDATE sale_transaction SET price=? WHERE id= '" + transactionId + "';";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, finalPrice);
            update = preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        if (update != 1) {
            return false;
        }
        return true;
    }
    
    
    /* Questo metodo agisce solo modificando la quantita', quando sta decrementando (committed=true) se la qty raggiunge 0 l'elemento NON viene 
     * eliminato, perche' se facessi cosi' poi se devo re-incrementare la qty (in deleteReturnTransaction) dovrei verificare se l'elemento e' presente e se
     * no inserirlo... --> CASINO
     */
    
    @Override
    public boolean updateSaleTransactionEntries (Integer transactionId, List<TicketEntry> returnEntries, boolean committed) throws DAOException {
    	Connection connection = null;
        PreparedStatement preparedStatement = null;
        int update;
        int final_qty;
        
        TicketEntry saleTe;
        for (TicketEntry te : returnEntries) {

        	saleTe = this.searchTicketEntry(transactionId, this.getProductTypeByBarCode(te.getBarCode()).getId());

        	if(committed) //confirm return transaction -> decrease products sold
        		final_qty = saleTe.getAmount() - te.getAmount();
        	else 
        		final_qty = saleTe.getAmount() + te.getAmount();
        	//System.out.println("quantita' finale:" + final_qty);
	        try {
	            connection = dataSource.getConnection();
	            String query = "UPDATE ticket_entry SET amount=? WHERE transactionId=? AND productId=?";
	            preparedStatement = connection.prepareStatement(query);
	            preparedStatement.setInt(1, final_qty);
	            preparedStatement.setInt(2, transactionId);
	            preparedStatement.setInt(3, this.getProductTypeByBarCode(te.getBarCode()).getId());
	            update = preparedStatement.executeUpdate();
	            //System.out.println("Update query executed succesfully?--> update= " + update);
	            
	            if (update != 1) {
	                return false;
	            }
	            
	        } catch (SQLException ex) {
	            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
	        } finally {
	            dataSource.close(connection);
	        }
        }

        return true;
    }
    
    @Override
    public TicketEntry searchTicketEntry(Integer transactionId, Integer productId) throws DAOException {
    	Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        TicketEntry ticketEntry;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM ticket_entry WHERE transactionId= '" + transactionId + "' AND productId= '" + productId + "';" ;
            resultSet = statement.executeQuery(query);

            if (!resultSet.next())
                return null;

            ticketEntry = new ConcreteTicketEntry(resultSet.getString("bar_code"), resultSet.getString("product_description"), resultSet.getInt("amount"),
                    resultSet.getDouble("price_per_unit"), resultSet.getDouble("discount_rate"));

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return ticketEntry;
    }

	@Override
	public void resetApplication() throws DAOException {
		Connection connection = null;
		PreparedStatement pstm = null;
		
		try {
			String query = "DELETE FROM ";
			String table[] = {"balance_operation","product_type","return_ticket_entry","return_transaction","sale_transaction","ticket_entry","'order'", "user", "customer", "product"};
			connection = dataSource.getConnection();
			for(int i = 0; i < 10; i++) {
				pstm = connection.prepareStatement(query + table[i]);
				pstm.executeUpdate();
				System.out.println("reset");
			}
			
		} catch(SQLException ex) {
			System.out.println("Impossible to execute query: " + ex.getMessage());
			
		} finally {
			dataSource.close(connection);
		}
		
	}

	@Override
	public boolean searchProductById(Integer productId) throws DAOException {
		Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean state = false;
        try {
        	connection = dataSource.getConnection();
        	statement = connection.createStatement();
        	String query = "SELECT * FROM product_type WHERE id = '" + productId + "';";
			
        	resultSet = statement.executeQuery(query);
        	if(resultSet.next())
        		state = true;
        	
			
		} catch(SQLException ex) {
			throw new DAOException("Impossibile to execute query: " + ex.getMessage());
			
		} finally {
			dataSource.close(connection);
		}
		
		return state;
	}

    @Override
    public boolean check_RFID_existance(String RFIDFrom, Integer interval) throws DAOException{

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        System.out.println("check_RFID_existance    RFIDFrom = "+ RFIDFrom);

        try{
            connection = dataSource.getConnection();
        	statement = connection.createStatement();

            System.out.println("check_RFID_existance    arrivo qui");
            Long parsedRFID= Long.valueOf(RFIDFrom);
            System.out.println("check_RFID_existance    parsedRFID = "+ parsedRFID);
            String baseRFID= "";

            for(Long i=parsedRFID; i<(parsedRFID+ interval); i++){

                //Built the correct base RFID

                int length = String.valueOf(i).length();
                if(length<12){  //Create a string with all 0
                
                    for(int j=0; j<(12-length); j++){
                         baseRFID= baseRFID+ "0";
                    }
                 }
                String actualRFID= baseRFID+i;
                baseRFID= "";
                System.out.println("check_RFID_existance    actualRFID = "+ actualRFID);

        	    String query = "SELECT * FROM product WHERE rfid ='" + actualRFID + "';";
			
        	    resultSet = statement.executeQuery(query);
                if(resultSet.next()){
                    System.out.println("check_RFID_existance= true");
                    return true;
                }
                           
            }
			
		} catch(SQLException ex) {
			throw new DAOException("Impossibile to execute query: " + ex.getMessage());
			
		} finally {
			dataSource.close(connection);
		}

        System.out.println("check_RFID_existance= false");
        return false;

    }

	@Override
	public Product getProductByRFID(String RFID) throws DAOException {
		Connection connection = null;
        Statement statment = null;
        ResultSet resultSet = null;
        Product p = null;
        try {
            connection = dataSource.getConnection();
            statment = connection.createStatement();
            String query = "select * from product where rfid = '" + RFID + "';";
            resultSet = statment.executeQuery(query);
            if(!resultSet.next())
            	return null;
        	p = new ConcreteProduct();
        	p.setRFID(resultSet.getString("rfid"));
        	p.setBarCode(resultSet.getString("bar_code"));
        	p.setTransactionId(resultSet.getInt("transaction_id"));
           	
        } catch (SQLException ex) {
        	throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
	
	return p;
	}

	@Override
	public List<Product> getSoldProducts(Integer transactionId) throws DAOException {
		Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Product> entries = new ArrayList<Product>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            System.out.println(transactionId);
            String query = "SELECT * FROM product WHERE transaction_id= '" + transactionId + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Product te = new ConcreteProduct();
                te.setBarCode(resultSet.getString("bar_code"));
                te.setRFID(resultSet.getString("rfid"));
                if(resultSet.getInt("transaction_id") == 0) {
                	te.setTransactionId(resultSet.getInt(null));
                } else {                	
                	te.setTransactionId(resultSet.getInt("transaction_id"));
                }
                entries.add(te);
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
        return entries;
	}
	

	@Override
    public void storeProduct(Product product) throws DAOException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            String query = "insert into product (rfid, transaction_id, bar_code) values (?,?,?);";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setString(1, product.getRFID());
            pstm.setInt(2, product.getTransactionId());
            pstm.setString(3, product.getBarCode());
            pstm.executeUpdate();
            
        } catch (SQLException ex) {
        	System.out.println(ex);
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }
    }
}
