package com.springsun.mdtserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetFirstValueTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseFirstValueTest1(){
        String s = "2:firstValue:secondValue";
        String firstValue = GetFirstValue.parseFirstValue(s);
        assertEquals("firstValue", firstValue);
    }

    @Test
    public void parseFirstValueTest2(){
        String s = "2:firstvalue:secondValue";
        String firstValue = GetFirstValue.parseFirstValue(s);
        assertNotEquals("firstValue", firstValue);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void parseFirstValueTest3(){
        String s = "2firstValuesecondValue";
        String firstValue = GetFirstValue.parseFirstValue(s);
    }
}