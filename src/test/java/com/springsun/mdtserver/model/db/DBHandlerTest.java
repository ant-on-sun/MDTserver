package com.springsun.mdtserver.model.db;

import com.springsun.mdtserver.model.IHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.*;

//!!!These tests require the presence of working databases, with the parameters specified in the tested class!!!
//These databases should also contain the records used in the tests
public class DBHandlerTest {
    IHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new DBHandler();
    }

    @After
    public void tearDown() throws Exception {
        handler = null;
    }

    @Test
    public void loginExistTest1() {
        String userLogin = "someUserLogin";
        assertTrue(!handler.loginExist(userLogin));
    }

    @Test
    public void loginExistTest2() {
        String userLogin = "UserOne";
        assertTrue(handler.loginExist(userLogin));
    }

    @Test
    public void userPasswordIsCorrectTest1() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        String userPassword = "111";
        assertTrue(handler.userPasswordIsCorrect(userPassword));
    }

    @Test
    public void userPasswordIsCorrectTest2() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        String userPassword = "22";
        assertTrue(!handler.userPasswordIsCorrect(userPassword));
    }

    @Test
    public void createNewUserTest1() {
        String login = "UserOne";
        String password = "333";
        assertTrue(!handler.createNewUser(login, password));
    }

    @Test
    public void calculateResultTest1() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        handler.setCurrentLatitude((float) 40.5);
        handler.setCurrentLongitude((float) 50.0);
        handler.setNewResult(0);
        handler.updateDB();

        String currentLat = "43.5";
        String currentLong = "55.7";
        int result = handler.calculateResult(currentLat, currentLong);
        assertEquals(576982, result);
    }

    @Test
    public void calculateResultTest2() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        String currentLat = "wwe";
        String currentLong = "55.7";
        int result = handler.calculateResult(currentLat, currentLong);
        assertEquals(-1, result);
    }

    @Test
    public void calculateResultTest3() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        handler.setCurrentLatitude((float) -1000);
        handler.setCurrentLongitude((float) -1000);
        handler.setNewResult(0);
        handler.updateDB();

        String currentLat = "45.6";
        String currentLong = "34.6";
        int result = handler.calculateResult(currentLat, currentLong);
        assertEquals(0, result);
    }

    @Test
    public void calculateResultTest4() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        handler.setCurrentLatitude((float) 45.7);
        handler.setCurrentLongitude((float) 44.8);
        handler.setNewResult(555);
        handler.updateDB();

        String currentLat = "1000";
        String currentLong = "1000";
        int result = handler.calculateResult(currentLat, currentLong);
        assertEquals(555, result);
    }


    @Test
    public void getDistanceTraveledFromDBTest1() {
        String userLogin = "Cowboy7";
        handler.loginExist(userLogin);
        assertEquals(10000, handler.getDistanceTraveledFromDB());
    }

    @Test
    public void getDistanceTraveledFromDBTest2() {
        String userLogin = "Cowboy7";
        handler.loginExist(userLogin);
        assertNotEquals(33, handler.getDistanceTraveledFromDB());
    }

    @Test
    public void updateDBTest1() {
        String userLogin = "UserOne";
        handler.loginExist(userLogin);
        handler.setCurrentLatitude((float) 45.7);
        handler.setCurrentLongitude((float) 44.8);
        handler.setNewResult(777);
        handler.updateDB();
        assertEquals(777, handler.getDistanceTraveledFromDB());
    }
}