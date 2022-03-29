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
            writer.write(serverName);

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }




    @Override
    public void run() {
        String message;


        // Espera al nombre de usuario
        try {
            this.userName = this.reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
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
                e.printStackTrace();
                closeConnection();
                break;
            }
        }
    }




    public void start() {
        new Thread(this).start();
    }


    public void closeConnection() {

        if (CLIENT_CONNECTIONS.contains(this)) {
            CLIENT_CONNECTIONS.remove(this);
            broadcastMessage(userName + " has left.", true);
        }

        System.out.println("Closing connection...");

        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (connection != null) connection.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Connection closed.\n");
    }


    private void parseCommand(String command) {
        System.out.println("Incoming command -> " + command);
    }


    private void broadcastMessage(String message, boolean serverMessage) {
        if (message != null) {
            for (ClientHandler clientConnection : CLIENT_CONNECTIONS) {
                if (!clientConnection.userName.equals(userName))
                    sendMessage(clientConnection, (serverMessage ? "Server" : userName), message);
            }
        }
    }


    private void sendMessage(ClientHandler destination, String senderName, String message) {
        try {
            destination.writer.write(senderName);
            destination.writer.write(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
