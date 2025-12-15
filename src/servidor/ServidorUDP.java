package servidor;

import java.net.*;

public class ServidorUDP {
    private static final int PUERTO = 5001;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            System.out.println("Servidor UDP - Calculadora de Primos");
            System.out.println("Servidor escuchando en puerto " + PUERTO);
            System.out.println("Esperando peticiones de clientes...\n");

            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                Thread thread = new Thread(new CalculadoraPrimos(socket, receivePacket));
                thread.start();

                receiveData = new byte[1024];
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
