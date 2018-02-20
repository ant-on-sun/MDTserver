package com.springsun.mdtserver.controller;

import com.springsun.mdtserver.model.IHandler;
import com.springsun.mdtserver.model.db.DBHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

@Sharable
public class BusinessHandler extends ChannelInboundHandlerAdapter{

    private static Logger log = Logger.getLogger(BusinessHandler.class.getName());
    private final String appPassword = "password";
    private final String approved = "approved";
    private Boolean appPasswordChecked, userLoginPasswordChecked, hashChecked;
    private String in;
    private Integer key;
    private String firstValue;
    private String secondValue;
    private IHandler handler = new DBHandler();
    private String inWithHash;
    private int hash;
    private String reply;
    private ChannelHandlerContext context;
    private final int maxValueOfKeyOnClient = 11;
    private int errorCounter = 0;
    private final int maxQuantityOfErrors = 100;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        context = ctx;
        inWithHash = (String) msg;
        hash = GetHashOfMessage.parseHash(inWithHash);
        in = GetMessageWithoutHash.getIncomingMessage(inWithHash);
        hashChecked = CheckHash.checkHash(in, hash);
        if (!hashChecked) {
            reply = "10:Data were changed while transmitting to server. Server will do nothing. " +
                    "Try to send data later. Received message = " + in + " Expected hash:" + in.hashCode();
            write();
            log.log(Level.WARNING, "Hash sum of incoming message is not valid. Server will do nothing.");
            return;
        }
        key = GetKeyFromMessage.parseKey(in);
        firstValue = GetFirstValue.parseFirstValue(in);
        secondValue = GetSecondValue.parseSecondValue(in);
        switch (key){
            case 1: //Check if connected client is a valid client of our application
                if (appPassword.equals(firstValue)){
                    appPasswordChecked = true;
                    reply = "1:" + approved;
                    write();
                    log.log(Level.FINE, "Application password approved.");
                } else {
                    ctx.writeAndFlush("I don't know you");
                    log.log(Level.INFO, "Application password is not correct. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 2: //Check login/password of user
                if (appPasswordChecked){
                    if (!handler.loginExist(firstValue)){
                        reply = "6:no such login";
                        write();
                        log.log(Level.INFO, "Provided user login does not exist.");
                        break;
                    }
                    userLoginPasswordChecked = handler.userPasswordIsCorrect(secondValue);
                    if (userLoginPasswordChecked){
                        reply = "3:login and password are correct";
                        write();
                        log.log(Level.FINE, "Provided user login and password are correct.");
                    } else {
                        reply = "2:incorrect password";
                        write();
                        log.log(Level.INFO, "Provided user password is not correct.");
                    }
                } else {
                    log.log(Level.INFO, "Application password was not provided. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 3: //Calculate the result and update DB
                if (appPasswordChecked && userLoginPasswordChecked){
                    int distance = handler.calculateResult(firstValue, secondValue);
                    reply = "4:" + distance;
                    write();
                    log.log(Level.FINE, "Sending calculated result.");
                } else {
                    log.log(Level.INFO, "Application password was not provided and/or " +
                            "user login and password are not correct. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 4: //Check new user login in DB
                if (appPasswordChecked && handler.loginExist(firstValue)){
                    reply = "5:login exist";
                    write();
                    log.log(Level.FINE, "Provided user login exist.");
                } else if (appPasswordChecked && !handler.loginExist(firstValue)){
                    reply = "6:no such login";
                    write();
                    log.log(Level.FINE, "Provided user login does not exist.");
                } else {
                    log.log(Level.INFO, "Application password was not provided. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 5: //Create new user in DB
                if (appPasswordChecked){
                    Boolean success = handler.createNewUser(firstValue, secondValue);
                    if (success){
                        reply = "7:new user successfully created";
                        write();
                        log.log(Level.FINE, "New user created.");
                    } else {
                        reply = "8:couldn't create new user";
                        write();
                        log.log(Level.WARNING, "Couldn't create new user.");
                    }
                } else {
                    log.log(Level.INFO, "Application password was not provided. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 6: //Reset result to zero
                if (appPasswordChecked && userLoginPasswordChecked){
                    handler.setCurrentLatitude(-1000);
                    handler.setCurrentLongitude(-1000);
                    handler.setNewResult(0);
                    Boolean success = handler.updateDB();
                    if (success){
                        int result = handler.getDistanceTraveledFromDB();
                        reply = "4:" + result;
                        write();
                        log.log(Level.FINE, "Result was reset to zero.");
                    } else {
                        reply = "9:couldn't reset result to zero";
                        write();
                        log.log(Level.WARNING, "Couldn't reset result to zero.");
                    }
                } else {
                    log.log(Level.INFO, "Application password was not provided and/or " +
                            "user login and password are not correct. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 7: //Invalid key protocol was received on client
                if (appPasswordChecked){
                    log.log(Level.WARNING, "Invalid key protocol was received on client.");
                    key = GetKeyFromMessage.parseKey(reply);
                    if (key < 1 || key > maxValueOfKeyOnClient) {
                        log.log(Level.WARNING, "Invalid key = " + key
                                + " was created on server. Closing connection.");
                        ctx.close();
                    } else {
                        log.log(Level.WARNING, "The key on server is fine, trying to re-send data.");
                        write();
                    }
                } else {
                    log.log(Level.INFO, "Application password was not provided. Channel will be closed.");
                    ctx.close();
                }
                break;
            case 8: //Data were changed while transmitting from server. Wrong hashCode.
                if (appPasswordChecked){
                    if (errorCounter < maxQuantityOfErrors){
                        log.log(Level.INFO, "Wrong hashCode in message from server. Trying to re-send data.");
                        write();
                    } else {
                        log.log(Level.INFO, "Too many errors in session. Channel will be closed.");
                        ctx.close();
                    }
                    errorCounter++;
                } else {
                    log.log(Level.INFO, "Application password was not provided. Channel will be closed.");
                    ctx.close();
                }
                break;
            default: //Invalid key protocol
                if (appPasswordChecked){
                    if (errorCounter < maxQuantityOfErrors){
                        errorCounter++;
                        reply = "11:invalid key protocol";
                        write();
                    } else {
                        log.log(Level.INFO, "Too many errors in session. Channel will be closed.");
                        ctx.close();
                    }
                    log.log(Level.INFO, "Invalid key protocol.");
                } else {
                    log.log(Level.INFO, "Application password was not provided. Channel will be closed.");
                    ctx.close();
                }
                break;

        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //Close the connection when an exception is raised
        log.log(Level.SEVERE, "Exception caught in method exceptionCaught() in BuisinessHandler: ",
                cause.getMessage());
        //cause.printStackTrace();
        ctx.close();
    }

    //Add hashCode to message, write to channel and flush
    public void write(){
        int h = reply.hashCode();
        reply = reply + ":" + h;
        context.writeAndFlush(reply);
    }

    //For tests
    public void setHandler(IHandler handler) {
        this.handler = handler;
    }

    //For tests
    public Boolean getAppPasswordChecked() {
        return appPasswordChecked;
    }

    //For tests
    public String getReply() {
        return reply;
    }

    //For tests
    public void setReply(String s){
        reply = s;
    }

    //For tests
    public void setErrorCounter(int errorCounter) {
        this.errorCounter = errorCounter;
    }


    //For tests
    public int getErrorCounter() {
        return errorCounter;
    }

}
