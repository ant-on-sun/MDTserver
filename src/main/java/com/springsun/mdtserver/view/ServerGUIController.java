package com.springsun.mdtserver.view;

import com.springsun.mdtserver.controller.Server;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerGUIController {
    private static Logger log = Logger.getLogger(ServerGUIController.class.getName());
    private Server server;
    private BooleanProperty started = new SimpleBooleanProperty(false);
    private StringProperty statusMessageModel = new SimpleStringProperty("Server is not started");
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    private String hostAsString = "localhost";
    private int portAsInt = 8007;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label serverStatus;
    @FXML
    private Label serverHostAndPort;
    @FXML
    private Label remember;
    @FXML
    private Label host;
    @FXML
    private Label port;
    @FXML
    private Label warning;
    @FXML
    private TextField tfHost;
    @FXML
    private TextField tfPort;
    @FXML
    private Button startServer;
    @FXML
    private Button shutdownServer;
    @FXML
    private Button shutdownApp;

    @FXML
    private void initialize(){
        serverStatus.textProperty().bind(statusMessageModel);
        startServer.disableProperty().bind(started);
        shutdownServer.disableProperty().bind(started.not());
        shutdownApp.disableProperty().bind(started);
        remember.setText("Do not forget that host and port on client \nmust be equal to these!");
        remember.visibleProperty().bind(
                Bindings.isNotEmpty(tfHost.textProperty())
                .or(Bindings.isNotEmpty(tfPort.textProperty()))
        );
        tfHost.setPromptText("localhost");
        tfPort.setPromptText("8007");
        warning.textProperty().set("");
        warning.visibleProperty().bind(started.not());
    }

    @FXML
    private void startHandler(ActionEvent actionEvent){
        if (started.get() || !checkHost() || !checkPort()) return;
        server = new Server(started, statusMessageModel, hostAsString, portAsInt);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                server.serverStart();
                return null;
            }

            @Override
            protected void failed(){
                Throwable exc = getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server");
                alert.setHeaderText( exc.getClass().getName() );
                alert.setContentText( exc.getMessage() );
                alert.showAndWait();
                started.set(false);
                log.log(Level.WARNING, "Couldn't start server.");
            }
        };
        executorService.submit(task);
        executorService.execute(task);
    }

    @FXML
    private void shutdownHandler(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Notice.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());
            stage.setTitle("Notice");
            stage.setScene(new Scene(root));

            //Passing parameters to another controller
            NoticeController noticeController = fxmlLoader.<NoticeController>getController();
            noticeController.setServer(server);
            noticeController.setStarted(started);
            noticeController.setStatusMessageModel(statusMessageModel);

            stage.show();
        } catch (IOException e) {
            log.log(Level.WARNING, "IOException in GUI shutdownHandler(): ", e);
            //e.printStackTrace();
        }
    }

    @FXML
    private void shutdownAppHandler() throws InterruptedException {
        if (started.get()) return;
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdownNow();
        Platform.exit();
        System.exit(0);
    }

    private boolean checkHost() {
        if (tfHost.getText().equals("")){
            log.log(Level.INFO, "In GUI text field Host equals empty string. " +
                    "\nDefault host '" + hostAsString + "' will be used.");
            return true;
        }
        hostAsString = tfHost.getText();
        if (checkSymbolsInHost(hostAsString)) return true;
        warning.setText("Wrong host format or wrong symbol(s) in host. \nIt must be [a-zA-Z0-9_.-]");
        return false;
    }

    private boolean checkPort(){
        if (tfPort.getText().equals("")){
            log.log(Level.INFO, "In GUI text field Port equals empty string. " +
                    "\nDefault port '" + portAsInt + "' will be used.");
            return true;
        }
        try {
            portAsInt = (int)Integer.parseInt(tfPort.getText());
        } catch (NumberFormatException e){
            warning.setText("Can't parse port. It must be integer.");
            log.log(Level.WARNING, "Can't parse port from GUI: ", e);
            return false;
        }
        if (portAsInt < 1024 || portAsInt > 49151){
            warning.setText("Port must be in range from 1024 to 49151");
            return false;
        }
        return true;
    }

    private boolean checkSymbolsInHost(String str){
        Pattern p = Pattern.compile("\\w+([\\.-]?\\w+)*");
        Matcher m = p.matcher(str);
        if (m.matches()) return true;
        return false;
    }

}
