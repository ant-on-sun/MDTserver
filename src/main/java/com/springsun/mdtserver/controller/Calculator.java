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
        double a, b, arctan;

        //Coordinates to radians
        double latitude1 = lastLatitude * Math.PI / 180;
        double longitude1 = lastLongitude * Math.PI / 180;
        double latitude2 = currentLatitude * Math.PI / 180;
        double longitude2 = currentLongitude * Math.PI / 180;

        double deltaLong = longitude2 - longitude1;
        a = sqrt(pow(cos(latitude2) * sin(deltaLong), 2)
                + pow(cos(latitude1) * sin(latitude2) - sin(latitude1) * cos(latitude2) * cos(deltaLong), 2));
        b = sin(latitude1) * sin(latitude2) + cos(latitude1) * cos(latitude2) * cos(deltaLong);
        arctan = atan(a/b);
        result = (int)round(arctan * EARTHRADIUS) + distanceAlreadyTraveled;
        return result;
    }
}
