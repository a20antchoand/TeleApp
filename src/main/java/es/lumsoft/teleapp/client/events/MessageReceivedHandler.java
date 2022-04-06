package es.lumsoft.teleapp.client.events;

public interface MessageReceivedHandler {
    void onMessageReceived(String sender, String message);
}
