# Integration and API Test Documentation

Authors: Marta Caggiano, Francesco Rabezzano, Marco Sapio, Emma Marrocu

Date: 26/05/2021

Version: 1.0.1

# Contents

- [Dependency graph](#dependency graph)

- [Integration approach](#integration)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph 

     <report the here the dependency graph of the classes in EzShop, using plantuml>
``` plantuml 
@startuml
class EZShop
Interface BalanceOperation
Interface Customer
Interface Order
Interface ProductType
Interface ReturnTransaction
Interface SaleTransaction
Interface TicketEntry
Interface User

EZShop --> DAOEZShop
EZShop --> BalanceOperation
EZShop --> Customer
EZShop --> ProductType
EZShop --> ReturnTransaction
EZShop --> SaleTransaction
EZShop --> TicketEntry
EZShop --> Order
EZShop --> User
@enduml          
```

# Integration approach

    The integration sequence adopted is Bottom Up.



#  Tests

   <define below a table for each integration step. For each integration step report the group of classes under test, and the names of
     JUnit test cases applied to them> JUnit test classes should be here src/test/java/it/polito/ezshop

## Step 1
| Classes           | JUnit test cases |
|--|--|
|BalanceOperation|testSetAndGetBalanceId(), testSetAndGetDate(), testSetAndGetMoney(), testSetAndGetType()|
|Customer|testSetAndGetCostumerName(), testSetAndGetCostumerCard(), testSetAndGetId(), testSetAndGetPoints()|
|Order|testOrderSetBalanceId(), testOrderSetProductCode(), testOrderSetPricePerUnit(), testOrderSetQuantity(), testOrderSetStatus(), testOrderSetOrderId()|
|ProductType|testProductTypeSetQuantity(), testProductTypeSetLocation(),  testProductTypeSetNote(), testProductTypeSetProductDescription(), testProductTypeSetBarCode(), testProductTypeSetPricePerUnit(), testProductTypeSetId()|
|ReturnTransaction|testReturnTransactionSetReturnId(), testReturnTransactionSetTransactionId(), testReturnTransactionSetEntries(), testReturnTransactionSetPrice(), testReturnTransactionSetPayed(), testReturnTransactionSetDiscountRate()|
|SaleTransaction|testSaleTransacionSetTicketNumber(), testSaleTransactionSetEntries(), testSaleTransactionSetDiscountRate(), testSaleTransactionSetPrice(), testSaleTransactionSetPayed()|
|TicketEntry|testTicketEntrySetBarCode(), testTicketEntrySetProductDescription(), testTicketEntrySetAmount(), testTicketEntrySetPricePerUnit(), testTicketEntrySetDiscountRate()|
|User|testUserSetId(), testUserSetUsername(), testUserSetPassword(), testUserSetRole()|


## Step 2
| Classes                      | JUnit test cases                                             |
|--|--|
|User + DAOEZShop|testInsertAndRemoveUser, testGetAllUsers, testSearchUserById, testUpdateRights, testSearchUserInvalid, testSearchUserNull, testGetAllUser, testSearchUserValid|
|ProductType+ DAOEZShop|testCreateProductTypeValid, testGetProductTypeByBarCodeInvalid, testGetProductTypeByBarCodeValid, testGetProductTypeByBarCodeNullProduct, testGetProductTypeByDescriptionInvalid, testGetProductTypeByDescriptionValid, testUpdateQuantityInvalidQuantity, testUpdateQuantityValid, testUpdatePositionValid, testSearchPositionInvalid, testSearchPositionAlreadyExist, testSearchPositionValid, testUpdateProductInvalid, testUpdateProductUnsuccess, testUpdateProductSuccess, testDeleteProductTypeUnsucess, testDeleteProductTypeSuccess, testSearchProductByIdUnsuccess, testSearchProductByIdSuccess, testGetAllProductTypetValid|
|TicketEntry+ DAOEZShop|testSearchTicketEntry, testSearchTicketEntryEmpty|
|Order + DAOEZShop|testInsertNewOrderProductNotExists, testInsertNewOrderValid, testPayOrderDirectlyProductNotExists, testPayOrderDirectlyValid, testPayOrderOrderNotExists, testPayOrderIssued, testPayOrderPayed, testPayOrderCompleted, testRecordArrivalOrderNotExists, testRecordArrivalValid, testGetOrderOrderNotExists, testGetOrderValid, testGetAllOrdersValid,|
|Customer+ DAOEZShop|testInsertCustomerButAlreadyExists, testInsertCustomerValid, testUpdateCustomerInvalidCard, testUpdateCustomerCardAlreadyExists, testUpdateCustomerValid, testDeleteCustomerButNotExists, testDeleteCustomerValid, testGetCustomerButNotExists, testGetCustomerValid, testGetAllCustomers, testBindCardToCustomerButNotExists, testBindCardToCustomerCardAlreadyExists, testBindCardToCustomerValid, testUpdatePointsCustomerNotExists, testUpdatePointsButNotEnough, testUpdatePointsValid,|
|SaleTransaction + DAOEZShop|testInsertSaleTransaction, testSetTransactionPaid, testSetTransactionPaidInvalid, testRemoveSaleTransactionInvalid, testRemoveSaleTransactionValid, testUpdateSaleTransactionPrice,|
|ReturnTransaction+ DAOEZShop|testInsertReturnTransactionTest, testDeleteReturnTransaction, testDeleteReturnTransactionFailed, testSearchReturnTransaction, testSearchReturnTransactionNull, testStoreReturnTransaction, testSetReturnTransactionPaid,|
|BalanceOperation+ DAOEZShop|testInsertBalanceOperation, testGetBalanceOperations|


## Step 3

| Classes                              | JUnit test cases                                             |
|--|--|
|User + DAOEZShop +EZShop|testUserInvalidUsername, testUserInvalidPassword, testCreateUserCheckRole, testUserAlreadyExists, testUserNotExists, testGetAllUsers,testUserInvalidId,  testUserUnauthorizedUser, testCreateUserWithSuccess, testDeleteUserWithSuccess,testGetUserWithSuccess , testUpdateUserRightsWithSuccess,testLoginWithSuccess, testLogoutWithSuccess|
|ProductType+ DAOEZShop+ EZShop|testCreateProductTypeInvalidDescription, testCreateProductTypeInvalidProductCode, testCreateProductTypeInvalidPricePerUnit, testCreateProductTypeUnauthorizedUser, testCreateProductTypeExistingProduct, testCreateProductTypeValidProduct, testUpdateProductInvalidId, testUpdateProductInvalidDescription, testUpdateProductInvalidProductCode, testUpdateProductInvalidPricePerUnit, testUpdateProductUnauthorizedException, testProductUpdateNotAvailableId, testProductUpdateExistingBarCode, testProductUpdateValid, testDeleteProductTypeInvalidId, testDeleteProductTypeUnauthorizedException, testDeleteProductNotExisting, testDeleteProductValidProduct, testGetAllProductTypeUnauthorizedException, testGetAllProductTypeValid, testGetProductTypeByBarCodeUnauthorizedException, testGetProductTypeByBarCodeInvalidBarCode, testGetProductTypeByBarCodeProductNotExists, testGetProductTypeByBarCodeValidProduct, testGetProductTypeByDescriptionUnauthorizedException, testGetProductTypeByDescriptionProductsNotExist, testGetProductTypeByDescriptionValid, testUpdateQuantityUnauthorizedUser, testUpdateQuantityInvalidProductId, testUpdateQuantityProductLocationNull, testUpdateQuantityProductLocationEmpty, testUpdateQuantityProductInvalidQuantity, testUpdateQuantityProductNotExists, testUpdateQuantityProductValidPositive, testUpdateQuantityProductValidNegative, testUpdatePositionUnauthorizedException, testUpdatePositionInvalidLocation, testUpdatePositionInvalidId, testUpdatePositionProductNotExist, testUpdatePositionAlreadyAssigned, testUpdatePositionValid, testUpdatePositionNull, testUpdatePositionEmpty,|
|Order+ DAOEZShop+ EZShop|testOrderUnauthorizedUser, testOrderInvalidProductCode, testOrderInvalidQUantity, testOrderInvalidPricePerQty, testOrderInvalidOrderId, testRecordOrderArrivalInvalidLocation, testOrderProductNotExists, testIssueOrderValidData, testPayOrderForBalanceNotEnough, testPayOrderForValidData, testOrderOrderNotExists, testPayOrderOrderNotIssuedOrOrdered, testPayOrderValidData, testRecordOrderArrivalOrderNotOrderedOrCompleted, testRecordOrderArrivalValidData, testGetAllOrdersValidData|
|Customer+ DAOEZShop+ EZShop|testCustomerUnauthorizedUser, testCustomerInvalidCustomerName, testCustomerInvalidCustomerCard, testCustomerInvalidCustomerId, testDefineCustomerNameAlreadyInUse, testCustomerCardAlreadyInUse, testCustomerCustomerNotExists, testModifyPointsOnCardInexistentCard, testModifyPointsOnCardNotEnoughPoints, testDefineCustomerValidData, testModifyCustomerValidData, testDeleteCustomerValidData, testGetCustomerValidData, testGetAllCustomersValidData, testCreateCardValidData, testAttachCardToCustomerValidData, testModifyPointsOnCardValidData, testCustomerDetachCard,|
|SaleTransaction+ DAOEZShop+ EZShop|testSaleTransactionUnauthorizedUser, testSaleTransactionInvalidTransactionId, testSaleTransactionInvalidProductCode, testSaleTransactionInvalidQuantity, testSaleTransactionInvalidDiscountRate, testSaleTransactionProductNotExists, testSaleTransactionNotExist, testStartSaleTransactionWithSuccess, testAddProductToSaleWithSuccess, testDeleteProductFromSaleWithSuccess, testEndSaleTransactionWithSuccess, testApplyDiscountRateToProductWithSuccess, testApplyDiscountRateToSaleWithSuccess, testdeleteSaleTransactionWithSuccess, testSaleTransactionWrongTransactionStatus, testGetSaleTransaction, testReceiveCashPaymentInvalidTransactionId, testReceiveCashPaymentInvalidUser, testReceiveCashPaymentInvalidCash, testReceiveCashPaymentReturnTransactionInexistent, testReceiveCashPaymentReturnTransactionNotEnded, testReceveCashPaymentReturnTransactionEndedAndNotPayed, testReceiveCashPaymentTransactionAlreadyPayed, testReceivenCreditCardPaymentInvalidReuturnId, testReceiveCreditCardPaymentInvalidUser, testReceiveCreditCardPaymentInvalidCard, testReceiveCreditCardPaymentTransactionInexistent, testReceiveCreditCardPaymentTransactionNotEnded, testReceiveCreditCardPaymentTransactionNotEnoughMoney, testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed, testReceiveCreditCardPaymentCardNotRegistered, testReceievCreditCardPaymentReturnTransactionAlreadyPayed,|
|ReturnTransaction+ DAOEZShop+ EZShop|testStartReturnTransactionNotFoundSale, testStartReturnTransactionInvalidId, testStartReturnTransactionClosedSale, testStartReturnTransactionNotClosedSale, testStartReturnTransactionNullRole, testReturnProductExceedAmount, testReturnProductTransactioNotExists, testReturnProductReturnNotExists, testReturnProductProductNotExists, testReturnProductAmountTooBig, testReturnProductCodeInvalid, testReturnProductNullUser, testReturnProductNegativeQuanitity, testReturnProductTransactionIdError, testReturnProductValidData, testEndReturnTransactionInvalidTransactionId, testEndReturnTransactionUserNull, testEndReturnTransactionInactiveReturnTransaction, testEndReturnTransactionCommitReturn, testEndReturnTransactionCommitReturnFalse, testDeleteReturnTransactionInvalidTransaction, testDeleteReturnTransactionInvalidUser, testDeleteReturnTransactionInvalid, testDeleteReturnTransactionValid, testReturnCashPaymentInvalidReutrnId, testReturnCashPaymentInvalidUser, testReturnCashPaymentReturnTransactionInexistent, testReturnCashPaymentReturnTransactionNotEnded, testReturnCashPaymentReturnTransactionEndedAndNotPayed, testReturnCashPaymentReturnTransactionAlreadyPayed, testReturnCreditCardPaymentInvalidReuturnId, testReturnCreditCardPaymentInvalidUser, testReturnCreditCardPaymentInvalidCard, testReturnCreditCardPaymentReturnTransactionInexistent, testReturnCreditCardPaymentReturnTransactionNotEnded, testReturnCreditCardPaymentReturnTransactionEndedAndNotPayed, testReturnCreditCardPaymentReturnCardNotRegistered, testReturnCreditCardPaymentReturnTransactionAlreadyPayed,|
|BalanceOperation+ DAOEZShop+ EZShop|testRecordBalanceUpdateCashierUser, testRecordBalanceUpdateNullUser, testRecordBalanceUpdateAddCredit, testRecordBalanceUpdateAddDebit, testRecordBalanceUpdateNegativeTotal, testGetCreditsAndDebitInvalidUser, testGetCreditsAndDebitNullList, testzGetCreditsAndDebitCorrectValue, testGetCreditsAndDebitWithDateExchanged, testGetCreditsAndDebitFromDateNull, testGetCreditsAndDebitFromToNull, testGetCreditsAndDebitFromAndToNull, testComputeBalanceUserNull, testComputeBalance,|




# Scenarios


<If needed, define here additional scenarios for the application. Scenarios should be named
 referring the UC in the OfficialRequirements that they detail>

## Scenario UCx.y

| Scenario |  name |
| ------------- |:-------------:|
|  Precondition     |  |
|  Post condition     |   |
| Step#        | Description  |
|  1     |  ... |
|  2     |  ... |



# Coverage of Scenarios and FR


<Report in the following table the coverage of  scenarios (from official requirements and from above) vs FR. 
Report also for each of the scenarios the (one or more) API JUnit tests that cover it. >




| Scenario ID | Functional Requirements covered | JUnit  Test(s) |
| ----------- | ------------------------------- | ----------- |
|  1-1     | FR3.1<br />FR4.2            | testCreateProductTypeValidProduct()<br />testUpdatePositionValid() |
| 1-2 | FR4.2<br />FR3.4 | testGetProductTypeByBarCodeValidProduct()<br />testUpdatePositionValid() |
| 1-3 | FR3.1<br />FR3.4 | testGetProductTypeByBarCodeValidProduct<br />testProductUpdateValid() |
|  3-1  |                | testIssueOrderValidData() |
| 3-2      | FR4.4 | testPayOrderValidData() |
| 3-3   | FR4.6 | testRecordOrderArrivalValidData() |
| 4-1     | FR5.1 | testDefineCustomerValidData() |
| 4-2      | FR5.6<br />FR5.5 | testAttachCardToCustomerValidData()<br />testCreateCardValidData() |
| 4-3 | FR5.1 | testCustomerDetachCard() |
| 4-4 | FR5.1 | testModifyCustomerValidData() |
| 5-1 |  | testLoginWithSuccess() |
| 5-2 |  | testLogoutWithSuccess() |
| 6-1 | FR6.1<br />FR6.2<br />FR4.1<br />FR7.1<br />FR7.2<br />FR8.2<br />FR6.10 | testStartSaleTransactionWithSuccess()<br />testAddProductToSaleWithSuccess()<br />testEndSaleTransactionWithSuccess()<br />testReceveCashPaymentSaleTransactionEndedAndNotPayed()<br />testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed()<br /> |
| 6-2 | FR6.1<br />FR6.2<br />FR4.1<br />FR6.5<br />FR6.10<br />FR7.1<br />FR7.2<br />FR8.2 | testStartSaleTransactionWithSuccess()<br />testAddProductToSaleWithSuccess()<br />testEndSaleTransactionWithSuccess()<br />testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed()<br />testReceveCashPaymentSaleTransactionEndedAndNotPayed()<br />testApplyDiscountRateToProductWithSuccess() |
| 6-3 | FR6.1<br />FR6.2<br />FR4.1<br />FR6.4<br />FR6.10<br />FR7.1<br />FR7.2<br />FR8.2 | testStartSaleTransactionWithSuccess()<br />testAddProductToSaleWithSuccess()<br />testEndSaleTransactionWithSuccess()<br />testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed()<br />testReceveCashPaymentSaleTransactionEndedAndNotPayed()<br />testApplyDiscountRateToSaleWithSuccess() |
| 6-4 | FR6.1<br />FR6.2<br />FR4.1<br />FR6.10<br />FR7.1<br />FR7.2<br />FR8.2<br />FR5.7 | testStartSaleTransactionWithSuccess()<br />testAddProductToSaleWithSuccess()<br />testEndSaleTransactionWithSuccess()<br />testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed()<br />testReceveCashPaymentSaleTransactionEndedAndNotPayed()<br />testModifyPointsOnCardValidData()<br />testComputePointsForSaleValid() |
| 6-5 | FR6.1<br />FR6.2<br />FR4.1<br />FR6.10<br />FR6.11 | testStartSaleTransactionWithSuccess()<br />testAddProductToSaleWithSuccess()<br />testEndSaleTransactionWithSuccess()<br />testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed()<br />testReceveCashPaymentSaleTransactionEndedAndNotPayed()<br />testdeleteSaleTransactionWithSuccess() |
| 6-6 | FR6.1<br />FR6.2<br />FR4.1<br />FR6.10<br />FR7.1<br />FR8.2 | testStartSaleTransactionWithSuccess()<br />testAddProductToSaleWithSuccess()<br />testEndSaleTransactionWithSuccess()<br />testReceveCashPaymentSaleTransactionEndedAndNotPayed() |
| 7-1 | FR7.2 | testReceiveCreditCardPaymentSaleTransactionEndedAndNotPayed()<br />testReturnCreditCardPaymentReturnTransactionEndedAndNotPayed() |
| 7-2 | FR7.2 | testReturnCreditCardPaymentInvalidCard()<br />testReceiveCreditCardPaymentInvalidCard() |
| 7-3 | FR7.2 | testReceiveCreditCardPaymentTransactionNotEnoughMoney() |
| 7-4 | FR7.1 | testReceveCashPaymentSaleTransactionEndedAndNotPayed() |
| 8-1 | FR6.12<br />FR6.13<br />FR6.14<br />FR6.15<br />FR4.1<br />FR8.1 | testStartReturnTransactionNotClosedSale ()<br />testStartReturnTransactionClosedSale () <br />testReturnProductValidData()<br />testReturnCreditCardPaymentReturnTransactionEndedAndNotPayed() |
| 8-2 | FR6.12<br />FR6.13<br />FR6.14<br />FR6.15<br />FR4.1<br />FR8.1 | testStartReturnTransactionNotClosedSale ()<br />testStartReturnTransactionClosedSale () <br />testReturnProductValidData()<br />testReturnCashPaymentReturnTransactionEndedAndNotPayed() |
| 9-1 | FR8.3 | testzGetCreditsAndDebitCorrectValue()                        |
| 10-1 | FR-7.4 | testReturnCreditCardPaymentReturnTransactionEndedAndNotPayed() |
| 10-2 | FR7.3 | testReturnCashPaymentReturnTransactionEndedAndNotPayed()     |



# Coverage of Non Functional Requirements

<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name                         |
| -------------------------- | --------------------------------- |
| NFR4                       | isValidCode()                     |
| NFR5                       | luhnCheck()                       |
| NFR6                       | testCustomerInvalidCustomerCard() |

