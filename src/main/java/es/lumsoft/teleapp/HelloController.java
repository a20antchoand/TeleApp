package es.lumsoft.teleapp;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private Button btnEnviar;

    @FXML
    private Button btnRebre;

    @FXML
    private VBox containerMissatges;

    @FXML
    private TextArea textAreaMissatge;

    @FXML
    protected void enviarMissatge() {

        HBox hbox = new HBox();

        containerMissatges.setPadding(new Insets(10, 50, 50, 50));
        containerMissatges.setSpacing(10);

        hbox.setAlignment(Pos.BASELINE_RIGHT);

        String text = textAreaMissatge.getText();
        textAreaMissatge.setText("");

        Label msg = new Label("Tú: " + text);
        msg.setPadding(new Insets(10, 10, 10, 10));
        msg.setStyle("-fx-background-color:WHITE");

        hbox.getChildren().add(msg);
        containerMissatges.getChildren().add(hbox);

    }

    @FXML
    protected void rebreMissatge() {

        HBox hbox = new HBox();

        containerMissatges.setPadding(new Insets(10, 50, 50, 50));
        containerMissatges.setSpacing(10);

        hbox.setAlignment(Pos.BASELINE_LEFT);

        String text = textAreaMissatge.getText();
        textAreaMissatge.setText("");

        Label msg = new Label("Usuari: " + text);
        msg.setPadding(new Insets(10, 10, 10, 10));
        msg.setStyle("-fx-background-color:GREY");

        hbox.getChildren().add(msg);
        containerMissatges.getChildren().add(hbox);
    }

}