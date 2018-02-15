package com.springsun.mdtserver.controller;

public class CheckHash {

    public static boolean checkHash(String s, int hash){
        int i = s.hashCode();
        if (i == hash) {
            return true;
        }
        return false;
    }
}
