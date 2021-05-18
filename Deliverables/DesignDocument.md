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
 userList: List<User>
 productTypeList: List<ProductType>
 ordersList: List<Order>
 customersList: List<Customer>
 accountBook: AccountBook
 saleTransactionsList: List<SaleTransaction>
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
 updateQuantity(productId:Integer, toBeAdded:int): boolen
 updatePosition(productId:Integer, newPos:String): boolean
 issueOrder(productCode:String, quantity:int, pricePerUnit:double): Integer
 payOrderFor(productCode:String, quantity:int, pricePerUnit:double): Integer
 payOrder(orderId:Integer): boolean
 recordOrderArrival(orderId:Integer): boolean
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
 deleteProductFromSale(transactionId:Integer, productCode:String, amount:int): boolean
 applyDiscountRateToProduct(transactionId:Integer, productCode:String, discountRate:Double): boolean
 applyDiscountRateToSale(transactionId:Integer, discountRate:Double): boolean
 computePointsForSale(transactionId:Integer): int
 endSaleTransaction(transactionId:Integer): boolean
 deleteSaleTransaction(transactionID:Integer): boolean
 getSaleTransaction(transactionID:Integer): SaleTransaction
 startReturnTransaction(transactionID:Integer): Integer
 returnProduct(returnId:Integer, productCode String, amount:int): boolean
 endReturnTransaction(returnId:Integer, commit:boolean): boolean
 deleteReturnTransaction(returnId:Integer): boolean
 receiveCashPayment(transactionID:Integer, cash:doube): double
 receiveCreditCardPayment(transactionID:Integer, creditCard:String): boolean
 returnCreditCardPayment(returnId:Integer, crediCard:String): double
 recordBalanceUpdate(toBeAdded:double): boolean
 getCreditsAndDebits(from:LocalDate, to:LocalDate): List<BalanceOperation>
 computeBalance(): double
}


class AccountBook {
 finalBalance: double
 balanceOperationsList: List<BalanceOperation>
 getCreditsAndDebits(from:LocalDate, to:LocalDate): List<BalanceOperation>
}

class BalanceOperation{
 description: String
 amount: double
 date: Date
}

class ReturnTransaction{
 returnTransactionId: Integer
 returnedValue: double
 productToReturn: List<ProductType>
 saleTransaction: SaleTransaction
}

class SaleTransaction{
 saleTransactionId: Integer
 pointsOfSale: int
 cost: double
 paymentType: String
 discountRare: double
 loyalityCard: Card
 ProductToSale: List<ProductType>
}

class Order {
 orderId: Integer
 status: String
 quantity: int
 pricePerUnit: double
 supplier: String
 productType: ProductType
}

class ProductType {
 productTypeId: Integer
 description: String
 barCode: String
 notes: String
 quantity: int
 sellPrice: double
 discountRate: double
 location: String
}

class Card {
 cardId: String
 points: int
}

class Customer {
 customerId: Integer
 name: String
 surname: String
 card: Card
}

class User{
 username: String
 password: String
 role: String
 id: Integer
}

class ShopManager{}

class Admin{}

AccountBook --> "*" BalanceOperation
SaleTransaction --|> BalanceOperation
ReturnTransaction --|> BalanceOperation
Order --|> BalanceOperation
ReturnTransaction "0..*" --> SaleTransaction
ReturnTransaction --> "*" ProductType
SaleTransaction--> "*" ProductType
Order "0...*" <--> ProductType
SaleTransaction --> "0..1" Card
Customer --> "0..1" Card
ShopManager --|> User
Admin --|> ShopManager
EZShop --> AccountBook
EZShop --> "*" User
EZShop --> "*" Customer
EZShop --> "*" ProductType
EZShop --> "*" Order
EZShop --> "*" SaleTransaction
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
