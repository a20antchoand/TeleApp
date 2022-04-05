package es.lumsoft.teleapp.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable {

    private static final List<ClientHandler> CLIENT_CONNECTIONS = new ArrayList<>();
    private static final List<Integer> GROUPS_ID = new ArrayList<>();


    private Socket connection;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;
    private boolean loggedInGroup;


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

        // Comienza a escuchar
        while (!connection.isClosed()) {
            try {
                message = reader.readLine();

                if (message != null) {
                    if (message.length() > 0 && message.charAt(0) == '#') parseCommand(message);
                    else sendMessage(this, "Server", "Bad request.");
                }

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
        String[] commandSplit;
        List<String> params;


        if (command.length() > 1) {
            // Elimina el # y separa el comando de sus parámetros
            commandSplit = command.substring(1).split("\s");
            command = commandSplit[0];
            params = Arrays.asList(commandSplit).subList(1, commandSplit.length);


            switch (command) {
                case "login" -> {
                    if (params.size() > 1) login(params.get(0), params.get(1));

                    else
                        sendMessage(
                                this,
                                "Server",
                                "Too few arguments, should be: #login 'userName' 'group ID or 'new' (Groups listed with #groups)'"
                        );
                }
                case "logout" -> closeConnection();
                case "groups" -> sendMessage(this, "Server: Groups: ", GROUPS_ID.toString());
                case "private" -> {
                    ClientHandler clientHandler;
                    StringBuilder finalMessage = new StringBuilder();


                    if (params.size() >= 2) {
                        // Busca el destinatario
                        clientHandler = findDirection(params.get(0));

                        // Crea el mensaje a partir de los parámetros
                        params.subList(1, params.size()).forEach(param -> finalMessage.append(param + " "));

                        // Si se ha encontrado el destinatario envía el mensaje
                        if (clientHandler != null)
                            sendMessage(clientHandler, this.userName + " - private", finalMessage.toString());
                        else
                            sendMessage(this, "Server", "Contact not found.");
                    }

                    else
                        sendMessage(
                                this,
                                "Server",
                                "Too few arguments, should be: #private 'userName' 'message'"
                        );
                }

                default -> sendMessage(this, "Server", "Invalid command.");
            }
        }

        else
            sendMessage(this, "Server", "Invalid command.");
    }


    private void login(String userName, String group) {
        int groupID = -1;
        this.userName = userName;


        // Si es un grupo nuevo busca un espacio nuevo
        if (group.equals("new")) {
            for (int i = 1; i <= 250; i++) {
                if (!GROUPS_ID.contains(i)) {
                    groupID = i;
                    break;
                }
            }
        }
        // Si no es nuevo comprueba que sea un grupo válido y que existe el grupo
        else {
            groupID = Integer.parseInt(group);
            if (groupID < 1 || groupID > 250 || !GROUPS_ID.contains(groupID)) groupID = -1;
        }


        // Si el grupo es correcto informa al usuario del grupo
        if (groupID > -1) {
            GROUPS_ID.add(groupID);
            sendMessage(this, "Server", Integer.toString(groupID));
            loggedInGroup = true;
        }

        else sendMessage(this, "Server", "Was not possible to login in that group. Maybe is not created or could not create.");
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


    private ClientHandler findDirection(String userName) {
        for (ClientHandler client : CLIENT_CONNECTIONS) {
            if (client.userName.equals(userName)) return client;
        }

        return null;
    }


    private void log(String message) {
        System.out.println("CLIENT_HANDLER" + (userName != null ? " (" + userName + "): " : ": ") + message);
    }
}
