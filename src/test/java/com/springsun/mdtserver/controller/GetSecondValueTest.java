package com.springsun.mdtserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetSecondValueTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseSecondValueTest1() {
        String s = "2:sometext:secondValue";
        String result = GetSecondValue.parseSecondValue(s);
        assertEquals("secondValue", result);
    }

    @Test
    public void parseSecondValueTest2() {
        String s = "2:sometext";
        String result = GetSecondValue.parseSecondValue(s);
        assertEquals(null, result);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void parseSecondValueTest3() {
        String s = "2sometextsecondValue";
        GetSecondValue.parseSecondValue(s);

    }

    @Test
    public void parseSecondValueTest4() {
        String s = "::::";
        String result = GetSecondValue.parseSecondValue(s);
        assertEquals(null, result);
    }

}