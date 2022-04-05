package es.lumsoft.teleapp;

import es.lumsoft.teleapp.client.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
    private Button btnEnviar;

    @FXML
    private VBox containerMissatges;

/*    @FXML
    private Label nomServer;*/

    @FXML
    private TextField textAreaMissatge;

    @FXML
    private ScrollPane scrollPane;

    public HelloController() throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreaMissatge.setOnKeyPressed(this::onEnterSendMessage);

        btnEnviar.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 100px; " +
                        "-fx-min-height: 100px; " +
                        "-fx-max-width: 100px; " +
                        "-fx-max-height: 100px;"
        );

        btnEnviar.setAlignment(Pos.CENTER);

    }

    boolean isConnected = false;
    ClientController controler = new ClientController(new Socket("localhost", 2022), (sender, message) -> Platform.runLater(() -> rebreMissatge(sender, message)));


    private void onEnterSendMessage(KeyEvent tecla) {

        if (tecla.getCode().equals(KeyCode.ENTER)) {

            if (!textAreaMissatge.getText().equals("\n"))
                enviarMissatge();
        }
    }

    @FXML
    protected void enviarMissatge() {

        String text = textAreaMissatge.getText();

        if (!text.equals("")) {

            textAreaMissatge.setText("");
            HBox hbox = new HBox();

            containerMissatges.setPadding(new Insets(10, 50, 50, 50));
            containerMissatges.setSpacing(10);

            hbox.setAlignment(Pos.BASELINE_RIGHT);

            if (!isConnected) {
                controler.start(text);
                isConnected = true;
            } else
                controler.sendMessage(text);

            Label msg = new Label("Tú: " + text);
            msg.setPadding(new Insets(10, 10, 10, 10));
            msg.setStyle("-fx-background-color:WHITE");
            msg.setWrapText(true);

            hbox.getChildren().add(msg);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    containerMissatges.getChildren().add(hbox);

                }
            });

            scrollPane.setVvalue(1.0);
        }
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
        msg.setWrapText(true);

        hbox.getChildren().add(msg);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                containerMissatges.getChildren().add(hbox);

            }
        });

        scrollPane.setVvalue(1.0);

    }


}