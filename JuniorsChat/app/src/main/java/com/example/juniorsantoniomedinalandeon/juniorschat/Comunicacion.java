package com.example.juniorsantoniomedinalandeon.juniorschat;

/**
 * Created by juniorsantoniomedinalandeon on 12/7/17.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
// Esta clase lo que nos va a permitir es conectar nuestro proyecto (aplicación ) a través de un canal de comunicación
                        //con el uso del DatagramSocket
public class Comunicacion implements Runnable {
    private DatagramSocket socket;
    private boolean running;

    private OnSocketListener onSocketListener; // Instancio la interfaz creada OnSocketListener

    public Comunicacion(OnSocketListener onSocketListener) {  // Creo un constructor en el cual le paso como parámetro la instancia de la interfaz creada
        this.onSocketListener=onSocketListener;
    }
    public void bind(int port) throws SocketException
    {
        socket = new DatagramSocket(port);
    }

    public void start()
    {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop()
    {
        running = false;
        socket.close();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while(running)
        {
            try
            {
                socket.receive(packet);

                String msg = new String(buffer, 0, packet.getLength());
                //System.out.println(msg);

                // Si el onSocketListener no está vacío, recibimos el mensaje que haya en él
                if(null!=onSocketListener){
                    onSocketListener.recibido(msg);
                }
            }
            catch (IOException e)
            {
                break;
            }
        }

    }
    public void sendTo(final InetSocketAddress address, final String msg)
    {
        // Puesto que trabajo con Udp, necesito implementar el método de la interfaz Runnable, ya que voy a generar hilos en segundo plano, para llevar a cabo este proceso de enviar y recibir mensajes de movil a movil
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] buffer = msg.getBytes();

                // Recordemos que los paquetes en UDP se llaman datagramas
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                // Para cada datagrama establezco el socket del destino
                packet.setSocketAddress(address);

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        // Creo el hilo secundario , hilo secundario es todo hilo que se va a ejecutar en segundo plano sin que la interfaz grafica de usuario se vea implicada o perjudicada
        Thread thread = new Thread(runnable);
        //Arraco o inicio el hilo
        thread.start();
    }
}
