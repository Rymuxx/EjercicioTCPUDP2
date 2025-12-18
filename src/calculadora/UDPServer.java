package calculadora;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(54321)) {
            System.out.println("Servidor UDP iniciado. Esperando clientes...");

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                ClientHandler clientHandler = new ClientHandler(socket, receivePacket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para manejar las solicitudes de los clientes en hilos separados
    private static class ClientHandler implements Runnable {
        private final DatagramSocket socket;
        private final DatagramPacket receivePacket;

        public ClientHandler(DatagramSocket socket, DatagramPacket receivePacket) {
            this.socket = socket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            // Obtiene la dirección IP y el puerto del cliente
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            System.out.println("Cliente conectado desde " + clientAddress + ":" + clientPort);

            try {
                // Convierte los datos recibidos a una cadena
                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                // Convierte la cadena a un número entero
                int number = Integer.parseInt(receivedData.trim());

                // Encuentra los números primos hasta el número recibido
                String primos = encontrarPrimos(number);

                // Convierte la cadena de números primos a bytes
                byte[] sendBuffer = primos.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(sendPacket);
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error al procesar la solicitud del cliente " + clientAddress + ":" + clientPort + ": " + e.getMessage());
            }
        }
    }

    private static String encontrarPrimos(int limite) {
        List<Integer> primos = new ArrayList<>();
        // Itera desde 2 hasta el límite
        for (int i = 2; i <= limite; i++) {
            // Si el número es primo, lo añade a la lista
            if (esPrimo(i)) {
                primos.add(i);
            }
        }
        // Convierte la lista de primos a una cadena y la devuelve
        return primos.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    private static boolean esPrimo(int num) {
        if (num <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
