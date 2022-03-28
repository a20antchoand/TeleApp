package es.lumsotf.cliente;

import javafx.beans.InvalidationListener;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class ServidorPrueba {
    private int puerto;

    public ServidorPrueba(int puerto){
        this.puerto=puerto;
    }

    public static void main (String[] args){
        ServerSocket servidor = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;

        final int PUERTO = 9999;

        try {
            servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");

            while(true){
                sc = servidor.accept();
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());

                String mensaje = in.readUTF();

                System.out.println(mensaje);

                out.writeUTF("El servidor de Lumsoft te saluda: ");

                sc.close();
                System.out.println("Cliente deconectado");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    @Override
    public void run() {
        ServerSocket servidor = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;

        //final int PUERTO = 9999;

        try {
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor iniciado");

            while(true){
                sc = servidor.accept();
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());

                //Leer mensaje del cliente
                String mensaje = in.readUTF();

                System.out.println(mensaje);

                //Revisa el mensaje
                this.setChanged();
                this.notifyObservers(mensaje);
                this.clearChanged();

                out.writeUTF("El servidor de Lumsoft te saluda: ");

                //Cierro el socket
                sc.close();
                System.out.println("Cliente deconectado");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
