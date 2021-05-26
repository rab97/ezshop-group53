# Integration and API Test Documentation

Authors: Marta Caggiano, Francesco Rabezzano, Marco Sapio, Emma Marrocu

Date: 26/05/2021

Version: 1.0.0

# Contents

- [Dependency graph](#dependency graph)

- [Integration approach](#integration)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph 

     <report the here the dependency graph of the classes in EzShop, using plantuml>

# Integration approach

    <Write here the integration sequence you adopted, in general terms (top down, bottom up, mixed) and as sequence
    (ex: step1: class A, step 2: class A+B, step 3: class A+B+C, etc)> 
    <Some steps may  correspond to unit testing (ex step1 in ex above), presented in other document UnitTestReport.md>
    <One step will  correspond to API testing>



#  Tests

   <define below a table for each integration step. For each integration step report the group of classes under test, and the names of
     JUnit test cases applied to them> JUnit test classes should be here src/test/java/it/polito/ezshop

## Step 1
| Classes  | JUnit test cases |
|--|--|
|||


## Step 2
| Classes  | JUnit test cases |
|--|--|
|||


## Step n 

   

| Classes  | JUnit test cases |
|--|--|
|||




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
|  1-1     | FR3.1-FR4.2                  | testCreateProductTypeValidProduct()<br />**testCreateProductTypeUnauthorizedUser**()<br />testUpdatePositionValid() |
| 1-2 | FR4.2-FR3.4 | testGetProductTypeByBarCodeValidProduct()<br />testUpdatePositionValid() |
| 1-3 | FR3.1-FR3.4 | testGetProductTypeByBarCodeValidProduct<br />testProductUpdateValid() |
|  3-1  |                | testIssueOrderValidData() |
| 3-2      | FR4.4 | testPayOrderValidData() |
| 3-3   | FR4.6 | testRecordOrderArrivalValidData() |
| 4-1     | FR5.1 | testDefineCustomerValidData() |
| 4-2      | FR5.6-FR5.5 | testAttachCardToCustomerValidData()<br />testCreateCardValidData() |
| 4-3 | FR5.1 | testCustomerDetachCard() |
| 4-4 | FR5.1 | testModifyCustomerValidData() |
| 5-1 |  | **test login** |
| 5-2 |  | **test logout** |
| 6-1 | FR6.1-FR6.2-FR4.1-FR7.1-FR7.2-FR8.2-FR6.10 | **test sale transaction**<br />**test add product**<br />test end sale transaction<br />test receive payment (cash and credit card) |
| 6-2 | FR6.1-FR6.2-FR4.1-FR6.5-FR6.10-FR7.1-FR7.2-FR8.2 | **test sale transaction**<br />**test add product**<br />test end sale transaction<br />test receive payment (cash and credit card)<br />test discount rate product |
| 6-3 | FR6.1-FR6.2-FR4.1-FR6.4-FR6.10-FR7.1-FR7.2-FR8.2 | **test sale transaction**<br />**test add product**<br />test end sale transaction<br />test receive payment (cash and credit card)<br />test discount rate |
| 6-4 | FR6.1-FR6.2-FR4.1-FR6.10-FR7.1-FR7.2-FR8.2-FR5.7 | **test sale transaction**<br />**test add product**<br />test end sale transaction<br />test receive payment (cash and credit card)<br />testModifyPointsOnCardValidData()<br />testcomputepointsforsale |
| 6-5 | FR6.1-FR6.2-FR4.1-FR6.10-FR6.11 | **test sale transaction**<br />**test add product**<br />test end sale transaction<br />test receive payment (cash and credit card) Invalid<br /> |
| 6-6 | FR6.1-FR6.2-FR4.1-FR6.10-FR7.1-FR8.2 | **test sale transaction**<br />**test add product**<br />test end sale transaction<br />test receive payment (cash) |
|  |  |  |



# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|                            |           |

