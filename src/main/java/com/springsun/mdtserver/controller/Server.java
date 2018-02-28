package com.springsun.mdtserver.controller;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static Logger log = Logger.getLogger(Server.class.getName());
    private static final boolean SSL = System.getProperty("ssl") != null;
    private static String host;
    private static int port;
    private EventLoopGroup boosGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture;
    private BooleanProperty started;
    private StringProperty statusMessageModel;

    public Server(BooleanProperty started, StringProperty statusMessageModel, String host, int port) {
        this.started = started;
        this.statusMessageModel = statusMessageModel;
        Server.host = host;
        Server.port = port;
    }

    public void serverStart() throws Exception{
        //Configure SSL
        final SslContext sslCtx;
        if (SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        //Configure the server
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.WARN))
                    .childHandler(new ServerSocketInitializer(sslCtx));
            //Start the server
            channelFuture = bootstrap.bind(Server.host, Server.port).sync();

            Platform.runLater(() -> {
                started.set(true);
                statusMessageModel.setValue("Server is working");
            });

            // Wait until the server socket is closed
            channelFuture.channel().closeFuture().sync();
        } finally {
            //Shut down all event loops to terminate all threads
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void serverShutdown() {
        try {
            //Shutdown EventLoopGroup's
            boosGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync(); //close port
            Platform.runLater(() -> {
                started.set(false);
                statusMessageModel.setValue("Server is not started");
            });
        } catch (InterruptedException e){
            log.log(Level.WARNING, "Exception in serverShutdown(): ", e);
            //e.printStackTrace();
        }
    }
}
