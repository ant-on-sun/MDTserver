package com.springsun.mdtserver.controller;

import com.springsun.mdtserver.model.IHandler;
import com.springsun.mdtserver.model.db.DBHandler;
import io.netty.channel.ChannelHandlerContext;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BusinessHandlerTest {
    private BusinessHandler bHandler;
    private ChannelHandlerContext ctxmock;
    private IHandler handlerMock;
    private Object msg;

    @Before
    public void setUp() throws Exception {
        bHandler = new BusinessHandler();
        ctxmock = mock(ChannelHandlerContext.class);
        handlerMock = mock(DBHandler.class);
        bHandler.setHandler(handlerMock);
        String appPassword = "password";
        msg = "1:" + appPassword;
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        bHandler.channelRead(ctxmock, msg);
    }

    @After
    public void tearDown() throws Exception {
        bHandler = null;
        msg = "";
    }

    @Test
    public void channelReadTest1a() {
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        boolean checked = bHandler.getAppPasswordChecked();
        assertEquals(checked, true);
    }

    @Test
    public void channelReadTest1b() {
        msg = "1:passworD";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        bHandler.channelRead(ctxmock, msg);
        verify(ctxmock, times(1)).close();
    }

    @Test
    public void channelReadTest2a() {
        msg = "2:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(true);
        when(handlerMock.userPasswordIsCorrect(anyString())).thenReturn(true);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(1)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("3:login and password are correct", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest2b() {
        msg = "2:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(true);
        when(handlerMock.userPasswordIsCorrect(anyString())).thenReturn(false);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(1)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("2:incorrect password", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest2c() {
        msg = "2:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(false);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("6:no such login", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest3a() {
        msg = "2:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(true);
        when(handlerMock.userPasswordIsCorrect(anyString())).thenReturn(true);
        bHandler.channelRead(ctxmock, msg);

        msg = "3:firstValue:secondValue";
        h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.calculateResult(anyString(), anyString())).thenReturn(100);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(1)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(1)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("4:100", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest4a() {
        msg = "4:userLogin";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(true);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("5:login exist", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest4b() {
        msg = "4:userLogin";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(false);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(2)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("6:no such login", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest5a() {
        msg = "5:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.createNewUser(anyString(), anyString())).thenReturn(true);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(1)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("7:new user successfully created", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest5b() {
        msg = "5:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.createNewUser(anyString(), anyString())).thenReturn(false);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(1)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        assertEquals("8:couldn't create new user", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest6a() {
        msg = "2:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(true);
        when(handlerMock.userPasswordIsCorrect(anyString())).thenReturn(true);
        bHandler.channelRead(ctxmock, msg);

        msg = "6:0";
        h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.updateDB()).thenReturn(true);
        when(handlerMock.getDistanceTraveledFromDB()).thenReturn(0);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(1)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(1)).setNewResult(0);
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(1)).setCurrentLongitude(-1000);
        verify(handlerMock, times(1)).setCurrentLatitude(-1000);
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(1)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(1)).updateDB();
        assertEquals("4:0", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest6b() {
        msg = "2:userLogin:userPassword";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.loginExist(anyString())).thenReturn(true);
        when(handlerMock.userPasswordIsCorrect(anyString())).thenReturn(true);
        bHandler.channelRead(ctxmock, msg);

        msg = "6:0";
        h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        when(handlerMock.updateDB()).thenReturn(false);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(1)).loginExist(anyString());
        verify(handlerMock, times(1)).setNewResult(0);
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(1)).setCurrentLongitude(-1000);
        verify(handlerMock, times(1)).setCurrentLatitude(-1000);
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(1)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(1)).updateDB();
        assertEquals("9:couldn't reset result to zero", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTest7a() {
        msg = "7:invalid key protocol was received on client";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        bHandler.setReply("100:sometext");
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        verify(ctxmock, times(1)).close();
    }

    @Test
    public void channelReadTest7b() {
        msg = "7:invalid key protocol was received on client";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        bHandler.setReply("5:sometext");
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        verify(ctxmock, times(0)).close();
        verify(ctxmock, times(2)).writeAndFlush(anyObject());
    }

    @Test
    public void channelReadTest8a() {
        msg = "8:Data were changed while transmitting from server. Wrong hashCode.";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        bHandler.setErrorCounter(1);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        verify(ctxmock, times(0)).close();
        verify(ctxmock, times(2)).writeAndFlush(anyObject());
        assertEquals(2, bHandler.getErrorCounter());
    }

    @Test
    public void channelReadTest8b() {
        msg = "8:Data were changed while transmitting from server. Wrong hashCode.";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        bHandler.setErrorCounter(300);
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        verify(ctxmock, times(1)).close();
        verify(ctxmock, times(1)).writeAndFlush(anyObject());
    }

    @Test
    public void channelReadTestDefaultA() {
        msg = "100:invalid key protocol";
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
        //bHandler.setReply("5:sometext");
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        verify(ctxmock, times(0)).close();
        verify(ctxmock, times(2)).writeAndFlush(anyObject());
        assertEquals("11:invalid key protocol", getMessageWithoutHash(bHandler.getReply()));
    }

    @Test
    public void channelReadTestHash() {
        msg = "2:userLogin:userPassword:111";
        bHandler.channelRead(ctxmock, msg);
        verify(handlerMock, times(0)).getDistanceTraveledFromDB();
        verify(handlerMock, times(0)).loginExist(anyString());
        verify(handlerMock, times(0)).setNewResult(anyInt());
        verify(handlerMock, times(0)).createNewUser(anyString(), anyString());
        verify(handlerMock, times(0)).setCurrentLongitude(anyFloat());
        verify(handlerMock, times(0)).setCurrentLatitude(anyFloat());
        verify(handlerMock, times(0)).calculateResult(anyString(), anyString());
        verify(handlerMock, times(0)).userPasswordIsCorrect(anyString());
        verify(handlerMock, times(0)).updateDB();
        verify(ctxmock, times(0)).close();
        verify(ctxmock, times(2)).writeAndFlush(anyObject());
        assertEquals("10:Data were changed while transmitting to server. Server will do nothing. " +
                "Try to send data later. Received message = 2:userLogin:userPassword Expected hash:1199421290",
                getMessageWithoutHash(bHandler.getReply()));
    }

    private String getMessageWithoutHash(String s) {
        if (s.contains(":")) {
            int i = s.lastIndexOf(":");
            return s.substring(0, i);
        } else {
            throw new StringIndexOutOfBoundsException("can't find the delimiter (:)");
        }
    }
}