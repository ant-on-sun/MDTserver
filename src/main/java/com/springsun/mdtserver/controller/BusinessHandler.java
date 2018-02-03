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
    private Boolean appPasswordChecked, userLoginPasswordChecked;
    private String in;
    private Integer key;
    private String firstValue;
    private String secondValue;
    private IHandler handler = new DBHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        in = (String)msg;
        key = GetKeyFromMessage.parseKey(in);
        firstValue = GetFirstValue.parseFirstValue(in);
        secondValue = GetSecondValue.parseSecondValue(in);
        switch (key){
            case 1: //Check if connected client is a valid client of our application
                if (appPassword.equalsIgnoreCase(firstValue)){
                    appPasswordChecked = true;
                    ctx.writeAndFlush("1:" + approved);
                } else {
                    ctx.writeAndFlush("I don't know you");
                    ctx.close();
                }
                break;
            case 2: //Check login/password of user
                if (appPasswordChecked){
                    if (!handler.loginExist(firstValue)){
                        ctx.writeAndFlush("6:no such login");
                        break;
                    }
                    userLoginPasswordChecked = handler.userPasswordIsCorrect(secondValue);
                    if (userLoginPasswordChecked){
                        ctx.writeAndFlush("3:login and password are correct");
                    } else {
                        ctx.writeAndFlush("2:incorrect password");
                    }
                }
                break;
            case 3: //Calculate the result and update DB
                if (appPasswordChecked && userLoginPasswordChecked){
                    int distance = handler.calculateResult(firstValue, secondValue);
                    ctx.writeAndFlush("4:" + distance);
                }
                break;
            case 4: //Check new user login in DB
                if (appPasswordChecked && handler.loginExist(firstValue)){
                    ctx.writeAndFlush("5:login exist");
                } else if (appPasswordChecked && !handler.loginExist(firstValue)){
                    ctx.writeAndFlush("6:no such login");
                }
                break;
            case 5: //Create new user in DB
                if (appPasswordChecked){
                    Boolean success = handler.createNewUser(firstValue, secondValue);
                    if (success){
                        ctx.writeAndFlush("7:new user successfully created");
                    } else {
                        ctx.writeAndFlush("8:couldn't create new user");
                    }
                }
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

}
