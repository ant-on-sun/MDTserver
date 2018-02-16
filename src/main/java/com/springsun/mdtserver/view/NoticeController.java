package com.springsun.mdtserver.view;

import com.springsun.mdtserver.controller.Server;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoticeController {
    private static Logger log = Logger.getLogger(NoticeController.class.getName());
    Server server;
    private BooleanProperty started;
    private StringProperty statusMessageModel;

    @FXML
    private Label msgLable;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;

    @FXML
    private void initialize(){
        msgLable.setText("This will shutdown MDT server. \nAre you sure?");
    }

    @FXML
    public void yesButtonHandler(ActionEvent actionEvent){
        if (!started.get()) return;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                server.serverShutdown();
                return null;
            }

            @Override
            protected void succeeded(){
                started.set(false);
            }

            @Override
            protected void failed(){
                Throwable exc = getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server");
                alert.setHeaderText( exc.getClass().getName() );
                alert.setContentText( exc.getMessage() );
                alert.showAndWait();
                started.set(true);
                log.log(Level.WARNING, "Couldn't shutdown server.");
            }
        };

        new Thread(task).start();
        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void noButtonHandler(ActionEvent actionEvent){
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setStarted(BooleanProperty started) {
        this.started = started;
    }

    public void setStatusMessageModel(StringProperty statusMessageModel) {
        this.statusMessageModel = statusMessageModel;
    }


}
