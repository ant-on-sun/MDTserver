package com.springsun.mdtserver.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetHashOfMessage {
    private static Logger log = Logger.getLogger(GetHashOfMessage.class.getName());
    private static String[] output;

    public static int parseHash(String s){
        int hash = 0;
        if (s.contains(":")){
            output = s.split(":");
        } else {
            log.log(Level.WARNING, "Wrong incoming string, can't find the delimiter (:)");
            throw new StringIndexOutOfBoundsException("can't find the delimiter (:)");
        }

        try {
            hash = Integer.parseInt(output[output.length - 1]);
        } catch (NumberFormatException e) {
            log.log(Level.WARNING, "Can't parse hash sum: ", e);
            //e.printStackTrace();
        }
        return hash;
    }
}
