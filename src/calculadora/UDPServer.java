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

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                System.out.println("Cliente conectado desde " + clientAddress + ":" + clientPort);

                new Thread(() -> {
                    try {
                        String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        int number = Integer.parseInt(receivedData.trim());

                        String primes = encontrarPrimos(number);

                        byte[] sendBuffer = primes.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                        socket.send(sendPacket);
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encontrarPrimos(int limite) {
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limite; i++) {
            if (esPrimo(i)) {
                primes.add(i);
            }
        }
        return primes.toString().replace("[", "").replace("]", "").replace(" ", "");
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
