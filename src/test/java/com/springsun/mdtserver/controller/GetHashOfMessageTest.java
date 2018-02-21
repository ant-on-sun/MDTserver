package com.springsun.mdtserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetHashOfMessageTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseHashTest1(){
        String s = "1:some text:1222";
        assertEquals(1222, GetHashOfMessage.parseHash(s));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void parseHashTest2(){
        String s = "1some text1222";
        GetHashOfMessage.parseHash(s);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void parseHashTest3(){
        String s = "::";
        GetHashOfMessage.parseHash(s);
    }
}