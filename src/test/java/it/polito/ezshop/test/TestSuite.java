package it.polito.ezshop.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
 @Suite.SuiteClasses({BalanceOperationTest.class, CustomerTest.class, OrderTest.class, OperatorTest.class, ProductTypeTest.class, UserTest.class})
public class TestSuite {

}
