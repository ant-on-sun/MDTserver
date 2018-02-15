package com.springsun.mdtserver.controller;

public class GetHashOfMessage {
    private static String[] output;

    public static int parseHash(String s){
        int hash = 0;
        if (s.contains(":")){
            output = s.split(":");
        } else {
            throw new StringIndexOutOfBoundsException("Wrong incoming string, can't find the delimiter (:) ");
        }

        try {
            hash = Integer.parseInt(output[output.length - 1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
