package com.springsun.mdtserver.controller;

import static java.lang.Math.*;

public class Calculator {
    private static final int EARTHRADIUS = 6371302;

    public static int calculate(
            float lastLatitude,
            float lastLongitude,
            float currentLatitude,
            float currentLongitude,
            int distanceAlreadyTraveled
    ){
        int result = 0;
        double d;
        d = acos(sin(lastLatitude) * sin(currentLatitude) +
                cos(lastLatitude) * cos(currentLatitude) * cos(lastLongitude - currentLongitude));
        result = (int)round(d * EARTHRADIUS) + distanceAlreadyTraveled;
        return result;
    }
}
