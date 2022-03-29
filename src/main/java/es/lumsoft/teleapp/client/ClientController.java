package es.lumsoft.teleapp.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientController implements Runnable {

    private Socket connection;
    private BufferedReader reader;
    private BufferedWriter writer;
    private OnMessageReceivedListener messageReceivedListener = null;
    private String serverName;


    public ClientController(Socket connection, OnMessageReceivedListener messageReceivedListener) {
        try {
            this.connection = connection;
            this.messageReceivedListener = messageReceivedListener;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            // Espera el nombre del servidor
            serverName = reader.readLine();

        } catch (IOException e) {
            closeConnection(e);
        }
    }




    public String getServerName() {
        return serverName;
    }




    @Override
    public void run() {
        String sender, message;


        // Comienza a escuchar
        while (connection.isConnected()) {
            try {
                sender = reader.readLine();
                message = reader.readLine();

                // Ejecuta la acción que se haya dado con los datos recibidos
                if (messageReceivedListener != null)
                    messageReceivedListener.onMessageReceived(sender, message);

            } catch (IOException e) {
                closeConnection(e);
                break;
            }
        }
    }




    public void start(String userName) {
        try {
            writer.write(userName);
            writer.newLine();
            writer.flush();

            new Thread(this).start();

        } catch (IOException e) {
            closeConnection(e);
        }
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


    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public interface OnMessageReceivedListener {
        void onMessageReceived(String sender, String message);
    }


    private void log(String message) {
        System.out.println("CLIENT_CONTROLLER: " + message);
    }









    public static void main(String[] args) throws IOException, InterruptedException {
        ClientController clientController = new ClientController(
                new Socket("localhost", 2022),
                (sender, message) -> System.out.println(sender + ": " + message)
        );

        System.out.println("Connected to " + clientController.getServerName());
        System.out.print("Escribe el nombre de usuario: ");
        clientController.start(new Scanner(System.in).nextLine());
        System.out.println("Chat iniciado");


        while (true) {
            clientController.sendMessage(new Scanner(System.in).nextLine());
            Thread.sleep(200);
        }
    }
}
