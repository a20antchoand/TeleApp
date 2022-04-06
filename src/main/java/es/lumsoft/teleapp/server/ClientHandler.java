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
    private Integer loggedInGroup = null;


    public ClientHandler(Socket connection) {
        try {
            this.connection = connection;
            reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.connection.getOutputStream()));

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
                    else sendMessage(this, "Server", "*error: Bad request.");
                }

            } catch (IOException e) {
                closeConnection(e);
                break;
            }
        }
    }




    // * Connection functions

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



    // * Server functions

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
                    if (params.size() > 1)
                        sendMessage(this, "Server", login(params.get(0), params.get(1)));

                    else
                        sendMessage(
                                this,
                                "Server",
                                "*error: Too few arguments, should be #login 'userName' 'group ID'|'new' (Groups listed with #groups)"
                        );
                }
                case "logout" -> closeConnection();
                case "groups" -> sendMessage(this, "Server: *info: ", GROUPS_ID.toString());
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
                            sendMessage(clientHandler, this.userName, finalMessage.toString());
                        else
                            sendMessage(this, "Server", "*error: Contact not found.");
                    }

                    else
                        sendMessage(
                                this,
                                "Server",
                                "*error: Too few arguments, should be: #private 'userName' 'message'"
                        );
                }

                default -> sendMessage(this, "Server", "*error: Invalid command.");
            }
        }

        else
            sendMessage(this, "Server", "*error: Invalid command.");
    }


    private String login(String userName, String group) {
        int groupID = -1;
        this.userName = (!userName.equals("Server") ? userName : userName + "Fake");


        // Comprueba si se tiene que crear un grupo o se tiene que entrar en uno creado

        // Si es un grupo nuevo busca un espacio nuevo
        if (group.equals("new")) {
            for (int i = 1; i <= 250; i++) {
                if (!GROUPS_ID.contains(i)) {
                    groupID = i;
                    GROUPS_ID.add(groupID);
                    break;
                }
            }
        }
        // Si no es nuevo comprueba que sea un grupo válido y que existe el grupo
        else {
            try {
                groupID = Integer.parseInt(group);

            } catch (NumberFormatException e) {
                return "*error: Group ID is not a number";
            }

            if (groupID < 1 || groupID > 250 || !GROUPS_ID.contains(groupID)) groupID = -1;
        }


        // Informa del resultado de la operación

        // Si el grupo es correcto informa al usuario del grupo
        if (groupID > -1 && loggedInGroup == null) {
            loggedInGroup = groupID;
            return "*login: group: " + groupID + " username: " + this.userName;
        }

        else if (loggedInGroup != null) {
            // TODO logout del grupo actual para que si se queda sin usuarios un grupo se elimine
            loggedInGroup = groupID;
            return "*login: group: " + groupID + " username: " + this.userName;
        }

        else return "*error: Was not possible to login in that group.";
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
