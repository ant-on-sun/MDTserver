package com.springsun.mdtserver.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetMessageWithoutHash {
    private static Logger log = Logger.getLogger(GetMessageWithoutHash.class.getName());

    public static String getIncomingMessage(String s){
        if (s.contains(":")){
            int i = s.lastIndexOf(":");
            return s.substring(0, i);
        } else {
            log.log(Level.WARNING, "Wrong incoming string, can't find the delimiter (:)");
            throw new StringIndexOutOfBoundsException("can't find the delimiter (:)");
        }

    }
}
