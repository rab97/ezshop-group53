# Unit Testing Documentation

Authors:

Date:

Version:

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)




- [White Box Unit Tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >

 ### **Class *Operator* - method *isValid***



**Criteria for method *isValid*:**
	
 - valid String 
 - productCode length
 - productCode format  





**Predicates for method *isValid*:**

| Criteria | Predicate |
| -------- | --------- |
! valid String | 
|  productCode length | = 12, 13 or 14 (Valid)|
|  productCode length | != (12 and 13 and 14) |
|productCode format | last value is a check digit|
|productCode format | last value is not a check digit|






**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |



**Combination of predicates**:


| producCode length | producTCode format | Valid / Invalid | Description of the test case | JUnit test case|
|-------|-------|-------|-------|-------|
| 12 | digit | Valid | T1a(123456789104) -> true | --  |
| 13 | digit | Valid | T1b(4563789345138) -> true | |
| 14 | digit | Valid | T1c(45637485902647) -> true | |
| 11 | not digit | Invalid | T2a(12345678910) -> false | |
| 15 | not digit | Invalid | T2b(456374859026475) -> false | |


### **Class *BalanceOperator* - method *setBalancId***



**Criteria for method *setBalanceId*:**

 - sign of id


**Predicates for method *setBalancId*:**

| Criteria | Predicate |
| -------- | --------- |
| sign of id | (0, maxint)|
| | (minint, 0]|
| | NULL |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
| sign of id | -inf, 0, +inf, NULL |



**Combination of predicates**:


| sing of id |  Valid / Invalid | Description of the test case | JUnit test case|
|-------|-------|-------|-------|
| (minint,0] | Valid | T1a(-1)  -> id = -1 |
| (0,maxInt) | Valid | T1b(2)  -> id = 2 |
| (minint,0] | Valid | T1c(0)  -> id = 0 |
| NULL | Invalid | T1d(null)  -> id = NULL |


**Criteria for method *getBalanceId*:**

 - value of the id


**Predicates for method *getBalanceId*:**

| Criteria | Predicate |
| -------- | --------- |
| value of id | (0, maxint)|
| | (minint, 0]|
| | NULL|

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
| value of id | -inf, 0, +inf, NULL|



**Combination of predicates**:


| sing of id |  Valid / Invalid | Description of the test case | JUnit test case|
|-------|-------|-------|-------|
| (minint,0] | Valid | T1a(-1)  -> id = -1 |
| (0,maxInt) | Valid | T1b(2)  -> id = 2 |
| (minint,0] | Valid | T1c(0)  -> id = 0 |
| NULL | Valid | T1d(NULL)  -> id = NULL |




# White Box Unit Tests

### Test cases definition
    
    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|--|--|
|||
|||
||||

### Code coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >


### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|||||
|||||
||||||



