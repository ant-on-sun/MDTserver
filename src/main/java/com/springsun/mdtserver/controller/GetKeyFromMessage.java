package com.springsun.mdtserver.controller;

public class GetKeyFromMessage {
    private static String[] output;

    public static Integer parseKey(String s) {
        Integer key = null;
        try {
            if (s.contains(":")){
                output = s.split(":");
            } else {
                throw new StringIndexOutOfBoundsException("Wrong incoming string, can't find the delimiter (:) ");
            }
            key = (Integer)Integer.parseInt(output[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return key;
    }
}
