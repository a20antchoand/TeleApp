package es.lumsoft.teleapp.client;

import es.lumsoft.teleapp.client.events.MessageReceivedHandler;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupController implements Runnable {

    private final int GroupId;
    private final String UserName;


    private MulticastSocket inConnection;
    private DatagramSocket outConnection;
    private MessageReceivedHandler messageReceivedHandler;
    boolean closeConnection = false;


    public GroupController(String userName, int groupId, MessageReceivedHandler messageReceivedHandler) throws IOException {
        GroupId = groupId;
        UserName = userName;
        this.messageReceivedHandler = messageReceivedHandler;

        // Conecta el socket de escucha
        inConnection = new MulticastSocket(2023);
        inConnection.joinGroup(
                new InetSocketAddress("239.0.0." + GroupId, 2023),
                NetworkInterface.getByName("wlan0")
        );

        // Crea el socket de escritura
        outConnection = new DatagramSocket();

        // Empieza a escuchar
        new Thread(this).start();
    }




    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        String data, sender, message;
        Pattern messagePattern = Pattern.compile("(^[^:]+): (.*)");
        Matcher messageMatcher;


        try {
            while (!closeConnection) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);


                inConnection.receive(datagramPacket);
                data = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                messageMatcher = messagePattern.matcher(data);

                if (messageMatcher.matches()) {
                    sender = messageMatcher.group(1);
                    message = messageMatcher.group(2);
                    messageReceivedHandler.onMessageReceived(sender, message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String message) throws IOException {
        message = UserName + ": " + message;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket datagramPacket = new DatagramPacket(
                messageBytes,
                messageBytes.length,
                new InetSocketAddress("239.0.0." + GroupId, 2023)
        );


        outConnection.send(datagramPacket);
    }
}
