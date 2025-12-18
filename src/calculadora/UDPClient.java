package calculadora;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    final static String IP = "98.92.91.168";
    final static int PUERTO = 5000;
    public static void main(String[] args) {
        try (
            DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)
        ) {
            InetAddress serverAddress = InetAddress.getByName(IP);

            System.out.print("Introduce un número entero positivo: ");
            String userInput = scanner.nextLine();

            byte[] sendBuffer = userInput.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, PUERTO);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Números primos: " + receivedData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
