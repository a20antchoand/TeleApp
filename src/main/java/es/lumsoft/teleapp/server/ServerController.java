package es.lumsoft.teleapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {

    private final ServerSocket ServerSocket;


    public ServerController() throws IOException {
        ServerSocket = new ServerSocket(2022);
    }
    public ServerController(ServerSocket serverSocket) {
        ServerSocket = serverSocket;
    }
    public ServerController(int port) throws IOException {
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
                new ClientHandler(inConnecton).start();
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
        ServerController serverController = new ServerController();
        serverController.startServer();
    }
}
