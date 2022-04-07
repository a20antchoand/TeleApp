package es.lumsoft.teleapp.client;

import es.lumsoft.teleapp.client.events.MessageReceivedHandler;
import es.lumsoft.teleapp.client.events.ServerMessageReceivedHandler;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientSideServerController implements Runnable {

    private final static String SERVER_HOST_NAME = "localhost";
    private final static int SERVER_PORT = 2022;


    private Socket connection;
    private BufferedReader reader;
    private BufferedWriter writer;
    private MessageReceivedHandler onMessageReceivedHandler;
    private ServerMessageReceivedHandler onServerMessageReceivedHandler;


    public ClientSideServerController(
            MessageReceivedHandler onMessageReceivedHandler,
            ServerMessageReceivedHandler onServerMessageReceivedHandler) {

        try {
            this.connection = new Socket(SERVER_HOST_NAME, SERVER_PORT);
            this.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            this.onMessageReceivedHandler = onMessageReceivedHandler;
            this.onServerMessageReceivedHandler = onServerMessageReceivedHandler;


            // Comienza a escuchar del servidor
            new Thread(this).start();

        } catch (IOException e) {
            closeConnection(e);
        }
    }


    public boolean isClosed() {
        return connection.isClosed();
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
                    else if (onMessageReceivedHandler != null)
                        onMessageReceivedHandler.onMessageReceived(sender, message);
                }

                else closeConnection(null);

            } catch (IOException e) {
                closeConnection(e);
                break;
            }
        }
    }


    private void handleServerMessage(String message) {
        // Pattern = 'messageType': 'message'
        Pattern serverMessagePattern = Pattern.compile("(^[^:]+): (.*)");
        Matcher serverMessageMatcher = serverMessagePattern.matcher(message);


        if (serverMessageMatcher.matches()) {
            onServerMessageReceivedHandler.onServerMessageReceived(
                    serverMessageMatcher.group(1),
                    serverMessageMatcher.group(2)
            );
        }

        else
            onServerMessageReceivedHandler.onServerMessageReceived(
                    "*error",
                    "Server response doesn't matches pattern."
            );
    }



    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void log(String message) {
        System.out.println("CLIENT_CONTROLLER: " + message);
    }
}
