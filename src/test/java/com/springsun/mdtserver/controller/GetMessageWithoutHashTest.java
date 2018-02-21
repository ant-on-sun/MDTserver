package com.springsun.mdtserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetMessageWithoutHashTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getIncomingMessageTest1() {
        String s = "1:some message:333";
        String result = GetMessageWithoutHash.getIncomingMessage(s);
        assertEquals("1:some message", result);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void getIncomingMessageTest2() {
        String s = "1some message333";
        GetMessageWithoutHash.getIncomingMessage(s);
    }

}