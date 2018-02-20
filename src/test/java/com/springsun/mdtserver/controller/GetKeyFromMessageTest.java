package com.springsun.mdtserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetKeyFromMessageTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseKeyTest1(){
        String s = "2:sometext";
        int result = GetKeyFromMessage.parseKey(s);
        assertEquals(2, result);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void parseKeyTest2(){
        String s = "2sometext";
        GetKeyFromMessage.parseKey(s);
    }

    @Test
    public void parseKeyTest3(){
        String s = "2sss:sometext";
        int result = GetKeyFromMessage.parseKey(s);
        assertEquals(1000, result);
    }
}