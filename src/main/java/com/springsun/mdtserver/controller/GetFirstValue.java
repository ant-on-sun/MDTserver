package com.springsun.mdtserver.controller;

public class GetFirstValue {
    private static String[] output;

    public static String parseFirstValue(String s){
        if (s.contains(":")){
            output = s.split(":");
        } else {
            throw new StringIndexOutOfBoundsException("Wrong incoming string, can't find the delimiter (:) ");
        }
        return output[1];
    }
}
