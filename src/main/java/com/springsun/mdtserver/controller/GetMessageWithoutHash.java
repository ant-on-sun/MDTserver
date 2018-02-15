package com.springsun.mdtserver.controller;

public class GetMessageWithoutHash {

    public static String getIncomingMessage(String s){
        int i = s.lastIndexOf(":");
        return s.substring(0, i);
    }
}
