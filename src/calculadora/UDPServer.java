package calculadora;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    final static int PUERTO = 5000;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            System.out.println("Servidor UDP listo en puerto " + PUERTO + ". Esperando peticiones...");

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                new Thread(new ClientHandler(socket, receivePacket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final DatagramSocket socket;
        private final DatagramPacket receivePacket;

        public ClientHandler(DatagramSocket socket, DatagramPacket receivePacket) {
            this.socket = socket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            try {
                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                System.out.println("Cliente [" + clientAddress + "] solicita primos hasta: " + receivedData);

                int number = Integer.parseInt(receivedData);
                String result = encontrarPrimos(number);

                byte[] sendBuffer = result.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(sendPacket);
            } catch (Exception e) {
                System.err.println("Error procesando cliente " + clientAddress + ": " + e.getMessage());
            }
        }
    }

    private static String encontrarPrimos(int limite) {
        List<Integer> primos = new ArrayList<>();
        for (int i = 2; i <= limite; i++) {
            if (esPrimo(i)) primos.add(i);
        }
        String res = primos.toString().replace("[", "").replace("]", "").replace(" ", "");
        return res.isEmpty() ? "No hay primos" : res;
    }

    private static boolean esPrimo(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }
}