package it.polito.ezshop.data;

import it.polito.ezshop.Constants;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.ConcreteProductType;
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


public class EZShop implements EZShopInterface {

    private IDAOEZshop dao = new DAOEZShop();
    private User runningUser = null;

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
        if (productCode == null || productCode.isEmpty()) {
            throw new InvalidProductCodeException();
        }
        try {
            int tmp = Integer.parseInt(productCode);
        } catch (Exception e) {
            throw new InvalidProductCodeException();
        }
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }

        ProductType productType = new ConcreteProductType(null, description, productCode, note, null, pricePerUnit,
                null, null);
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
        if (newCode == null || newCode.isEmpty()) {
            throw new InvalidProductCodeException();
        }
        //valid bar code
        try {
            int tmp = Integer.parseInt(newCode);
        } catch (Exception e) {
            throw new InvalidProductCodeException();
        }
        if ( newPrice <= 0) {
            throw new InvalidPricePerUnitException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        try {
        	ProductType p = new ConcreteProductType(id, newDescription,newCode, newNote, null, newPrice, null, null);
        	return dao.updateProduct(p);
        } catch (Exception e) {
			System.out.println(e);
        }
        return false;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        if(id == null || id <= 0) {
        	throw new InvalidProductIdException();
        }
        if (!runningUser.getRole().equals(Constants.ADMINISTRATOR) && !runningUser.equals(Constants.SHOP_MANAGER)) {
            throw new UnauthorizedException();
        }
        boolean delete = false;
        try {
        	delete = dao.deleteProductType(id);
        	delete = true;
        }catch (Exception e) {
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
            if (dao.searchPosition(newPos)) {
                return false;
            }
        } catch (DAOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return true;
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

        //call the db
        /*
        boolean cardInsertion= false;
        try{
            cardInsertion = dao.createNewCard(generatedString);

        }catch(DAOException e){
            System.out.println("db excepiton");
        
        }

        if(cardInsertion ==false){
            return "";
        }else{
            return generatedString;
        }*/
        return generatedString;

    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        
        if(runningUser==null |!runningUser.getRole().equals(Constants.ADMINISTRATOR) 
            && !runningUser.getRole().equals(Constants.SHOP_MANAGER) 
            && !runningUser.getRole().equals(Constants.CASHIER)) {
            throw new UnauthorizedException();
        }
        if(customerId== null | customerId<= 0){
            throw new InvalidCustomerIdException();
        }
        if(customerCard== null| customerCard.isEmpty() | customerCard.length()!=10){
            throw new InvalidCustomerCardException();
        }

        boolean result= false;
        try{
            result= dao.bindCardToCustomer(customerCard, customerId);

        }catch(DAOException e){
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

        boolean modification= false;
        try{
            modification= dao.updatePoints(customerCard, pointsToBeAdded);

        }catch(DAOException e){
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

        boolean state = false;
        try {
            state = dao.insertProductToSale(transactionId, productCode, amount);
        } catch (Exception e) {
            System.out.println(e);
        }
        return state;

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
        boolean state = false;
        try {
            state = dao.removeProductToSale(transactionId, productCode, amount);
        } catch (Exception e) {
            System.out.println(e);
        }
        return state;
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

        SaleTransaction sT = null;
        try {
            sT = dao.selectSaleTransaction(transactionId);
        } catch (DAOException e) {
           System.out.print(e);
        }
        return sT;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber)
            throws /* InvalidTicketNumberException, */InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException,
            InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
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
        return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        return null;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }
}
