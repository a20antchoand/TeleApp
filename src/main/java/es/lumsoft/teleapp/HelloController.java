package es.lumsoft.teleapp;

import es.lumsoft.teleapp.client.ClientController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private VBox containerMissatges;

    @FXML
    private TextArea textAreaMissatge;

    public HelloController() throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreaMissatge.setOnKeyPressed(this::onEnterSendMessage);
    }

    boolean isConnected = false;
    ClientController controler = new ClientController(new Socket("localhost", 2022), (sender, message) -> Platform.runLater(() -> rebreMissatge(sender, message)));


    private void onEnterSendMessage(KeyEvent tecla) {
        if (tecla.getCode().equals(KeyCode.ENTER)) {
            enviarMissatge();
        }
    }

    @FXML
    protected void enviarMissatge() {

        HBox hbox = new HBox();

        containerMissatges.setPadding(new Insets(10, 50, 50, 50));
        containerMissatges.setSpacing(10);

        hbox.setAlignment(Pos.BASELINE_RIGHT);

        String text = textAreaMissatge.getText();
        textAreaMissatge.setText("");

        if (!isConnected) {
            controler.start(text);
            isConnected = true;
        }
        else
            controler.sendMessage(text);

        Label msg = new Label("Tú: " + text);
        msg.setPadding(new Insets(10, 10, 10, 10));
        msg.setStyle("-fx-background-color:WHITE");

        hbox.getChildren().add(msg);
        containerMissatges.getChildren().add(hbox);

    }

    @FXML
    protected void rebreMissatge(String sender, String message) {

        HBox hbox = new HBox();

        containerMissatges.setPadding(new Insets(10, 50, 50, 50));
        containerMissatges.setSpacing(10);

        hbox.setAlignment(Pos.BASELINE_LEFT);

        Label msg = new Label(sender + ": " + message);
        msg.setPadding(new Insets(10, 10, 10, 10));
        msg.setStyle("-fx-background-color:GREY");

        hbox.getChildren().add(msg);
        containerMissatges.getChildren().add(hbox);
    }


}