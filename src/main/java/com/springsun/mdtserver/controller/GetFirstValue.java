package com.springsun.mdtserver.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetFirstValue {
    private static Logger log = Logger.getLogger(GetFirstValue.class.getName());
    private static String[] output;

    public static String parseFirstValue(String s){
        if (s.contains(":")){
            output = s.split(":");
        } else {
            log.log(Level.WARNING, "Wrong incoming string, can't find the delimiter (:)");
            throw new StringIndexOutOfBoundsException("can't find the delimiter (:)");
        }
        return output[1];
    }
}
