package com.springsun.mdtserver.model.db;

import com.springsun.mdtserver.controller.Calculator;
import com.springsun.mdtserver.model.IHandler;

import java.sql.*;

public class DBHandler implements IHandler {
    private static String sqlURL = "jdbc:mysql://localhost:3306";
    private static String nameOfDB = "map_users";
    private static String tableDB = "musers";
    private static String nameOfLogPassDB = "accounts";
    private static String tableLogPass = "log_pass";
    private static String usernameDB = "user1";
    private static String passwordDB = "Aa1a2a3a4a5a6a7";

    private String userLogin, userPassword;
    private float lastLatitude, currentLatitude, lastLongitude, currentLongitude;
    private int distanceAlreadyTraveled, newResult;

    public DBHandler() {
    }

    @Override
    public Boolean loginExist(String login) {
        userLogin = login;
        try (
                Connection connectionToDB = DriverManager.getConnection(
                        sqlURL + "/" + nameOfLogPassDB + "?useSSL=false", usernameDB, passwordDB);
                Statement statement = connectionToDB.createStatement()
        ){
            String strSelect = "select login from " + tableLogPass + " where login='" + userLogin + "'";
            System.out.println("The SQL query is: " + strSelect); // Echo for debugging
            ResultSet resultSet = statement.executeQuery(strSelect);
            if (resultSet.next()){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean userPasswordIsCorrect(String password) {
        userPassword = password;
        Boolean passwordIsCorrect = false;
        try (
                Connection connectionToDB = DriverManager.getConnection(
                        sqlURL + "/" + nameOfLogPassDB + "?useSSL=false", usernameDB, passwordDB);
                Statement statement = connectionToDB.createStatement()
        ){
            String strSelect = "select login, password from " + tableLogPass + " where login='" + userLogin + "'";
            System.out.println("The SQL query is: " + strSelect); // Echo for debugging
            ResultSet resultSet = statement.executeQuery(strSelect);
            if (resultSet.next()){
                String passwordFromDB = resultSet.getString("password");
                if (passwordFromDB.contentEquals(userPassword.subSequence(0, userPassword.length() - 1))){
                    passwordIsCorrect = true;
                }
            } else {
                throw new SQLException("Can't find userlogin " + userLogin + " in DB");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return passwordIsCorrect;
    }

    @Override
    public Boolean createNewUser(String login, String password) {
        if (loginExist(login)) return false;
        try (
                Connection connectionToDB = DriverManager.getConnection(
                        sqlURL + "/" + nameOfLogPassDB + "?useSSL=false", usernameDB, passwordDB);
                Statement statement = connectionToDB.createStatement()
        ){
            String strInsert = "insert into " + tableLogPass + " (id, login, password) values (null, '" +
                    login + "', '" + password + "')";
            System.out.println("The SQL query is: " + strInsert); // Echo for debugging
            statement.executeUpdate(strInsert);
        } catch (SQLException e){
            e.printStackTrace();
        }
        try (
                Connection connectionToDB = DriverManager.getConnection(
                        sqlURL + "/" + nameOfDB + "?useSSL=false", usernameDB, passwordDB);
                Statement statement = connectionToDB.createStatement()
        ){
            String strInsert = "insert into " + tableDB + " (id, username, distance_traveled, latitude, longitude) " +
                    "values (null, '" + login + "', 0, -1000, -1000)";
            System.out.println("The SQL query is: " + strInsert); // Echo for debugging
            statement.executeUpdate(strInsert);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int calculateResult(String currentLat, String currentLong) {
        try {
            currentLatitude = Float.parseFloat(currentLat);
            currentLongitude = Float.parseFloat(currentLong);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try (
                Connection connectionToDB = DriverManager.getConnection(
                        sqlURL + "/" + nameOfDB + "?useSSL=false", usernameDB, passwordDB);
                Statement statement = connectionToDB.createStatement()
        ){
            String strSelect = "select username, distance_traveled, latitude, longitude from " + tableDB +
                    " where login='" + userLogin + "'";
            System.out.println("The SQL query is: " + strSelect); // Echo for debugging
            ResultSet resultSet = statement.executeQuery(strSelect);
            if (resultSet.next()){
                distanceAlreadyTraveled = resultSet.getInt("distance_traveled");
                lastLatitude = resultSet.getFloat("latitude");
                lastLongitude = resultSet.getFloat("longitude");
                if (lastLatitude < -999 || lastLongitude < -999) {
                    newResult = 0;
                } else {
                    newResult = Calculator.calculate(lastLatitude, lastLongitude, currentLatitude, currentLongitude,
                            distanceAlreadyTraveled);
                }

            } else {
                throw new SQLException("can't find userlogin " + userLogin + " in DB " + nameOfDB + " in table " + tableDB);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        updateDB();
        return newResult;
    }

    @Override
    public void updateDB() {
        try (
                Connection connectionToDB = DriverManager.getConnection(
                        sqlURL + "/" + nameOfDB + "?useSSL=false", usernameDB, passwordDB);
                Statement statement = connectionToDB.createStatement()
        ){
            String strUpdate = "update " + tableDB + " set distance_traveled = " + newResult +
                    ", latitude = " + currentLatitude +
                    ", longitude = " + currentLongitude +
                    " where login='" + userLogin + "'";
            System.out.println("The SQL query is: " + strUpdate); // Echo for debugging
            statement.executeUpdate(strUpdate);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
