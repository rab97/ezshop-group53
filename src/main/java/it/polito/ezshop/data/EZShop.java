package it.polito.ezshop.data;

import it.polito.ezshop.Constants;
import it.polito.ezshop.Operator;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.model.ConcreteTicketEntry;
import it.polito.ezshop.model.ConcreteUser;
import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.text.StyleConstants.CharacterConstants;
//import javax.transaction.InvalidTransactionException;
import javax.xml.stream.events.StartElement;

public class EZShop implements EZShopInterface {

    private IDAOEZshop dao = new DAOEZShop();
    private User runningUser = null;
    List<TicketEntry> productsToSale;
    List<TicketEntry> soldProducts;
    List<TicketEntry> productsToReturn;
    boolean saleTransaction_state;
    boolean returnTransaction_state;
    private Operator o = new Operator();

    @Override
    public void reset() {

    }

    @Override
    public Integer createUser(String username, String password, String role)
            throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        Integer user_id = -1;
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException();
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException();
        }
        if (role == null || role.isEmpty() && (!role.equals(Constants.ADMINISTRATOR) || !role.equals(Constants.CASHIER)
                || !role.equals(Constants.SHOP_MANAGER))) {
            throw new InvalidRoleException();
        }
        try {
            user_id = dao.insertUser(username, password, role);
        } catch (DAOException e) {
            System.out.println(e);
        }
        System.out.println(user_id);
        return user_id;
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        boolean state = true;
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }
        if (runningUser == null || !runningUser.getRole().equals(Constants.ADMINISTRATOR)) {
            throw new UnauthorizedException();
        }
        try {
            state = dao.removeUser(id);
        } catch (DAOException e) {
            System.out.println(e);
        }

        return state;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        List<User> users = new ArrayList<>();
        if (runningUser == null || !runningUser.getRole().equals(Constants.ADMINISTRATOR)) {
            throw new UnauthorizedException();
        }
        try {
            users = dao.getAllUsers();
        } catch (DAOException e) {
            System.out.println("getAllUsers exception");
        }
        return users;
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        User user = null;
        if (runningUser == null || !runningUser.getRole().equals(Constants.ADMINISTRATOR)) {
            throw new UnauthorizedException();
        }
        if (id <= 0 || id == null) {
            throw new InvalidUserIdException();
        }
        try {
            user = dao.searchUserById(id);
        } catch (DAOException e) {
            System.out.println(e);
        }
        return user;
    }

    @Override
    public boolean updateUserRights(Integer id, String role)
            throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        boolean state = true;

        if (runningUser == null || !runningUser.getRole().equals(Constants.ADMINISTRATOR)) {
            throw new UnauthorizedException();
        }
        if (id <= 0 || id == null) {
            throw new InvalidUserIdException();
        }
        if (role == null || role.isEmpty() && (!role.equals(Constants.ADMINISTRATOR) || !role.equals(Constants.CASHIER)
                || !role.equals(Constants.SHOP_MANAGER))) {
            throw new InvalidRoleException();
        }
        try {
            state = dao.updateRights(id, role);
        } catch (DAOException e) {
            System.out.println(e);
        }

        return state;
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        User user = null;
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException();
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException();
        }
        try {
            user = dao.searchUser(username, password);
        } catch (DAOException e) {
            System.out.println(e);
        }
        if (user != null && (user.getPassword().equals(password))) {
            runningUser = new ConcreteUser(user);
            return user;
        }
        return null;
    }

    @Override
    public boolean logout() {
        if (runningUser == null)
            return false;
        runningUser = null;
        return true;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note)
            throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException,
            UnauthorizedException {

        if (description.isEmpty()) {
            throw new InvalidProductDescriptionException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) {
            System.out.println("throw");
            throw new InvalidProductCodeException();
        }
        try {
            Long.parseLong(productCode);

        } catch (Exception e) {
            System.out.println("throw");
            System.out.println(productCode);
            throw new InvalidProductCodeException();
        }
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }

        ProductType productType = new ConcreteProductType(null, description, productCode, note, null, pricePerUnit,
                null);
        try {
            dao.createProductType(productType);
        } catch (DAOException e) {
            System.out.println(e);
            return -1;
        }
        return productType.getId();
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
            throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException,
            InvalidPricePerUnitException, UnauthorizedException {
        if (newDescription == null || newDescription.isEmpty()) {
            throw new InvalidProductDescriptionException();
        }
        if (newCode == null || newCode.isEmpty() || !o.isValidCode(newCode)) {
            throw new InvalidProductCodeException();
        }
        // valid bar code
        try {
            int tmp = Integer.parseInt(newCode);
        } catch (Exception e) {
            throw new InvalidProductCodeException();
        }
        if (newPrice <= 0) {
            throw new InvalidPricePerUnitException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        try {
            ProductType p = new ConcreteProductType(id, newDescription, newCode, newNote, null, newPrice, null);
            return dao.updateProduct(p);
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        if (id == null || id <= 0) {
            throw new InvalidProductIdException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        boolean delete = false;
        try {
            delete = dao.deleteProductType(id);
            delete = true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return delete;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        List<ProductType> productTypeList = new ArrayList<>();
        try {
            productTypeList = dao.getAllProducTypet();
        } catch (DAOException e) {
            System.out.println("getAllProductType exception");
        }
        if (runningUser == null) {
            throw new UnauthorizedException();
        }
        return productTypeList;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode)
            throws InvalidProductCodeException, UnauthorizedException {
        if (barCode == null || barCode.isEmpty()) {
            throw new InvalidProductCodeException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        ProductType productType = null;
        try {
            productType = dao.getProductTypeByBarCode(barCode);
            return productType;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("errror");
        }
        return null;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        List<ProductType> productTypeList = new ArrayList<>();
        try {
            productTypeList = dao.getProductTypeByDescription(description);
        } catch (Exception e) {
            System.out.println("errror");
        }
        return productTypeList;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded)
            throws InvalidProductIdException, UnauthorizedException {

        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException();
        }
        if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        boolean result = false;
        try {
            result = dao.updateQuantity(productId, toBeAdded);
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos)
            throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        String position[] = newPos.split("-");
        System.out.println(newPos);
        System.out.println("position lemgth: " + position.length);
        if (position.length != 3 || position[0].isEmpty() || position[1].isEmpty() || position[2].isEmpty()) {
            throw new InvalidLocationException(
                    "location wrong: assure that you use this pattern: number-string-number");
        }
        try {
            Integer.parseInt(position[0]);
            Integer.parseInt(position[2]);

        } catch (Exception e) {
            System.out.println("eccezione : " + e);
            throw new InvalidLocationException(
                    "location wrong: assure that you use this pattern: number-string-number");
        }
        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        try {
            if (!dao.searchPosition(newPos)) {
            	dao.updatePosition(productId, newPos);
                return true;
            }
        } catch (DAOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return false;
    }

    /**
         * This method issues an order of <quantity> units of product with given
         * <productCode>, each unit will be payed <pricePerUnit> to the supplier.
         *  
         * @return the id of the order (> 0) -1  if there
         *         are problems with the db
        
         */
    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException,
            InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {

            if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)) {
                throw new UnauthorizedException();
            }
            if(productCode== null| productCode.isEmpty() | productCode.length()!=12){
                throw new InvalidProductCodeException();
            }
            if(quantity<=0){
                throw new InvalidQuantityException();
            }
            if(pricePerUnit<=0){
                throw new InvalidPricePerUnitException();
            }

            Integer newOrderId= 0;
            try {
                 newOrderId= dao.insertNewOrder(productCode, quantity, pricePerUnit);
    
            } catch (DAOException e) {
                System.out.println("db excepiton");
            }

        return newOrderId;
    }


    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit)
            throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException,
            UnauthorizedException {
        return null;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        return false;
    }

    

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }

        List<Order> ordersList = new ArrayList<>();
        try {
            ordersList = dao.getAllOrders();

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return ordersList;
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }

        if (customerName == null | customerName.isEmpty()) {
            throw new InvalidCustomerNameException();
        }

        Integer newCustomerId = -1;
        System.out.println("newCustomerId before query=");
        System.out.println(newCustomerId);

        try {
            newCustomerId = dao.insertCustomer(customerName);
            System.out.println("newCustomerId after query=");
            System.out.println(newCustomerId);

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return newCustomerId;
    }


    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard)
            throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException,
            UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)){
                throw new UnauthorizedException();
        }
        if (newCustomerName == null | newCustomerName.isEmpty()) {
            throw new InvalidCustomerNameException();
        }
        if (newCustomerCard== null | newCustomerCard.isEmpty() | newCustomerCard.length() != 10) { 
            throw new InvalidCustomerCardException();
        }

        boolean modification = false;

        try {
            modification = dao.updateCustomer(id, newCustomerName, newCustomerCard);
            System.out.println("modification= " + modification);

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return modification;
    }


    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }
        if (id == null | id <= 0) {
            throw new InvalidCustomerIdException();
        }

        boolean del = false;
        try {
            del = dao.deleteCustomer(id);
        } catch (DAOException e) {
            System.out.println("db excepiton");
        }
        return del;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }
        if (id == null | id <= 0) {
            throw new InvalidCustomerIdException();
        }

        Customer c = null;
        try {
            c = dao.getCustomer(id);
        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return c;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }

        List<Customer> customersList = new ArrayList<>();
        try {
            customersList = dao.getAllCustomers();

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return customersList;
    }

    @Override
    public String createCard() throws UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)){
            throw new UnauthorizedException();
        }

        //Card String generation
        int leftLimit = 48;
        int rightLimit = 57; 
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        String generatedString = buffer.toString();
        System.out.println(generatedString);

        // call the db
        /*
         * boolean cardInsertion= false; try{ cardInsertion =
         * dao.createNewCard(generatedString);
         * 
         * }catch(DAOException e){ System.out.println("db excepiton");
         * 
         * }
         * 
         * if(cardInsertion ==false){ return ""; }else{ return generatedString; }
         */
        return generatedString;

    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        
        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }
        if (customerId == null | customerId <= 0) {
            throw new InvalidCustomerIdException();
        }
        if(customerCard== null| customerCard.isEmpty() | customerCard.length()!=10){
            throw new InvalidCustomerCardException();
        }

        boolean result = false;
        try {
            result = dao.bindCardToCustomer(customerCard, customerId);

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return result;
    }

      

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {

        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }
        if(customerCard==null| customerCard.isEmpty()|customerCard.length()!=10){
            throw new InvalidCustomerCardException();
        }

        boolean modification = false;
        try {
            modification = dao.updatePoints(customerCard, pointsToBeAdded);

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return modification;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        if (runningUser==null |runningUser == null && (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                || !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                || !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }

        Integer sale_transaction_id = -1;

        try {
            sale_transaction_id = dao.insertSaleTransaction();
        } catch (DAOException e) {
            System.out.println(e);
        }
        productsToSale = new ArrayList<TicketEntry>();
        saleTransaction_state = Constants.OPENED;
        System.out.println(sale_transaction_id);
        return sale_transaction_id;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount)
            throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException,
            UnauthorizedException {

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null && (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                || !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                || !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (amount < 0) {
            throw new InvalidQuantityException();
        }
        if (productCode.isEmpty() || productCode == null) { // manca invalid
            throw new InvalidProductCodeException();
        }

        // check on product
        ProductType pt = getProductTypeByBarCode(productCode);
        if (pt == null || pt.getQuantity() < amount)
            return false;

        // check sale transaction state
        if (saleTransaction_state != Constants.OPENED)
            return false;

        TicketEntry te = new ConcreteTicketEntry(productCode, pt.getProductDescription(), amount, pt.getPricePerUnit(),
                0);

        // decrement product availability
        try {
            dao.updateQuantity(pt.getId(), (-2) * amount);
        } catch (DAOException e) {
            System.out.println(e);
            return false;
        }

        // add to list
        boolean toAdd = true;
        for (TicketEntry t : productsToSale) {
            if (t.getBarCode().equals(productCode)) {
                t.setAmount(t.getAmount() + amount);
                toAdd = false;
                break;
            }
        }
        if (toAdd)
            productsToSale.add(te);

        // print log
        System.out.println("Added product to sale:");
        for (TicketEntry td : productsToSale) {
            pt = getProductTypeByBarCode(td.getBarCode());
            System.out.println(td.getProductDescription() + " " + td.getBarCode() + " " + td.getAmount() + "Product available: " + pt.getQuantity());
        }

        return true;

    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)
            throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException,
            UnauthorizedException {
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null && (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                || !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                || !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (amount < 0) {
            throw new InvalidQuantityException();
        }
        if (productCode.isEmpty() || productCode == null) { // manca invalid
            throw new InvalidProductCodeException();
        }

        // check sale transaction state
        if (saleTransaction_state != Constants.OPENED)
            return false;

        // print log
        System.out.println("Available products from sale:");
        for (TicketEntry td : productsToSale) {
            System.out.println(td.getProductDescription() + td.getAmount());
        }

        //search-check on product 
        TicketEntry t = null;
        for(TicketEntry te : productsToSale) {
            if(te.getBarCode().equals(productCode)) {
                te.setAmount(te.getAmount() - amount);
                System.out.println(te.getProductDescription() + " " + te.getAmount());
                if(te.getAmount() <= 0)
                    productsToSale.remove(te);
                t = te;
            }
        }

        System.out.println("Product found= " + t.getProductDescription());
        if(t == null)
            return false;

        // increment product availability
        ProductType pt = getProductTypeByBarCode(productCode);
        try {
            dao.updateQuantity(pt.getId(), amount);
        } catch (DAOException e) {
            System.out.println(e);
            return false;
        }

        // print log
        System.out.println("Removed product from sale:");
        for (TicketEntry td : productsToSale) {
            System.out.println(td.getProductDescription() + td.getAmount());
        }

        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)
            throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException,
            UnauthorizedException {
        return true;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate)
            throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId)
            throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber)
            throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId)
            throws InvalidTransactionIdException, UnauthorizedException {

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        if (runningUser == null && (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                || !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                || !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }

        return null;
    }

    @Override
    public Integer startReturnTransaction(Integer transactionId)
            throws InvalidTransactionIdException, UnauthorizedException {
    	if (runningUser==null |runningUser == null && (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                || !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                || !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	if(transactionId<=0 || transactionId==null) {
    		throw new InvalidTransactionIdException();
    	}
    	
        Integer return_transaction_id = -1;
        
        SaleTransaction s = this.getSaleTransaction(transactionId);
        if(s!=null) {
	        try {
	            return_transaction_id = dao.insertReturnTransaction();
	        } catch (DAOException e) {
	            System.out.println(e);
	        }
	        soldProducts = new ArrayList<TicketEntry>();
	        try {
	        	soldProducts = dao.getSoldProducts(transactionId);
	        } catch (DAOException e) {
	            System.out.println(e);
	        }
	        returnTransaction_state = Constants.NOT_COMMITTED;
	        System.out.println(return_transaction_id);
        }
        return return_transaction_id;

    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException,
            InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
    	
    	if (runningUser==null |runningUser == null && (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                || !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                || !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	if(returnId<=0 || returnId==null) {
    		throw new InvalidTransactionIdException();
    	}
    	if(amount<=0) {
    		throw new InvalidQuantityException();
    	}
    	if (productCode.isEmpty() || productCode == null) { // manca invalid
             throw new InvalidProductCodeException();
        }
    	
    	boolean res=false;
    	try {
    		res=dao.getReturnTransactionById(returnId);
    	} catch (DAOException e) {
    		System.out.println(e);
    	}
    	
    	TicketEntry prodToReturn=null;
    	for (TicketEntry prod : soldProducts) {
    		if(prod.getBarCode().equals(productCode))
    			prodToReturn=prod;
    	}
    	if(prodToReturn==null|| res==false || prodToReturn.getAmount()<amount)
    		return false;
    	productsToReturn.add(prodToReturn);
    	
    	return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit)
            throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId)
            throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash)
            throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard)
            throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard)
            throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
    	String type;
    	double future_balance;
    	 if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                 && !runningUser.equals(Constants.SHOP_MANAGER))) {
             throw new UnauthorizedException();
         }
    	future_balance=this.computeBalance();
    	future_balance+=toBeAdded;
    	if(future_balance<=0)
    		return false;
    	if(toBeAdded>=0)
    		type="CREDIT";
    	else
    		type="DEBIT";
    	boolean state = false;
        try {
            state = dao.insertBalanceOperation(Math.abs(toBeAdded), type);
        } catch (Exception e) {
            System.out.println(e);
        }
        return state;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
    	 List<BalanceOperation> balanceOperationList = new ArrayList<>();
    	 if(from==null)
    		 from=LocalDate.of(1900, 1, 1);
    	 if(to==null)
    		 to=LocalDate.of(2100, 1, 1);
    	 if(from.isAfter(to)) {
    		 LocalDate temp = from;
    		 from=to;
    		 to=temp;
    	 } 
         try {
        	 balanceOperationList = dao.getBalanceOperations(from,to);
         } catch (DAOException e) {
             System.out.println("getBalanceOperations exception");
         }
         if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                 && !runningUser.equals(Constants.SHOP_MANAGER))) {
             throw new UnauthorizedException();
         }
         return balanceOperationList;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
    	List<BalanceOperation> balanceOperationList = new ArrayList<>();
    	double balance=0;
    	
    	if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
    	balanceOperationList=this.getCreditsAndDebits(null,null);
    	for (BalanceOperation op : balanceOperationList) {
    		if(op.getType().equals("DEBIT") || op.getType().equals("ORDER") || op.getType().equals("RETURN"))
    			balance-=op.getMoney();
    		else
    			balance+=op.getMoney();
    	}
    	return balance;
    }   
}
