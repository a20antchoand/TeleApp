package es.lumsotf.cliente;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
    private int puerto;
    private String mensaje;

    public Cliente (int puerto, String mensaje){
        this.puerto=puerto;
        this.mensaje=mensaje;
    }
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 9999;
        DataInputStream in;
        DataOutputStream out;

        try {
            Socket sc = new Socket(HOST, PUERTO);

            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            out.writeUTF("El cliente saluda a Lumsotf: ");

            String mensaje = in.readUTF();
            System.out.println(mensaje);
            sc.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    @Override
    public void run() {
        final String HOST = "localhost";
        final int PUERTO = 9999;
        DataInputStream in;
        DataOutputStream out;

        try {
            //Crear Socket para conectarlo con el cliente
            Socket sc = new Socket(HOST, puerto);

            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            //Enviar mensaje
            out.writeUTF("El cliente saluda a Lumsotf: ");

            String mensaje = in.readUTF();
            System.out.println(mensaje);
            sc.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
