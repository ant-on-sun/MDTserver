package com.springsun.mdtserver.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetSecondValue {
    private static Logger log = Logger.getLogger(GetSecondValue.class.getName());
    private static String[] output;

    public static String parseSecondValue(String s){
        if (s.contains(":")){
            output = s.split(":");
        } else {
            log.log(Level.WARNING, "Wrong incoming string, can't find the delimiter (:)");
            throw new StringIndexOutOfBoundsException("can't find the delimiter (:)");
        }
        if (output.length < 3) return null;
        return output[2];
    }
}
