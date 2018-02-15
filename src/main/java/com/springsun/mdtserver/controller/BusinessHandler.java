package com.springsun.mdtserver.controller;

import com.springsun.mdtserver.model.IHandler;
import com.springsun.mdtserver.model.db.DBHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class BusinessHandler extends ChannelInboundHandlerAdapter{
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        context = ctx;
        inWithHash = (String) msg;
        hash = GetHashOfMessage.parseHash(inWithHash);
        in = GetMessageWithoutHash.getIncomingMessage(inWithHash);
        hashChecked = CheckHash.checkHash(in, hash);
        if (!hashChecked) {
            reply = "10:Data were changed while transmitting to server. Server will do nothing. Try to send data later.";
            write();
            return;
        }
        key = GetKeyFromMessage.parseKey(in);
        firstValue = GetFirstValue.parseFirstValue(in);
        secondValue = GetSecondValue.parseSecondValue(in);
        switch (key){
            case 1: //Check if connected client is a valid client of our application
                if (appPassword.equalsIgnoreCase(firstValue)){
                    appPasswordChecked = true;
                    reply = "1:" + approved;
                    write();
                } else {
                    ctx.writeAndFlush("I don't know you");
                    ctx.close();
                }
                break;
            case 2: //Check login/password of user
                if (appPasswordChecked){
                    if (!handler.loginExist(firstValue)){
                        reply = "6:no such login";
                        write();
                        break;
                    }
                    userLoginPasswordChecked = handler.userPasswordIsCorrect(secondValue);
                    if (userLoginPasswordChecked){
                        reply = "3:login and password are correct";
                        write();
                    } else {
                        reply = "2:incorrect password";
                        write();
                    }
                } else {
                    ctx.close();
                }
                break;
            case 3: //Calculate the result and update DB
                if (appPasswordChecked && userLoginPasswordChecked){
                    int distance = handler.calculateResult(firstValue, secondValue);
                    reply = "4:" + distance;
                    write();
                } else {
                    ctx.close();
                }
                break;
            case 4: //Check new user login in DB
                if (appPasswordChecked && handler.loginExist(firstValue)){
                    reply = "5:login exist";
                    write();
                } else if (appPasswordChecked && !handler.loginExist(firstValue)){
                    reply = "6:no such login";
                    write();
                } else {
                    ctx.close();
                }
                break;
            case 5: //Create new user in DB
                if (appPasswordChecked){
                    Boolean success = handler.createNewUser(firstValue, secondValue);
                    if (success){
                        reply = "7:new user successfully created";
                        write();
                    } else {
                        reply = "8:couldn't create new user";
                        write();
                    }
                } else {
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
                    } else {
                        reply = "9:couldn't reset result to zero";
                        write();
                    }
                } else {
                    ctx.close();
                }
                break;
            default:
                reply = "11:invalid key protocol";
                write();
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
        cause.printStackTrace();
        ctx.close();
    }

    //Add hashCode to message, write to channel and flush
    private void write(){
        int h = reply.hashCode();
        reply = reply + ":" + h;
        context.writeAndFlush(reply);
    }

}
