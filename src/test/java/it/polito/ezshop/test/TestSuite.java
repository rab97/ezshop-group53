package it.polito.ezshop.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
 @Suite.SuiteClasses({BalanceOperationTest.class, CustomerTest.class, OrderTest.class, ProductTypeTest.class, UserTest.class, OperatorTest.class})
public class TestSuite {

}
