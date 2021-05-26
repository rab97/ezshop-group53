package it.polito.ezshop.test;

import org.junit.Test;

import it.polito.ezshop.persistence.DAOEZShop;
import it.polito.ezshop.persistence.DAOException;
import scala.collection.immutable.List;
import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;

import it.polito.ezshop.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Objects;

public class DAOTest {
    
    DAOEZShop dao= new DAOEZShop();

    @Test
    public void testInsertAndRemoveUser(){

        User testUser= new ConcreteUser("test", null, "passwordTest", Constants.CASHIER);
        try{
            Integer newUserId= dao.insertUser(testUser.getUsername(),testUser.getPassword(), testUser.getRole());
            if(newUserId<=0){
                fail();
            }
            assertTrue(dao.removeUser(newUserId));
            assertFalse(dao.removeUser(newUserId)); //Second removal

        }catch(DAOException e){
            fail();
        }   

    }

    @Test
    public void testGetAllUsers(){

        User u1= new ConcreteUser("test1", null, "p1", Constants.CASHIER);
        User u2= new ConcreteUser("test2", null, "p2", Constants.CASHIER);
        User u3= new ConcreteUser("test3", null, "p3", Constants.CASHIER);

        try{
            Integer id1= dao.insertUser(u1.getUsername(),u1.getPassword(), u1.getRole());
            Integer id2= dao.insertUser(u2.getUsername(),u2.getPassword(), u2.getRole());
            Integer id3= dao.insertUser(u3.getUsername(),u3.getPassword(), u3.getRole());

            System.out.println("id1, id2, id3 = " + id1 + " " + id2 + " " + id3 + " ");
            if(id1<=0||id2<=0||id3<=0){
                fail();
            }
            
            //Completare col caso "buono"
            //List<User> testList= new ArrayList<User>;
            dao.removeUser(id1);
            dao.removeUser(id2);
            dao.removeUser(id3);

        }catch(DAOException e){
            fail();
        }   
    }


    /*
    NON FUNZIONA A CAUSA DI ASSERT EQUALS

    @Override
    public boolean equals(Object o){
        // self check
        if(this == o){ return true; } else
        // null check
        if(o == null){ return false;} else
        // type check and cast
        if(getClass() != o.getClass()){ return false; } else {
            final User a = (User) o;
            // field comparison
            return Objects.equals(a, a);
        }
    }

    @Test
    public void testSearchUserById(){

        User testUser= new ConcreteUser("test1", null, "passwordTest", Constants.CASHIER);
        try{
            Integer newUserId= dao.insertUser(testUser.getUsername(),testUser.getPassword(), testUser.getRole());
            if(newUserId<=0){
                fail();
            }
            testUser.setId(newUserId);

            assertEquals(testUser, dao.searchUserById(newUserId));
            
            dao.removeUser(newUserId);
        }catch(DAOException e){
            fail();
        }   
    }

    */

    @Test
    public void testUpdateRights(){

        User testUser= new ConcreteUser("test", null, "passwordTest", Constants.CASHIER);
        try{
            assertFalse(dao.updateRights(0, Constants.ADMINISTRATOR)); //User doesn't exist

            Integer newUserId= dao.insertUser(testUser.getUsername(),testUser.getPassword(), testUser.getRole());
            if(newUserId<=0){
                fail();
            }
            assertTrue(dao.updateRights(newUserId, Constants.SHOP_MANAGER));
            
            dao.removeUser(newUserId);
        }catch(DAOException e){
            fail();
        }   

    }


}
