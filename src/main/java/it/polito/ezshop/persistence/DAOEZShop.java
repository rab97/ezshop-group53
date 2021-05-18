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
        try {
            connection = dataSource.getConnection();
            statment = connection.createStatement();
            String query = "select * from product_type where bar_code = '" + barCode + "';";
            resultSet = statment.executeQuery(query);
            ConcreteProductType productType = null;
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer quantity = resultSet.getInt("quantity");
                String location = resultSet.getString("location");
                String notes = resultSet.getString("note");
                String description = resultSet.getString("description");
                String bar_code = resultSet.getString("bar_code");
                Double pricePerUnit = resultSet.getDouble("price_per_unit");
                productType = new ConcreteProductType(id, description, bar_code, notes, quantity, pricePerUnit,
                        location);
            }
            return productType;
        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e);
        } finally {
            dataSource.close(connection);
        }
        return null;
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
            if(preparedStatement.executeUpdate() > 0);
            	state = true;
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

            pstm = connection.prepareStatement(
                    "INSERT INTO 'order' (product_code, price_per_unit, quantity, status) values (?,?,?,?)");
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
            PreparedStatement prstm = connection
                    .prepareStatement("INSERT INTO balance_operation VALUES (date, money, type)= (?,?,?);");

            java.util.Date today = new java.util.Date();
            prstm.setDate(1, (java.sql.Date) today);
            prstm.setDouble(2, pricePerUnit * quantity);
            prstm.setString(3, "DEBIT");

            prstm.execute();
            rs = prstm.getGeneratedKeys();

            if (!rs.next()) {
                return -1;
            }

            // Insert of an Order
            prstm = connection.prepareStatement(
                    "INSERT INTO order(balanceId, product_code, price_per_unit, quantity, status) values (?,?,?,?,?)");
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
    public boolean payOrder(Integer orderId) throws DAOException {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM order WHERE id= '" + orderId + "';";
            ResultSet rs = statement.executeQuery(query);

            if (!rs.next()) { // The are no orders with given id
                return false;
            }

            String orderStatus = rs.getString("status");
            System.out.println("Status= " + orderStatus);
            if (orderStatus == "PAYED") { // It doesn't need a modification
                return true;
            }
            if (orderStatus != "ISSUED" | orderStatus != "ORDERED") { // Not valid status
                return false;
            }

            ConcreteOrder order = new ConcreteOrder(rs.getInt("balanceId"), rs.getString("product_code"),
                    rs.getDouble("price_per_unit"), rs.getInt("quantity"), orderStatus, rs.getInt("id"));

            // Insert BalanceOperation
            PreparedStatement prstm = connection
                    .prepareStatement("INSERT INTO balance_operation VALUES (date, money, type)= (?,?,?);");

            java.util.Date today = new java.util.Date();
            prstm.setDate(1, (java.sql.Date) today);
            prstm.setDouble(2, order.getPricePerUnit() * order.getQuantity());
            prstm.setString(3, "DEBIT");

            prstm.execute();
            rs = prstm.getGeneratedKeys();

            // Update Order
            query = "UPDATE order SET balanceId= '" + rs.getInt(1) + "' , status = 'PAYED' WHERE id= '" + orderId
                    + "';";

            rs = statement.executeQuery(query);
            if (!rs.next()) {
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
            String query= "UPDATE order SET status = 'COMPLETED' WHERE id = '" + orderId + "';";

            int update = statement.executeUpdate(query);
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
    public Order getOrder(Integer orderId) throws DAOException{

        Connection connection = null;
        Statement statement = null;
        Order order = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "SELECT * FROM order WHERE id = '" + orderId + "';";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                order = new ConcreteOrder(rs.getInt("balanceId"), rs.getString("product_code"), rs.getDouble("price_per_unit"), 
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

            String query = "SELECT * FROM order";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if (resultSet.getString("status") == "ISSUED" | resultSet.getString("status") == "ORDERED"
                        | resultSet.getString("status") == "COMPLETED") {
                    Order o = new ConcreteOrder(resultSet.getInt("balanceId"), resultSet.getString("product_code"),
                            resultSet.getDouble("price_per_unit"), resultSet.getInt("quantity"),
                            resultSet.getString("status"), resultSet.getInt("orderId"));
                    orders.add(o);
                }
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
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            // Insert
            PreparedStatement pstm;

            pstm = connection.prepareStatement("insert into customer(name) values (?)");
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
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "select * from product_type where description = '" + description + "';";
            resultSet = statement.executeQuery(query);
            List<ProductType> productTypeList = new ArrayList<>();
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
            return productTypeList;
        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e);
        } finally {
            dataSource.close(connection);
        }
        return null;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws DAOException {
        // TODO Auto-generated method stub
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
        } catch (Exception e) {
            System.out.println(e);
            return false;
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
            
            System.out.println("query ok and return_transaction created with id: " + id);

            id = resultSet.next() ? resultSet.getInt(1) : 1;
            
            System.out.println("query ok and return_transaction created with id: " + id);
            
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

            // Search for sale transaction
            String query = "DELETE FROM return_transaction WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, returnId);
            if (pstm.executeUpdate() == -1)
                return false;

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
            
            if (!resultSet.next())
                return null;

            List<TicketEntry> entries = getReturnEntries(returnId);         

            returnTransaction = new ConcreteReturnTransaction(returnId, -1, entries, resultSet.getDouble("amount"), resultSet.getDouble("discountRate"));
            
            if(resultSet.getInt("payed")==1)
            	returnTransaction.setPayed(true);

            
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

            String query = "SELECT * FROM customer WHERE customerId= '" + customerId + "' OR card= '" + card + "';";
            ResultSet rs = statement.executeQuery(query);

            boolean customerExistance = false;
            while (rs.next()) { // check all the rows to find a non-existing user or an already assigned card

                if (rs.getInt("id") == customerId) {
                    customerExistance = true;
                }
                if (rs.getString("card") == card) {
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
        }

        return true;
    }

    @Override
    public void updatePosition(Integer productId, String position) throws DAOException {
        // TODO Auto-generated method stub
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "update product_type set location = '" + position + "' where id = '" + productId + "'";
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            dataSource.close(connection);
        }
    }

    @Override
    public boolean searchPosition(String position) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "select * from product_type where location = '" + position + "'";
            resultSet = statement.executeQuery(query);
            return (resultSet.next() ? true : false);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            dataSource.close(connection);
        }
        return false;
    }

    @Override
    public boolean updateProduct(ProductType productType) throws DAOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
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
            System.out.println(query.toString());
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
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = "delete from product_type where id= '" + id + "'";
            int i = statement.executeUpdate(query.toString());
            return (i == 0 ? false : true);
        } catch (SQLException e) {
            System.out.println(e);
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
    public boolean insertBalanceOperation(double amount, String type) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer id = -1;
        String now = LocalDate.now().toString();
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
            System.out.println("id: " + id);
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

            String query = "SELECT * FROM balance_operation WHERE date>='" + from + "' AND date<= '" + to + "';";
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
            String query = "INSERT INTO sale_transaction(discountRate, price, payed) VALUES(?, ?, 0)";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, saleTransaction.getDiscountRate());
            pstm.setDouble(2, saleTransaction.getPrice());
            pstm.executeUpdate();

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

        } catch (SQLException ex) {
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
            String query = "INSERT INTO return_transaction(amount, payed, discountRate) VALUES(?, 0, ?)";
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setDouble(1, returnTransaction.getPrice());
            pstm.setDouble(2, returnTransaction.getDiscountRate());
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
            String query = "SELECT * FROM ticket_entry WHERE transactionId= '" + transactionId + "'";
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

            if (!resultSet.next())
                return null;

            List<TicketEntry> entries = getEntries(transactionId);

            saleTransaction = new ConcreteSaleTransaction(transactionId, entries, resultSet.getDouble("discountRate"),
                    resultSet.getDouble("price"));
            
            if(resultSet.getInt("payed")==1)
            	saleTransaction.setPayed(true);

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
            if (pstm.executeUpdate() == -1)
                return false;

        } catch (SQLException ex) {
            throw new DAOException("Impossibile to execute query: " + ex.getMessage());
        } finally {
            dataSource.close(connection);
        }

        return true;
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
}
