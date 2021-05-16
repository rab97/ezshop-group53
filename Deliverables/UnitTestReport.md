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
	
 - Valitidy of the string
 - productCode length
 - productCode format  
 - character type of the productCode




**Predicates for method *isValid*:**

| Criteria | Predicate |
| -------- | --------- | 
| Valitidy of the string | valid|
| Valitidy of the string | NULL |
| productCode length |[0, 12)|
| productCode length | [12, 13 or 14]|
| productCode length | (14 , maxString)|
| productCode format | last value is a check digit|
| productCode format | last value is not a check digit|
| character type of the productCode | all number |
| character type of the productCode | alphabetic value |
| character type of the productCode | alphanumeric value |






**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
| poductCode length | 0, 12, 14 |



**Combination of predicates**:


|Valitidy of the string | producCode length | producTCode format |Valid / Invalid | Description of the test case | JUnit test case|
|-------|-------|-------|-------|-------|-------|
| Valid | 12 | true | Valid | T1a(123456789104) -> true | TestIsValid1() |
| Valid | 13 | true | Valid | T2(4563789345138) -> true | TestIsValid2()  |
| Valid | 14 | true | Valid | T3(45637485902647) -> true |TestIsValid3()  |
| Valid | 11 | false | Invalid | T4(12345678910) -> false |TestIsValid4()  |
| Valid | 15 | false | Invalid | T5(456374859026475) -> false |TestIsValid5()  |
| Valid | 0 | false | Invalid | T5("") -> false |TestIsValid6()  |
| InValid | null | null | Invalid | T6(null) -> false |TestIsValid7()  |


**Criteria for method *luhnCheck*:**
	
 - validity of the  String


**Predicates for method *luhnCheck*:**

| Criteria | Predicate |
| -------- | --------- |
! validity of the String | Valid |
| | NULL| 
| Length of the string  | 13,16|
| | 0 or != 13,16


**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |



**Combination of predicates**:


| Validity of the String | Length of the String| Valid / Invalid | Description of the test case | JUnit test case|
|-------|-------|-------|-------|-------|
| Valid | 13 | Valid | T1a(4485370086510891) -> true  |TestLuhnCheck1()  |
| Valid | 16  | Valid | T1b(4716258050958645) -> true |TestLuhnCheck2()   |
| Invalid | 16 | Invalid | T1c(45637485902647) -> false |TestLuhnCheck3()   |
| Invalid | 15 | Invalid | T2a(123456789102345) -> false |TestLuhnCheck4()   |
| Invalid | 16 | Invalid | T2a(1234567812345678) -> false |TestLuhnCheck5()   |
| Invalid | 0  | Invalid | T2b("") -> false | TestLuhnCheck5()  |
| Invalid | NULL  | Invalid | T2b(null) -> false | TestLuhnCheck6() |
| Invalid | NULL  | Invalid | T2b(ashudsallidò) -> false | TestLuhnCheck7() |


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



