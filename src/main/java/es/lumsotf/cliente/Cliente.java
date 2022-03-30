package es.lumsotf.cliente;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    private int puerto;
    String mensajeRecibido;
    Scanner input = new Scanner(System.in);

    /*public Cliente (int puerto, String mensaje){
        this.puerto=puerto;
        this.mensaje=mensaje;
    }*/
    public void initCliente(){
        final String HOST = "localhost";
        final int PUERTO = 9999;
        DataInputStream in;
        DataOutputStream out;
        try{
            Socket sc = new Socket(HOST, PUERTO);
            sc = new Socket(HOST, PUERTO);
            out = new DataOutputStream(sc.getOutputStream());
            in = new DataInputStream(sc.getInputStream());
            String mensaje = "";
            while(!mensaje.equals("x")){
                System.out.println("Escriba un msn para enviar");
                mensaje = input.nextLine();
                out.writeUTF(mensaje);//enviamos mensaje
                mensajeRecibido = in.readUTF();//Leemos respuesta
                System.out.println(mensajeRecibido);
            }
            sc.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Cliente o = new Cliente();
        o.initCliente();
    }

/*
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
    }*/

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
