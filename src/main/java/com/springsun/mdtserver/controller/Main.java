package com.springsun.mdtserver.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Main extends Application{

    static {
        InputStream stream = Main.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.err.println("Could not set up logger configuration: " + e.toString());
        }
    }

    private static Stage primaryStage;
    private static Logger log = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ServerGUI.fxml"));
        Parent root = fxmlLoader.load();
        this.primaryStage.setTitle("MDT server");
        this.primaryStage.setMinHeight(270);
        this.primaryStage.setMinWidth(400);
        Scene scene = new Scene(root, 400, 270);
        this.primaryStage.setScene(scene);
        this.primaryStage.setOnCloseRequest(event -> event.consume());
        this.primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception caught in Main: ", e);
        }
    }

}
