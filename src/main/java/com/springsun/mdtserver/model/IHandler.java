package com.springsun.mdtserver.model;

public interface IHandler {

    Boolean loginExist(String login);
    Boolean userPasswordIsCorrect(String password);
    Boolean createNewUser(String login, String password);
    int calculateResult(String firstValue, String secondValue);
    void updateDB();
}
