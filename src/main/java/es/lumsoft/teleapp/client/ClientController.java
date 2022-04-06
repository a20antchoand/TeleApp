package es.lumsoft.teleapp.client;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController {

    private ClientSideServerController serverController;
    private GroupController groupController;


    public ClientController() {
        serverController = new ClientSideServerController(
                this::onMessageReceived,
                this::onServerMessageReceived
        );


        System.out.println("Connected to server");
    }




    public void startCommunication() throws InterruptedException, IOException {
        String message;
        Scanner scan = new Scanner(System.in);


        while (!serverController.isClosed()) {
            if ((message = scan.nextLine()).charAt(0) == '#' || Objects.isNull(groupController))
                serverController.sendMessage(message);
            else
                groupController.sendMessage(message);
            Thread.sleep(200);
        }
    }




    public void onMessageReceived(String sender, String message) {
        System.out.println(sender + ": " + message);
    }


    public void onServerMessageReceived(String type, String message) {
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
    }




    public void login(String userName, int groupID) {
        try {
            groupController = new GroupController(userName, groupID, this::onMessageReceived);

        } catch (IOException e) {
            System.out.println("Was not possible to join group.");
        }
    }







    public static void main(String[] args) throws InterruptedException, IOException {
        new ClientController().startCommunication();
    }
}
