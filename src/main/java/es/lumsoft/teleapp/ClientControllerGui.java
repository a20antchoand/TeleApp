package es.lumsoft.teleapp;

import es.lumsoft.teleapp.client.ClientController;
import es.lumsoft.teleapp.client.ClientSideServerController;
import es.lumsoft.teleapp.client.GroupController;
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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientControllerGui implements Initializable {

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

    ClientSideServerController controler;
    private Dictionary<Integer, GroupController> groupControllers = new Hashtable<>();

    public ClientControllerGui() throws IOException, InterruptedException {

        controler = new ClientSideServerController(this::rebreMissatge, this::rebreMissatgeServidor);

        System.out.println("Conected to server");

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


    private void onEnterSendMessage(KeyEvent tecla) {

        if (tecla.getCode().equals(KeyCode.ENTER)) {

            if (!textAreaMissatge.getText().equals("\n"))
                enviarMissatge();
        }
    }


    public void startCommunication() throws InterruptedException, IOException {
        String message;
        Scanner scan = new Scanner(System.in);


        while (!controler.isClosed()) {
            if ((message = scan.nextLine()).charAt(0) == '#' || groupControllers.isEmpty())
                controler.sendMessage(message);
            else
                for (var groups = groupControllers.keys(); groups.hasMoreElements(); ) {
                    groupControllers.get(groups.nextElement()).sendMessage(message);
                }

            Thread.sleep(200);
        }
    }

    public void login(String userName, int groupID) {
        try {
            groupControllers.put(groupID, new GroupController(userName, groupID, this::rebreMissatge));

        } catch (IOException e) {
            System.out.println("Was not possible to join group.");
        }
    }


    public void logout(int groupID) {
        System.out.println("Logging out from group " + groupID);
        groupControllers.get(groupID).logout();
        groupControllers.remove(groupID);
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

        Platform.runLater(() -> {
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
        });
    }

    @FXML
    protected void rebreMissatgeServidor(String type, String message) {

        Platform.runLater(() -> {




            if (type.equals("*info"))
                System.out.println("Info from server: " + message);
            else if (type.equals("*error"))
                System.out.println("Error from server: " + message);
            else if (type.equals("*login")) {
                Pattern login_pattern = Pattern.compile("group: (\\d) username: (.*)");
                Matcher login_matcher = login_pattern.matcher(message);


                if (login_matcher.matches()) login(login_matcher.group(2), Integer.parseInt(login_matcher.group(1)));
                else
                    System.out.println("Error while processing server response: Server response doesn't matches login pattern.");
            }

            else if (type.equals("*logout")) {
                Pattern logout_pattern = Pattern.compile("(\\d)");
                Matcher logout_matcher = logout_pattern.matcher(message);


                if (logout_matcher.matches()) logout(Integer.parseInt(logout_matcher.group(1)));
                else
                    System.out.println("Error while processing server response: Server response doesn't matches login pattern.");
            }


            HBox hbox = new HBox();

            containerMissatges.setPadding(new Insets(10, 50, 50, 50));
            containerMissatges.setSpacing(10);

            hbox.setAlignment(Pos.BASELINE_LEFT);

            Label msg = new Label(type + ": " + message);
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
        });
    }


}