package com.springsun.mdtserver.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckHash {
    private static Logger log = Logger.getLogger(CheckHash.class.getName());

    public static boolean checkHash(String s, int hash){
        int i = s.hashCode();
        if (i == hash) {
            log.log(Level.FINE, "Hash sum of incoming message is correct.");
            return true;
        }
        return false;
    }
}
