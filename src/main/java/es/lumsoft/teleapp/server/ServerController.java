package es.lumsoft.teleapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {

    private final ServerSocket ServerSocket;
    private final String ServerName;


    public ServerController(String serverName) throws IOException {
        ServerName = serverName;
        ServerSocket = new ServerSocket(2022);
    }
    public ServerController(ServerSocket serverSocket, String serverName) {
        ServerSocket = serverSocket;
        ServerName = serverName;
    }
    public ServerController(String serverName, int port) throws IOException {
        ServerName = serverName;
        ServerSocket = new ServerSocket(port);
    }




    public void startServer() {
        try {
            log("Server started on " +
                    ServerSocket.getInetAddress().getHostAddress() + ":" +
                    ServerSocket.getLocalPort());

            while (!ServerSocket.isClosed()) {
                Socket inConnecton = ServerSocket.accept();


                log("New connection.");
                new ClientHandler(inConnecton, ServerName).start();
            }

        } catch (IOException e) {
            closeServer();
        }
    }


    public void closeServer() {
        if (ServerSocket != null) {
            try {
                log("Closing server...");
                ServerSocket.close();
                log("Server closed.");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void log(String message) {
        System.out.println("SERVER_CONTROLLER: " + message);
    }






    public static void main(String[] args) throws IOException {
        ServerController serverController = new ServerController("Servidor de Mauro");
        serverController.startServer();
    }
}
