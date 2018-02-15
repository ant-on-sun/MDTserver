package com.springsun.mdtserver.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetKeyFromMessage {
    private static Logger log = Logger.getLogger(GetKeyFromMessage.class.getName());
    private static String[] output;

    public static Integer parseKey(String s) {
        Integer key = 1000;
        try {
            if (s.contains(":")){
                output = s.split(":");
            } else {
                log.log(Level.WARNING, "Wrong incoming string, can't find the delimiter (:)");
                throw new StringIndexOutOfBoundsException("can't find the delimiter (:)");
            }
            key = (Integer)Integer.parseInt(output[0]);
        } catch (NumberFormatException e) {
            log.log(Level.WARNING, "Can't parse key from message: ", e);
            //e.printStackTrace();
        }
        return key;
    }
}
