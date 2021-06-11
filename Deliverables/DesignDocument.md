# Design Document 


Authors: Marco Sapio, Marta Caggiano, Emma Marrocu, Francesco Rabezzano

Date: April 2021

Version: 1.0.0


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# High level design 

![](diagramsImages/package_diagram.png)





# Low level design


``` plantuml 
@startuml
class EZShop{
 runningUser: User
 returnTransaction: ReturnTransaction
 saleTransaction: SaleTransaction
 saleTransaction_state: boolean 
 returnTransaction_state: boolean 
 reset(): void
 createUser(username:String, password:String,role:String): Integer
 deleteUser(id:Integer): boolean
 getAllUsers(): List<User>
 getUser(id:Integer): User
 updateUserRights(id:Integer, role:String): boolean
 login(username:String, password:String): User
 logout(): boolean
 createProductType(description:String, productCode:String, pricePerUnit:double, note:String): Integer
 updateProduct(id:Integer, newDescription:String, newCode:String, newPrice:String, newNote:String): boolean
 deleteProductType(id:Integer): boolean
 getAllProductTypes(): List<ProductType>
 getProductTypeBarCode(barCode:String): ProductType
 getProductTypesByDescription(description:String): List<ProductType>
 updateQuantity(productId:Integer, toBeAdded:int): boolean
 updatePosition(productId:Integer, newPos:String): boolean
 issueOrder(productCode:String, quantity:int, pricePerUnit:double): Integer
 payOrderFor(productCode:String, quantity:int, pricePerUnit:double): Integer
 payOrder(orderId:Integer): boolean
 recordOrderArrival(orderId:Integer): boolean
 recordOrderArrivalRFID(orderId: Integer , RFIDfrom: String): boolean
 getAllOrders(): List<Order>
 getOrder(): Order
 defineCustomer(customerName:String): Integer
 modifyCustomer(id:Integer, newCustomerName:String, newCustomerCard:String): boolean
 deleteCustomer(id:Integer): boolean
 getCustomer(id:Integer): Customer
 getAllCustomers(): List<Customers>
 createCard(): String
 attachCardToCustomer(customerCard:String, customerId:String): boolean
 modifyPointsOnCard(customerCard:String, pointsToBeAdded:int): boolean
 startSaleTransaction(): Integer
 applyDiscountToProduct(transactionId:Integer, discountRate:double): boolean
 addProductToSale(transactionId:Integer, productCode:String, amount:int): boolean
 addProductToSaleRFID(transactionId: Integer , RFID: String) : boolean
 deleteProductFromSale(transactionId:Integer, productCode:String, amount:int): boolean
 deleteProductFromSaleRFID(transactionId: Integer, RFID: String): boolean
 applyDiscountRateToProduct(transactionId:Integer, productCode:String, discountRate:Double): boolean
 applyDiscountRateToSale(transactionId:Integer, discountRate:Double): boolean
 computePointsForSale(transactionId:Integer): int
 endSaleTransaction(transactionId:Integer): boolean
 deleteSaleTransaction(transactionID:Integer): boolean
 getSaleTransaction(transactionID:Integer): SaleTransaction
 startReturnTransaction(transactionID:Integer): Integer
 returnProduct(returnId:Integer, productCode String, amount:int): boolean
 returnProductRFID(returnId: Integer , RFID: String): boolean
 endReturnTransaction(returnId:Integer, commit:boolean): boolean
 deleteReturnTransaction(returnId:Integer): boolean
 receiveCashPayment(transactionID:Integer, cash:doube): double
 receiveCreditCardPayment(transactionID:Integer, creditCard:String): boolean
 returnCreditCardPayment(returnId:Integer, crediCard:String): double
 recordBalanceUpdate(toBeAdded:double): boolean
 getCreditsAndDebits(from:LocalDate, to:LocalDate): List<BalanceOperation>
 computeBalance(): double
}

class BalanceOperation{
 balanceId: Integer;
 type: String
 money: double
 date: Date
}

class ReturnTransaction{
 transactionId: Integer
 returnId: Integer
 price: double
 entries: List<TicketEntry>
 discountRate: double
 payed: boolean
 returnProducts: List<Product>
}

class Product {
 RFID: String
 barCode: String
 transactionId: Integer
}

class SaleTransaction{
 ticketNumber: Integer
 price: double
 discountRate: double
 entries: List<TicketEntry>
 payed: boolean
 saleProducts: List<Product>
}

class Order {
 orderId: Integer
 status: String
 quantity: int
 pricePerUnit: double
 balanceId: Integer
 productCode: String
}

class ProductType {
 id: Integer
 productDescription: String
 barCode: String
 note: String
 quantity: Integer
 pricePerUnit: Double
 location: String
}

class Customer {
 Id: Integer
 customerName: String
 customerCard: String
 points: Integer
}

class User{
 username: String
 password: String
 role: String
 id: Integer
}

class TicketEntry {
    barCode : String
    productDescription: String 
    amount: int
    pricePerUnit: double 
    discountRate double
}

ReturnTransaction -> "*" TicketEntry
SaleTransaction -> "*" TicketEntry
EZShop --> "*" User
EZShop --> "*" Customer
EZShop --> "*" ProductType
EZShop --> "*" Order
EZShop --> "*" SaleTransaction
EZShop --> "*" BalanceOperation
Product "*" -->  ProductType
SaleTransaction --> "*" Product
ReturnTransaction --> "*" Product
@enduml
```







# Verification traceability matrix

![](diagramsImages/matrix1.png)
![](diagramsImages/matrix2.png)


# Verification sequence diagrams 

#### Scenario 1.1

![](sequenceDiagrams/Scenario_1.1.png)

#### Scenario 2.2

![](sequenceDiagrams/Scenario_2.3.png)

#### Scenario 3.1

![](sequenceDiagrams/Scenario_3.1.png)

#### Scenario 3.3

![](sequenceDiagrams/Scenario_3.3.png)

#### Scenario 6.1 + 7.1

![](sequenceDiagrams/Scenario_6.1_7.1.png)

#### Scenario 6.1 + 7.4

![](sequenceDiagrams/Scenario_6.1_7.4.png)

#### Scenario 8.1

![](sequenceDiagrams/Scenario_8.1.png)

#### Scenario 9.1

![](sequenceDiagrams/Scenario_9.1.png)
