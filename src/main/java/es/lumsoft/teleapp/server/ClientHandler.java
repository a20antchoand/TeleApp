package es.lumsoft.teleapp.server;

import java.io.*;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private static final List<ClientHandler> CLIENT_CONNECTIONS = new ArrayList<>();


    private Socket connection;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;


    public ClientHandler(Socket connection, String serverName) {
        try {
            this.connection = connection;
            reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.connection.getOutputStream()));

            // Envía el nombre del servidor
            sendMessage(this, null, serverName);

        } catch (IOException e) {
            closeConnection(e);
        }
    }




    @Override
    public void run() {
        String message;


        // Espera al nombre de usuario
        try {
            this.userName = this.reader.readLine();

        } catch (IOException e) {
            closeConnection(e);
        }

        // Informa al resto de usuarios
        broadcastMessage(userName + " has joined.", true);

        // Añade la conexión actual a la lista de conexiones con el servidor
        CLIENT_CONNECTIONS.add(this);


        // Comienza a escuchar
        while (connection.isConnected()) {
            try {
                message = reader.readLine();

                if (message.charAt(0) == '#') parseCommand(message);
                else broadcastMessage(message, false);

            } catch (IOException e) {
                closeConnection(e);
                break;
            }
        }
    }




    public void start() {
        new Thread(this).start();
    }


    public void closeConnection() {
        closeConnection(null);
    }
    public void closeConnection(Throwable error) {

        if (CLIENT_CONNECTIONS.contains(this)) {
            CLIENT_CONNECTIONS.remove(this);
            broadcastMessage(userName + " has left.", true);
        }

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


    private void parseCommand(String command) {
        log("Incoming command -> " + command);
    }


    private void broadcastMessage(String message, boolean serverMessage) {
        if (!serverMessage) log(message);
        if (message != null) {
            for (ClientHandler clientConnection : CLIENT_CONNECTIONS) {
                if (!clientConnection.userName.equals(userName))
                    sendMessage(clientConnection, (serverMessage ? "Server" : userName), message);
            }
        }
    }


    private void sendMessage(ClientHandler destination, String senderName, String message) {
        try {
            if (senderName != null) {
                destination.writer.write(senderName);
                destination.writer.newLine();
                destination.writer.flush();
            }
            destination.writer.write(message);
            destination.writer.newLine();
            destination.writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void log(String message) {
        System.out.println("CLIENT_HANDLER:" + (userName != null ? " (" + userName + "): " : ": ") + message);
    }
}
