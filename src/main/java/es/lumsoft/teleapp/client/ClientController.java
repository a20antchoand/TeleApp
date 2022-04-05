package es.lumsoft.teleapp.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientController implements Runnable {

    private Socket connection;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;
    private OnMessageReceivedListener onMessageReceivedListener;
    private OnServerMessageReceivedListener onServerMessageReceivedListener;


    public ClientController(String hostName, int port) {
        try {
            this.connection = new Socket(hostName, port);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        } catch (IOException e) {
            closeConnection(e);
        }
    }


    public String getUserName() {
        return userName;
    }
    public boolean isClosed() {
        return connection.isClosed();
    }




    @Override
    public void run() {
        String sender, message;


        // Comienza a escuchar
        while (!connection.isClosed()) {
            try {
                sender = reader.readLine();
                message = reader.readLine();

                // Comprueba que no sea null
                if (sender != null && message != null) {

                    // Comprueba si es un mensaje del servidor
                    if (sender.equals("Server")) handleServerMessage(message);

                    // Si no es del servidor ejecuta la acción establecida
                    else if (onMessageReceivedListener != null)
                        onMessageReceivedListener.onMessageReceived(sender, message);
                }

                else closeConnection();

            } catch (IOException e) {
                closeConnection(e);
                break;
            }
        }
    }


    private void log(String message) {
        System.out.println("CLIENT_CONTROLLER: " + message);
    }




    // * Connection control

    public void startListening(
            OnMessageReceivedListener onMessageReceivedListener,
            OnServerMessageReceivedListener onServerMessageReceivedListener
    ) {

        this.onMessageReceivedListener = onMessageReceivedListener;
        this.onServerMessageReceivedListener = onServerMessageReceivedListener;
        new Thread(this).start();
    }
    public void closeConnection() {
        closeConnection(null);
    }
    public void closeConnection(Throwable error) {
        if (error != null) log("Closing connection due to an error: " + error.getMessage());
        else log("Closing connection...");

        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (connection != null) connection.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        log("Connection closed.\n");
    }



    // * Client functions

    public void sendCommand(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getMessageType(String message) {
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == ':') return message.substring(0, i);
        }

        return null;
    }


    private void handleServerMessage(String message) {
        String messageType = getMessageType(message);
        assert messageType != null;
        message = message.substring(messageType.length() + 2);


        switch (messageType) {
            case "*info", "*error" ->
                    onServerMessageReceivedListener.onServerMessageReceived(
                            messageType,
                            message
                    );
            case "*login" -> {
            }
        }
    }


    private void logginInGroup(int groupID) {

    }




    // * Events

    public interface OnMessageReceivedListener {
        void onMessageReceived(String sender, String message);
    }


    public interface OnServerMessageReceivedListener {
        void onServerMessageReceived(String type, String message);
    }






    public static void main(String[] args) throws InterruptedException {
        ClientController clientController = new ClientController("localhost", 2022);


        System.out.println("Connected to server");
        clientController.startListening(
                (sender, message) -> System.out.println(sender + ": " + message),
                 (type, message) -> System.out.println((type.equals("*error") ? "Error: " : "") + message));
        System.out.println("Started listening");

        while (!clientController.isClosed()) {
            clientController.sendCommand(new Scanner(System.in).nextLine());
            Thread.sleep(200);
        }
    }
}
