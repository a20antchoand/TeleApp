package es.lumsoft.teleapp.client;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController {

    private ClientSideServerController serverController;
    private Dictionary<Integer, GroupController> groupControllers = new Hashtable<>();


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
            if ((message = scan.nextLine()).charAt(0) == '#' || groupControllers.isEmpty())
                serverController.sendMessage(message);
            else
                for (var groups = groupControllers.keys(); groups.hasMoreElements(); ) {
                    groupControllers.get(groups.nextElement()).sendMessage(message);
                }

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

        else if (type.equals("*logout")) {
            Pattern logout_pattern = Pattern.compile("(\\d)");
            Matcher logout_matcher = logout_pattern.matcher(message);


            if (logout_matcher.matches()) logout(Integer.parseInt(logout_matcher.group(1)));
            else
                System.out.println("Error while processing server response: Server response doesn't matches login pattern.");
        }
    }




    public void login(String userName, int groupID) {
        try {
            groupControllers.put(groupID, new GroupController(userName, groupID, this::onMessageReceived));

        } catch (IOException e) {
            System.out.println("Was not possible to join group.");
        }
    }


    public void logout(int groupID) {
        System.out.println("Logging out from group " + groupID);
        groupControllers.get(groupID).logout();
        groupControllers.remove(groupID);
    }







    public static void main(String[] args) throws InterruptedException, IOException {
        new ClientController().startCommunication();
    }
}
