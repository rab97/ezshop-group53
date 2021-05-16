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

#### Note: the unit test for the getter and setter are not report in the document because they do not have any criteria

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
| productCode format | last value is a check  -> true|
| productCode format | last value is not a check digit -> false|
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
| Length of the string  | [0,13)|
| Length of the string  | [13,14)|
| Length of the string  | [14,16)|
| Length of the string  | [16, maxString)|
| | 0 or != 13,16


**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
| Length of the string| 0, 13, 14, 15|



**Combination of predicates**:


| Validity of the String | Length of the String| Valid / Invalid | Description of the test case | JUnit test case|
|-------|-------|-------|-------|-------|
| Valid | 13 | Valid | T1(4485370086510891) -> true  |TestLuhnCheck1()  |
| Valid | 16  | Valid | T2(4716258050958645) -> true |TestLuhnCheck2()   |
| Invalid | 16 | Invalid | T3(45637485902647) -> false |TestLuhnCheck3()   |
| Invalid | 15 | Invalid | T4(123456789102345) -> false |TestLuhnCheck4()   |
| Invalid | 16 | Invalid | T5(1234567812345678) -> false |TestLuhnCheck5()   |
| Invalid | 0  | Invalid | T6("") -> false | TestLuhnCheck5()  |
| Invalid | NULL  | Invalid | T7(null) -> false | TestLuhnCheck6() |
| Invalid | NULL  | Invalid | T8(ashudsallidò) -> false | TestLuhnCheck7() |


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



