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
            System.out.println("Server started on " +
                    ServerSocket.getInetAddress().getHostAddress() + ":" +
                    ServerSocket.getLocalPort());

            while (!ServerSocket.isClosed()) {
                Socket inConnecton = ServerSocket.accept();
                ClientHandler clientHandler;


                System.out.println("New connection.");
                clientHandler = new ClientHandler(inConnecton);
                clientHandler.start();
            }

        } catch (IOException e) {
            closeServer();
        }
    }


    public void closeServer() {
        if (ServerSocket != null) {
            try {
                System.out.println("Closing server...");
                ServerSocket.close();
                System.out.println("Server closed.");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
