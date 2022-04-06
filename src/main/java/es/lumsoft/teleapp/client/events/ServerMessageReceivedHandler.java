package es.lumsoft.teleapp.client.events;

public interface ServerMessageReceivedHandler {

    void onServerMessageReceived(String type, String message);
}
