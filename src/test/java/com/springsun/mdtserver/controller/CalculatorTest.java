package com.springsun.mdtserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CalculatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void calculateTest() {
        float lastLatitude = (float) 43.2;
        float lastLongitude = (float) 45.6;
        float currentLatitude = (float) 43.3;
        float currentLongitude = (float) 45.8;
        int distanceAlreadyTraveled = 222;
        int result = Calculator.calculate(
                lastLatitude,
                lastLongitude,
                currentLatitude,
                currentLongitude,
                distanceAlreadyTraveled
        );
        assertEquals(19870, result);
    }
}