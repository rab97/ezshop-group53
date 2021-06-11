package it.polito.ezshop.data;

import it.polito.ezshop.Constants;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.ConcreteProductType;
import it.polito.ezshop.model.ConcreteSaleTransaction;
import it.polito.ezshop.model.ConcreteTicketEntry;
import it.polito.ezshop.model.ConcreteUser;
import it.polito.ezshop.model.ConcreteReturnTransaction;
import it.polito.ezshop.model.Operator;
import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import it.polito.ezshop.persistence.IDAOEZshop;

import java.beans.Transient;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EZShop implements EZShopInterface {

    private IDAOEZshop dao = new DAOEZShop();
    private User runningUser = null;
    ReturnTransaction returnTransaction;
    SaleTransaction saleTransaction;
    boolean saleTransaction_state;
    boolean returnTransaction_state;
    private Operator o = new Operator();
    
    
    
    @Override
    public void reset() {
    	try {
    		dao.resetApplication();
    	} catch (DAOException e){
    		System.out.println(e);
    	}
    }

    @Override
    public Integer createUser(String username, String password, String role)
            throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        Integer user_id = -1;
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException("Invalid Username");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("Invalid Password");
        }
        if (role == null || role.isEmpty() || (!role.equals(Constants.ADMINISTRATOR) && !role.equals(Constants.CASHIER)
                && !role.equals(Constants.SHOP_MANAGER))) {
            throw new InvalidRoleException("Invalid Role");
        }
        try {
            user_id = dao.insertUser(username, password, role);
        } catch (DAOException e) {
            System.out.println(e);
        }

        
        System.out.println("new id = " + user_id);
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
        if (id == null||(id <= 0)) {
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
        boolean state = false;

        if (runningUser == null || !runningUser.getRole().equals(Constants.ADMINISTRATOR)) {
            throw new UnauthorizedException();
        }
        if (id == null||id <= 0) {
            throw new InvalidUserIdException("Invalid User Id");
        }
        if (role == null || role.isEmpty() || (!role.equals(Constants.ADMINISTRATOR) && !role.equals(Constants.CASHIER)
                && !role.equals(Constants.SHOP_MANAGER))) {
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
        if (user != null)
            runningUser = new ConcreteUser(user);
        
        return user;
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

        if (description == null || description.isEmpty()) {
            throw new InvalidProductDescriptionException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) {
            System.out.println("throw");
            throw new InvalidProductCodeException();
        }
        try {
            Long.parseLong(productCode);

        } catch (Exception e) {
            
            throw new InvalidProductCodeException();
        }
        if (pricePerUnit <= 0) {
        	System.out.println("throw invalid price");
            throw new InvalidPricePerUnitException("Invalid PricePerUnit");
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
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
    	
    	if(id == null || id <= 0) {
    		throw new InvalidProductIdException();
    	}
    	
        if (newDescription == null || newDescription.isEmpty()) {
            throw new InvalidProductDescriptionException("Invalid Product Description");
        }
        try {
            Long.parseLong(newCode);
        } catch (Exception e) {
            throw new InvalidProductCodeException();
        }
        if (newCode == null || newCode.isEmpty() || !o.isValidCode(newCode)) {
            throw new InvalidProductCodeException();
        }
        // valid bar code
        
        if (newPrice <= 0) {
            throw new InvalidPricePerUnitException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
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
            throw new InvalidProductIdException("Invalid Product Id");
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        boolean delete = false;
        try {
            delete = dao.deleteProductType(id);
        } catch (Exception e) {
            System.out.println(e);
        }
        return delete;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
    	 if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)) && !runningUser.getRole().equals(Constants.SHOP_MANAGER)) {
             throw new UnauthorizedException();
         }
    	
        List<ProductType> productTypeList = new ArrayList<>();
        try {
            productTypeList = dao.getAllProducTypet();
        } catch (DAOException e) {
            System.out.println("getAllProductType exception");
        }
       
        return productTypeList;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode)
            throws InvalidProductCodeException, UnauthorizedException {
        if (barCode == null || barCode.isEmpty() || !o.isValidCode(barCode)) {
        	System.out.println("invalid barcode");
            throw new InvalidProductCodeException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        ProductType productType = null;
        try {
            productType = dao.getProductTypeByBarCode(barCode);
        } catch (Exception e) {
            System.out.println("errror");
        }
        return productType;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        List<ProductType> productTypeList = new ArrayList<>();
        
        if(description == null)
        	description = "";
        
        try {
            productTypeList = dao.getProductTypeByDescription(description);
        } catch (Exception e) {
            System.out.println("errror");
        }
        return productTypeList;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {

        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException();
        }
        if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
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
        
    	System.out.println("posizione: " + newPos);
    	
        if(newPos==null || newPos.isEmpty()) {
        	try {
        		dao.updatePosition(productId, "");
                return true;
            } catch (DAOException e) {
                System.out.println(e);
            }
        }
        
        String position[] = newPos.split("-");
        if(position == null || position.length == 0)
        	throw new InvalidLocationException();
        
        if (position.length != 3 || position[0].isEmpty() || position[1].isEmpty() || position[2].isEmpty()) {
            throw new InvalidLocationException(
                    "location wrong: assure that you use this pattern: number-string-number");
        }
        
        if(!position[1].matches("[a-zA-Z]*"))
        	throw new InvalidLocationException("location wrong: assure that you use this pattern: number-string-number");
        try {
            Integer.parseInt(position[0]);
            Integer.parseInt(position[2]);

        } catch (Exception e) {
            System.out.println("eccezione : " + e);
            throw new InvalidLocationException("location wrong: assure that you use this pattern: number-string-number");
        }
        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        try {
            if (!dao.searchPosition(newPos)  && dao.searchProductById(productId)) {
                dao.updatePosition(productId, newPos);
                return true;
            }
        } catch (DAOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException,
            InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) {
        	System.out.println("case problem with productCode");
            throw new InvalidProductCodeException();
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Invalid Quantity");
        }
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }

        Integer newOrderId = 0;
        try {
            newOrderId = dao.insertNewOrder(productCode, quantity, pricePerUnit);

        } catch (DAOException e) {
            System.out.println("db excepiton" + e);
        }

        return newOrderId;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit)
            throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException,
            UnauthorizedException {

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) {
            throw new InvalidProductCodeException();
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }
        // return -1 if the balance is not enough to satisfy the order
        if (computeBalance() < (quantity * pricePerUnit)) {
            return -1;
        }

        Integer newOrderId = -1;
        try {
            newOrderId = dao.payOrderDirectly(productCode, quantity, pricePerUnit);

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return newOrderId;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException("Invalid User Role");
        }
        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException("Invalid Order Id");
        }

        boolean payment = false;
        double money = computeBalance();
        
        try {
            payment = dao.payOrder(orderId, money);
            System.out.println("payment dopo la dao= " + payment);


        } catch (DAOException e) {
            System.out.println("db excepiton" + e);
        }

        System.out.println("payment ultimo= " + payment);
        return payment;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
             && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException();
        }

        Order myOrder;
        boolean recordArrival= false;

        try {
            myOrder= dao.getOrder(orderId);

            if(myOrder==null){ //the order doesn't exist
                return false;
            }

            if(myOrder.getStatus().equals("COMPLETED")){ //don't modify anything
                return true;
            
            }else if(!myOrder.getStatus().equals("PAYED")  && !myOrder.getStatus().equals("ORDERED")){ //not valid status
                return false;
            }

            ConcreteProductType orderProduct= dao.getProductTypeByBarCode(myOrder.getProductCode());

            if(orderProduct.getLocation()==null){
                throw new InvalidLocationException();
            }

            recordArrival= dao.recordArrival(orderId);

            if(recordArrival){ //if true, updateProductQuantity
                boolean updateProductQuantity= dao.updateQuantity(orderProduct.getId(), myOrder.getQuantity());
                if(!updateProductQuantity){
                    return false;
                }
            }

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return recordArrival;
    }

    @Override
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, 
InvalidLocationException, InvalidRFIDException {

        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException();
        }
        if(RFIDfrom.length()!=12){
            throw new InvalidRFIDException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
         && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        boolean isNumeric = RFIDfrom.chars().allMatch( Character::isDigit );
        if(!isNumeric){
            throw new InvalidRFIDException();
        }

        Order myOrder;
        boolean recordArrival= false;
        boolean rfidAlreadyExist= true;

        try {

            myOrder= dao.getOrder(orderId);

            if(myOrder==null){ //the order doesn't exist
                return false;
            }

            if(myOrder.getStatus().equals("COMPLETED")){ //don't modify anything
                return true;
            
            }else if(!myOrder.getStatus().equals("PAYED")  && !myOrder.getStatus().equals("ORDERED")){ //not valid status
                return false;
            }

            ConcreteProductType orderProduct= dao.getProductTypeByBarCode(myOrder.getProductCode());

            if(orderProduct.getLocation()==null){
                throw new InvalidLocationException();
            }

            
            rfidAlreadyExist= dao.check_RFID_existance(RFIDfrom, myOrder.getQuantity());
            System.out.println("rfidAlreadyExist in recordOrderArrivalRFID = " + rfidAlreadyExist);
            if(rfidAlreadyExist){ //If true, throw exception
                throw new InvalidRFIDException();
            }

            //Record Arrival
            recordArrival= dao.recordArrival(orderId);

            if(recordArrival){ //if true, updateProductQuantity
                boolean updateProductQuantity= dao.updateQuantity(orderProduct.getId(), myOrder.getQuantity());
                if(!updateProductQuantity){
                    return false;
                }

                boolean updateProductRFID= dao.recordProductArrivalRFID(orderId, myOrder.getQuantity(), RFIDfrom, orderProduct.getBarCode());
                if(!updateProductRFID){
                    return false;
                }
            }

        } catch (DAOException e) {
            System.out.println("db excepiton");
        }

        return recordArrival;

    }


    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }

        List<Order> ordersList = new ArrayList<>();
        try {
            ordersList = dao.getAllOrders();

        } catch (DAOException e) {
            System.out.println("db excepiton" + e);
        }

        return ordersList;
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (customerName == null || customerName.isEmpty()) {
            throw new InvalidCustomerNameException("Invalid Customer Name");
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
    	
    	if (id == null || id <= 0) {
            throw new InvalidCustomerIdException("Invalid Customer Id");
        }
    	
    	if (newCustomerName == null || newCustomerName.isEmpty()) {
            throw new InvalidCustomerNameException();
        }
        if (newCustomerCard != null && !newCustomerCard.isEmpty() && newCustomerCard.length() != 10) {
            throw new InvalidCustomerCardException("Invalid Customer Card");
        }
        
        if(newCustomerCard != null && !newCustomerCard.isEmpty()) {
        	try {
            	Long.parseLong(newCustomerCard);

        	} catch (Exception e) {
            
            	throw new InvalidCustomerCardException();
        	}
        }

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
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

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (id == null || id <= 0) {
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

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (id == null || id <= 0) {
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

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
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

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }

        // Card String generation
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

        return generatedString;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId)
            throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (customerId == null || customerId <= 0) {
            throw new InvalidCustomerIdException();
        }
        if (customerCard == null || customerCard.isEmpty() || customerCard.length() != 10) {
            throw new InvalidCustomerCardException();
        }
  
        boolean result = false;
        try {
            result = dao.bindCardToCustomer(customerCard, customerId);
        } catch (DAOException e) {
            System.out.println("db excepiton" + e);
        }

        return result;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded)
            throws InvalidCustomerCardException, UnauthorizedException {
    	
    	if (customerCard == null || customerCard.isEmpty() || customerCard.length() != 10) {
            throw new InvalidCustomerCardException();
        }
    	
    	try {
            Long.parseLong(customerCard);

        } catch (Exception e) {
            
            throw new InvalidCustomerCardException();
        }
    	
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
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
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        System.out.println("passa?");

        Integer sale_transaction_id = -1;

        try {
            sale_transaction_id = dao.insertSaleTransaction();
        } catch (DAOException e) {
            System.out.println(e);
        }
        saleTransaction = new ConcreteSaleTransaction(sale_transaction_id + 1, new ArrayList<TicketEntry>(), 0, 0);
        saleTransaction_state = Constants.OPENED;
        	
        saleTransaction.getSaleProducts().clear();
        
        return saleTransaction.getTicketNumber();
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount)
            throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException,
            UnauthorizedException {

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (amount < 0) {
            throw new InvalidQuantityException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) { // manca invalid
            throw new InvalidProductCodeException();
        }
        if (saleTransaction.getTicketNumber() != transactionId)
            return false;
        // check on product
        ProductType pt = null;
        try {
        	pt = dao.getProductTypeByBarCode(productCode);
        } catch (DAOException e) {
			System.out.println(e);
		} 
        if (pt == null || pt.getQuantity() < amount)
            return false;

        // check sale transaction state
        if (saleTransaction_state != Constants.OPENED)
            return false;

        TicketEntry te = new ConcreteTicketEntry(productCode, pt.getProductDescription(), amount, pt.getPricePerUnit(),
                0);

        // decrement product availability
        try {
            dao.updateQuantity(pt.getId(), -amount);
        } catch (DAOException e) {
            System.out.println(e);
            return false;
        }

        // add to list
        boolean toAdd = true;
        for (TicketEntry t : saleTransaction.getEntries()) {
            if (t.getBarCode().equals(productCode)) {
                t.setAmount(t.getAmount() + amount);
                toAdd = false;
                break;
            }
        }
        if (toAdd)
            saleTransaction.getEntries().add(te);

        // print log
        System.out.println("Added product to sale:");
        for (TicketEntry td : saleTransaction.getEntries()) {
        	try {
            	pt = dao.getProductTypeByBarCode(productCode);
            } catch (DAOException e) {
    			System.out.println(e);
    		} 
            System.out.println(td.getProductDescription() + " " + td.getBarCode() + " " + td.getAmount()
                    + "Product available: " + pt.getQuantity());
        }

        return true;

    }

    @Override
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException{
    	if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
    	
    	if(RFID == null || RFID.isEmpty() || RFID.length() != 12) {
    		throw new InvalidRFIDException();
    	}
    	
    	if(RFID != null && !RFID.isEmpty()) {
    		try {
    			Integer.parseInt(RFID);
    		} catch(Exception e) {
    			throw new InvalidRFIDException();
    		}
    	}
    	
    	if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if (saleTransaction.getTicketNumber() != transactionId)
            return false;
    	
    	// check on product
        Product p = null;
        try {
        	p = dao.getProductByRFID(RFID);
        } catch (DAOException e) {
			System.out.println(e);
		} 
        if (p == null)
            return false;
        
     // check sale transaction state
        if (saleTransaction_state != Constants.OPENED)
            return false;
        
        ProductType pt = null;
        try {
        	pt = dao.getProductTypeByBarCode(p.getBarCode());
        } catch (DAOException e) {
            System.out.println(e);
            return false;
        }
        
        TicketEntry te = new ConcreteTicketEntry(pt.getBarCode(), pt.getProductDescription(), 1, pt.getPricePerUnit(),
                0);
        
     // decrement product availability
        try {
            dao.updateQuantity(pt.getId(), -1);
        } catch (DAOException e) {
            System.out.println(e);
            return false;
        }
        
     // add to list
        boolean toAdd = true;
        for (TicketEntry t : saleTransaction.getEntries()) {
            if (t.getBarCode().equals(pt.getBarCode())) {
                t.setAmount(t.getAmount() + 1);
                toAdd = false;
                break;
            }
        }
        
        if (toAdd)
            saleTransaction.getEntries().add(te);
        
        saleTransaction.getSaleProducts().add(p);
    
    	return true;
    }
    

    @Override
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException{
    	if(transactionId == null || transactionId <= 0) {
        	throw new InvalidTransactionIdException();
        }
    	if(RFID == null || RFID.isEmpty() || RFID.length() != 12) {
    		throw new InvalidRFIDException();
    	}
    	
    	if(RFID != null && !RFID.isEmpty()) {
    		try {
    			Integer.parseInt(RFID);
    		} catch(Exception e) {
    			throw new InvalidRFIDException();
    		}
    	}
    	
    	if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if (saleTransaction.getTicketNumber() != transactionId) {    		
    		return false;
    	}
    	
    	if (saleTransaction_state != Constants.OPENED)
            return false;
    	
    	boolean found = false;
        for (Product p : saleTransaction.getSaleProducts()) {
            if (p.getRFID().equals(RFID)) {
                    saleTransaction.getSaleProducts().remove(p);
                    // increment product availability
                    try {
                    	ProductType pt = dao.getProductTypeByBarCode(p.getBarCode());
                        dao.updateQuantity(pt.getId(), 1);
                    } catch (DAOException e) {
                        System.out.println(e);
                        return false;
                    }
                    // remove product from ticket entries
                    for (TicketEntry te : saleTransaction.getEntries()) {
                        if (te.getBarCode().equals(p.getBarCode())) {
                            te.setAmount(te.getAmount() - 1);
                            if (te.getAmount() <= 0)
                                saleTransaction.getEntries().remove(te);
                            found = true;
                            break;
                        }
                    }
                found = true;
                break;
            }
        }
        if(!found)
        	return false;
    	
    	return found;

    }


    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)
            throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException,
            UnauthorizedException {
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (amount < 0) {
            throw new InvalidQuantityException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) { // manca invalid
            throw new InvalidProductCodeException();
        }
        if (saleTransaction.getTicketNumber() != transactionId)
            return false;
        // check sale transaction state
        if (saleTransaction_state != Constants.OPENED)
            return false;

        // print log
        System.out.println("Available products from sale:");
        for (TicketEntry td : saleTransaction.getEntries()) {
            System.out.println(td.getProductDescription() + td.getAmount());
        }

        // search-check on product
        boolean found = false;
        for (TicketEntry te : saleTransaction.getEntries()) {
            if (te.getBarCode().equals(productCode)) {
                te.setAmount(te.getAmount() - amount);
                System.out.println(te.getProductDescription() + " " + te.getAmount());
                if (te.getAmount() <= 0)
                    saleTransaction.getEntries().remove(te);
                found = true;
                break;
            }
        }
        if (!found)
            return false;

        // increment product availability
        ProductType pt = null;
        try {
        	pt = dao.getProductTypeByBarCode(productCode);
        } catch (DAOException e) {
			System.out.println(e);
		} 
        try {
            dao.updateQuantity(pt.getId(), amount);
        } catch (DAOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)
            throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException,
            UnauthorizedException {
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (productCode == null || productCode.isEmpty() || !o.isValidCode(productCode)) { // manca invalid
            throw new InvalidProductCodeException("Invalid Product Code");
        }
        if (discountRate < 0 || discountRate >= 1.00) {
            throw new InvalidDiscountRateException("Invalid Discount Rate");
        }
        if (saleTransaction.getTicketNumber() != transactionId)
            return false;
        if (saleTransaction_state != Constants.OPENED)
            return false;

        // check if the product exists in the sale transaction
        for (TicketEntry te : saleTransaction.getEntries()) {
            if (te.getBarCode().equals(productCode)) {
                te.setDiscountRate(discountRate);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate)
            throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (discountRate < 0 || discountRate >= 1.00) {
            throw new InvalidDiscountRateException();
        }

        if (saleTransaction.getTicketNumber() != transactionId)
            return false;

        saleTransaction.setDiscountRate(discountRate);
        
        return true;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
        if (saleTransaction.getTicketNumber() != transactionId)
            return -1;

        try{
            SaleTransaction checkTransaction= dao.searchSaleTransaction(transactionId);
    
            if(checkTransaction==null){
                return -1;
            }
        }catch(DAOException e){
            System.out.println(e);
        }

        return (int) saleTransaction.getPrice() / 10;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId)
            throws InvalidTransactionIdException, UnauthorizedException {
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
        			throw new UnauthorizedException();
        }
        if (saleTransaction.getTicketNumber() != transactionId)
            return false;
        if (saleTransaction_state == Constants.CLOSED)
            return false;

        double price = 0;
        for (TicketEntry te : saleTransaction.getEntries())
            price += ((1 - te.getDiscountRate()) * te.getPricePerUnit()) * te.getAmount();

        price = (1 - saleTransaction.getDiscountRate()) * price;
        saleTransaction.setPrice(price);
        
        boolean state = false;
        try {
            state = dao.storeSaleTransaction(saleTransaction);
        } catch (DAOException e) {
            System.out.println(e);
        }

        return state;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber)
            throws InvalidTransactionIdException, UnauthorizedException {
        if (saleNumber == null || saleNumber <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }

        boolean state = false;
        SaleTransaction st = null;
        try {
            st = dao.searchSaleTransaction(saleNumber);
            System.out.println("dopo ricerca " + st == null);
        } catch (DAOException e) {
            System.out.println(e);
        }

        if (st == null || st.getPayed())
            return false;

        try {
            state = dao.removeSaleTransaction(saleNumber);

        } catch (DAOException e) {
            System.out.println(e);
        }
        
      //increase product availability
      		for (TicketEntry te : saleTransaction.getEntries()) {
      			try {
      				ProductType pt = dao.getProductTypeByBarCode(te.getBarCode());
                      dao.updateQuantity(pt.getId(), te.getAmount());
                  } catch (DAOException e) {
                      System.out.println(e);
                      return false;
                  }
      		}
        
        return state;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId)
            throws InvalidTransactionIdException, UnauthorizedException {

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }

        SaleTransaction st = null;
        try {
            st = dao.searchSaleTransaction(transactionId);
        } catch (DAOException e) {
            System.out.println(e);
        }
        
        return st;
    }

    @Override
    public Integer startReturnTransaction(Integer transactionId)
            throws InvalidTransactionIdException, UnauthorizedException {
    	if (runningUser==null  || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	if(transactionId == null || transactionId <= 0) {
    		throw new InvalidTransactionIdException();
    	}
    	
    	//System.out.println("checks transaction id ok");
    	
        Integer return_transaction_id = -1;
        
        SaleTransaction s = this.getSaleTransaction(transactionId);
        
        if(s != null) {
        	if(!s.getPayed()) {
        		return -1;
        	}
	        try {
	            return_transaction_id = dao.insertReturnTransaction();
	            
	        } catch (DAOException e) {
	            System.out.println(e);
	        }
	        
	        returnTransaction = new ConcreteReturnTransaction(return_transaction_id+1, transactionId, new ArrayList<TicketEntry>(), 0.0, s.getDiscountRate());
	        //System.out.println("returnTransaction created, number: " + return_transaction_id + " " + returnTransaction.getReturnId());
	        return returnTransaction.getReturnId();
        }
        return -1;
       
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException,
            InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
    	
    	if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && 
    			!runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	if(returnId==null || returnId<=0) {
    		throw new InvalidTransactionIdException();
    	}
    	if(amount<=0) {
    		throw new InvalidQuantityException();
    	}
    	if (productCode == null || productCode.isEmpty()  || !o.isValidCode(productCode)) { // manca invalid 
    		throw new InvalidProductCodeException();
        }
    	
    	if (returnTransaction == null || !returnTransaction.getReturnId().equals(returnId)) {
    		return false;
    	}
    	List<TicketEntry> soldProducts = new ArrayList<TicketEntry>();
        try {
        	soldProducts = dao.getEntries(returnTransaction.getTransactionId());
        } catch (DAOException e) {
            System.out.println(e);
        }
    	
    	TicketEntry prodToReturn=null;
    	for (TicketEntry prod : soldProducts) {
    		if(prod.getBarCode().equals(productCode)) {
    			prodToReturn=prod;
    		}
    	}
    	if(prodToReturn == null || prodToReturn.getAmount()<amount) {
    		return false;
    	}
    	
    	// add to list
        boolean toAdd = true;
        for (TicketEntry t : returnTransaction.getEntries()) {
        	//System.out.println("barcode prod da aggiungere: " + productCode);
        	//System.out.println("t attuale: " + t.getBarCode());
            if (t.getBarCode().equals(productCode)) {
                t.setAmount(t.getAmount() + amount);
                toAdd = false;
                break;
            }
        }
        //System.out.println("toAdd: " + toAdd);
        if (toAdd) {
        	prodToReturn.setAmount(amount);
            returnTransaction.getEntries().add(prodToReturn);
        }
    	return true;
    }

    @Override
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException {
    	if (returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException();
        }
    	
    	if(RFID == null || RFID.isEmpty() || RFID.length() != 12) {
    		throw new InvalidRFIDException();
    	}
    	
    	if(RFID != null && !RFID.isEmpty()) {
    		try {
    			Integer.parseInt(RFID);
    		} catch(Exception e) {
    			throw new InvalidRFIDException();
    		}
    	}
    	
    	if (runningUser == null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if (returnTransaction == null || !returnTransaction.getReturnId().equals(returnId)) {
    		return false;
    	}
    	
    	List<Product> soldProducts= new ArrayList<Product>();
        try {
        	soldProducts = dao.getSoldProducts(returnTransaction.getTransactionId());
        } catch (DAOException e) {
            System.out.println(e);
        }
    	
    	Product prodToReturn = null;
    	for (Product prod : soldProducts) {
    		if(prod.getRFID().equals(RFID)) {
    			prodToReturn = prod;
    		}
    	}
    	if(prodToReturn == null || prodToReturn.getTransactionId() == null) {
    		return false;
    	}
    	
    	List<TicketEntry> ticketEntries = null;
		try {
			ticketEntries = dao.getEntries(returnTransaction.getTransactionId());
		} catch (DAOException e) {
			e.printStackTrace();
		}
    	
    	TicketEntry te = null;
    	for (TicketEntry prod : ticketEntries) {
    		if(prod.getBarCode().equals(prodToReturn.getBarCode())) {
    			te=prod;
    		}
    	}
    	if(te == null || te.getAmount() < 1) {
    		return false;
    	}
    	
    	// add to list
        boolean toAdd = true;
        
        for (TicketEntry t : returnTransaction.getEntries()) {
            if (t.getBarCode().equals(prodToReturn.getBarCode())) {
                t.setAmount(t.getAmount() + 1);
                toAdd = false;
                break;
            }
        }
        
        if (toAdd) {
        	te.setAmount(1);
        	returnTransaction.getEntries().add(te);        
        }
        
        returnTransaction.getReturnProducts().add(prodToReturn);
        
        return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit)
            throws InvalidTransactionIdException, UnauthorizedException {
    	
    	if (runningUser==null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	if(returnId==null || returnId <= 0) {
    		throw new InvalidTransactionIdException();
    	}
    	
    	if (returnTransaction==null || returnTransaction.getReturnId()!=returnId) 
    		return false;
    	
    	//rollback
    	if(commit==false) 
    		returnTransaction=null;
    	//commit
    	else {
    		//increase product availability
    		for (TicketEntry te : returnTransaction.getEntries()) {
    			try {
    				ProductType pt = dao.getProductTypeByBarCode(te.getBarCode());
                    dao.updateQuantity(pt.getId(), te.getAmount());
                } catch (DAOException e) {
                    System.out.println(e);
                    return false;
                }
    		}
            
    		
    		// calculate price for return transaction
            double price = 0;
            for (TicketEntry te : returnTransaction.getEntries())
                price += ((1 - te.getDiscountRate()) * te.getPricePerUnit()) * te.getAmount();

            price = (1 - returnTransaction.getDiscountRate()) * price;
            returnTransaction.setPrice(price);
            
            //update sale transaction (product sold + final price)
            // 1. update final price
            try {
                dao.updateSaleTransactionPrice(returnTransaction.getTransactionId(), price,Constants.COMMITTED);		
            } catch (DAOException e) {
                System.out.println(e);
            }
            // 2. update ticket entries
            try {
                dao.updateSaleTransactionEntries(returnTransaction.getTransactionId(), returnTransaction.getEntries(), Constants.COMMITTED);		
            } catch (DAOException e) {
                System.out.println(e);
            }
            
            //insert return transaction in db + returned products to return_ticket_entry
            try {
                dao.storeReturnTransaction(returnTransaction);		
            } catch (DAOException e) {
                System.out.println(e);
            }
            returnTransaction=null;
    	}
    	
    	return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId)
            throws InvalidTransactionIdException, UnauthorizedException {
    	
    	if (runningUser==null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	if(returnId==null || returnId<=0 ) {
    		throw new InvalidTransactionIdException();
    	}
    	
    	ReturnTransaction rt=null;
    	try {
    		rt=dao.searchReturnTransaction(returnId);
    	} catch (DAOException e) {
    		System.out.println(e);
    	}
    	if (rt==null || rt.getPayed())
    		return false;
    	
    	boolean state = false;
        System.out.println("entro qui");
        System.out.println(returnId);
    	try {
            state = dao.deleteReturnTransaction(returnId);						//deletes entry in return_transaction + all related entries in return_ticket_entry
    		
        } catch (DAOException e) {
            System.out.println(e);
        }
        
        //update sale transaction (product sold + final price)
        // 1. update final price

    	System.out.println("value: " + rt.getReturnId());
        System.out.println("state returned: " + state);
        System.out.println("pric: " + rt.getPrice());
        System.out.println("id: " + returnTransaction.getTransactionId());
        System.out.println("done");
        try {
            dao.updateSaleTransactionPrice(returnTransaction.getTransactionId(), rt.getPrice(), Constants.NOT_COMMITTED);		
        } catch (DAOException e) {
            System.out.println(e);   
        }
        
        // 2. update ticket entries
        //System.out.println(returnTransaction.getTransactionId());
        try {
            dao.updateSaleTransactionEntries(returnTransaction.getTransactionId(), rt.getEntries(), Constants.NOT_COMMITTED);		
        } catch (DAOException e) {
        	System.out.println(e);
        }
        
        //decrease product availability
        System.out.println("updating");
		for (TicketEntry te : returnTransaction.getEntries()) {
			try {
				ProductType pt = dao.getProductTypeByBarCode(te.getBarCode());
                dao.updateQuantity(pt.getId(), -te.getAmount());
                System.out.println("updated");
			} catch (DAOException e) {
                System.out.println(e);
                return false;
            }
		}
        
        return state;
    }

    @Override
    public double receiveCashPayment(Integer transactionId, double cash)
            throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
    	
    	if (runningUser==null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if(transactionId==null || transactionId<=0) {
    		throw new InvalidTransactionIdException("Invalid TransactionId");
    	}
    	if(cash<=0) {
    		throw new InvalidPaymentException("Invalid Payment");
    	}
    	
    	SaleTransaction s=null;
    	try {
            s = dao.searchSaleTransaction(transactionId);
        } catch (DAOException e) {
            System.out.println(e);
        }
    	
    	if(s==null || s.getPrice()>cash)
    		return -1;
    	if(s.getPayed()) {
    		return -1;
    	}
    	//update the db: the sale transaction is payed
    	try {
    		dao.setSaleTransactionPaid(transactionId);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
    	//add balanceOperation
    	try {
    		dao.insertBalanceOperation(s.getPrice(), Constants.SALE, null);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
        return cash-s.getPrice();
    }

    @Override
    public boolean receiveCreditCardPayment(Integer transactionId, String creditCard)
            throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
    	
    	if(creditCard==null || creditCard.isEmpty() || !o.luhnCheck(creditCard)) {			
    		throw new InvalidCreditCardException("");
    	}
    	
    	if (runningUser==null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if(transactionId==null || transactionId<=0) {
    		throw new InvalidTransactionIdException();
    	}
    	
    	SaleTransaction s=null;
    	try {
            s = dao.searchSaleTransaction(transactionId);
        } catch (DAOException e) {
            System.out.println(e);
        }
    	
    	if(s==null)
    		return false;
    	if(s.getPayed()) {
    		return false;
    	}
    	//check existence of credit card and if it has enough money and update amount of money on credit card 
    	if(o.checkCreditCardAmount(creditCard, s.getPrice(), true)) {
    		if(!o.updateCreditCardAmount(creditCard, s.getPrice(), true))
    			return false;
    	}
    	else
    		return false;
    	
    	
    	//update the db: the sale transaction is payed
    	try {
    		dao.setSaleTransactionPaid(transactionId);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
    	//add balanceOperation
    	try {
    		dao.insertBalanceOperation(s.getPrice(), Constants.SALE, null);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
    	if (runningUser==null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if(returnId==null || returnId<=0) {
    		throw new InvalidTransactionIdException();
    	}
    	
    	ReturnTransaction r=null;
    	try {
            r = dao.searchReturnTransaction(returnId);
        } catch (DAOException e) {
            System.out.println(e);
        }

    	if(r==null) {
    		return -1;
    	}
    	
    	if(r.getPayed()) {

        	return -1;
        }
    	//update the db: the return transaction is payed
    	try {
    		dao.setReturnTransactionPaid(returnId);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
    	//add balanceOperation
    	try {
    		dao.insertBalanceOperation(r.getPrice(), Constants.RETURN, null);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
        return r.getPrice();
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard)
            throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
    	if (runningUser==null || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER)
                && !runningUser.getRole().equals(Constants.CASHIER))) {
            throw new UnauthorizedException();
        }
    	
    	if(returnId==null || returnId<=0 ) {
    		throw new InvalidTransactionIdException("Invalid Transaction Id");
    	}
    	
    	if(creditCard==null || creditCard.isEmpty() || !o.luhnCheck(creditCard)) {			
    		throw new InvalidCreditCardException("Invalid Credit Card");
    	}
    	
    	ReturnTransaction r=null;
    	try {
            r = dao.searchReturnTransaction(returnId);
        } catch (DAOException e) {
            System.out.println(e);
        }
    	
    	if(r==null)
    		return -1;
    	
    	if(r.getPayed()) {
        	return -1;
        }
    	//check existence of credit card and update balance of credit card
    	if(o.checkCreditCardAmount(creditCard, r.getPrice(), false)) {
    		if(!o.updateCreditCardAmount(creditCard, r.getPrice(), false))
    			return -1;
    	}
    	else
    		return -1;
    	
    	//update the db: the return transaction is payed
    	try {
    		dao.setReturnTransactionPaid(returnId);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
    	//add balanceOperation
    	try {
    		dao.insertBalanceOperation(r.getPrice(), Constants.RETURN, null);
    	} catch (DAOException e) {
            System.out.println(e);
        }
    	
        return r.getPrice();
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        String type;
        double future_balance;
        if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
        future_balance = this.computeBalance();
        future_balance += toBeAdded;
        if (future_balance < 0)
            return false;
        if (toBeAdded >= 0)
            type = "CREDIT";
        else
            type = "DEBIT";
        boolean state = false;
        try {
            state = dao.insertBalanceOperation(Math.abs(toBeAdded), type, null);
        } catch (Exception e) {
            System.out.println(e);
        }
        return state;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
    	if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
            throw new UnauthorizedException();
        }
    	
    	if(from == null)
    		from = LocalDate.of(1900, 1, 1);
    	if(to == null)
    		to = LocalDate.of(2100, 1, 1);
    	if(from.isAfter(to)) {
            LocalDate tmp = from;
            from = to;
           	to = tmp;
        }
       
    	List<BalanceOperation> balanceOperationList = new ArrayList<>();
        try {
            balanceOperationList = dao.getBalanceOperations(from, to);
        } catch (DAOException e) {
            System.out.println("getBalanceOperations exception");
        }
        
        return balanceOperationList;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        List<BalanceOperation> balanceOperationList = new ArrayList<>();
        double balance = 0;
        if ((runningUser == null) || (!runningUser.getRole().equals(Constants.ADMINISTRATOR)
                && !runningUser.getRole().equals(Constants.SHOP_MANAGER))) {
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
    
    public User getRunningUser() {
    	return this.runningUser;
    }
    
    public void setRunningUser(User user) {
    	this.runningUser = user;
    }

    public SaleTransaction getSaleTransaction(){
        return this.saleTransaction;
    }

    public void setSaleTransaction(SaleTransaction st){
        this.saleTransaction= st;
    }
    
    public boolean getSaleTransactionState(){
        return this.saleTransaction_state;
    }

    public void setSaleTransactionState(boolean state){
        this.saleTransaction_state= state;
    }

    public ReturnTransaction getReturnTransaction() {
    	return this.returnTransaction;
    }
    
    public void setReturnTransaction(ReturnTransaction r) {
    	this.returnTransaction = r;
    }

    public IDAOEZshop getDAO(){
        return this.dao;
    }

    public void setDAO(IDAOEZshop dao){
        this.dao= dao;
    }

    
}
