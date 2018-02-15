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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGUIController {
    private static Logger log = Logger.getLogger(ServerGUIController.class.getName());
    private Server server;
    private BooleanProperty started = new SimpleBooleanProperty(false);
    private StringProperty statusMessageModel = new SimpleStringProperty("Server is not started");
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label serverStatus;
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
    }

    @FXML
    private void startHandler(ActionEvent actionEvent){
        if (started.get()) return;
        server = new Server();

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
        started.set(true);
        statusMessageModel.setValue("Server is working");
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

}
