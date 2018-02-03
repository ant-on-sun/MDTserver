package com.springsun.mdtserver.controller;

public class GetSecondValue {
    private static String[] output;

    public static String parseSecondValue(String s){
        if (s.contains(":")){
            output = s.split(":");
        } else {
            throw new StringIndexOutOfBoundsException("Wrong incoming string, can't find the delimiter (:) ");
        }
        if (output.length < 3) return null;
        return output[2];
    }
}
