package cliente;

import java.net.*;
import java.util.Scanner;

public class ClienteUDP {
    private static final String SERVIDOR_HOST = "localhost";
    private static final int SERVIDOR_PUERTO = 5001;
    private static final int TIMEOUT = 5000;

    public static void main(String[] args) {
        String host = SERVIDOR_HOST;
        int puerto = SERVIDOR_PUERTO;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                puerto = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto inválido, usando puerto por defecto: " + SERVIDOR_PUERTO);
            }
        }

        System.out.println("Cliente UDP - Calculadora de Primos");
        System.out.println("Servidor: " + host + ":" + puerto + "\n");

        try (
                DatagramSocket socket = new DatagramSocket();
                Scanner scanner = new Scanner(System.in)) {
            socket.setSoTimeout(TIMEOUT);
            InetAddress serverAddress = InetAddress.getByName(host);

            while (true) {
                System.out.print("Introduce un número entero positivo (0 para salir): ");
                String input = scanner.nextLine();

                if (input.trim().equals("0")) {
                    System.out.println("Hasta luego");
                    break;
                }

                try {
                    int numero = Integer.parseInt(input.trim());

                    if (numero < 1) {
                        System.out.println("Error: Debes introducir un número positivo\n");
                        continue;
                    }

                    byte[] sendData = input.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                            serverAddress, puerto);
                    socket.send(sendPacket);

                    byte[] receiveData = new byte[65535];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    String respuesta = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    System.out.println("\nNúmeros primos menores o iguales a " + numero + ":");
                    System.out.println(respuesta);
                    System.out.println();

                } catch (NumberFormatException e) {
                    System.out.println("Error: Debes introducir un número válido\n");
                } catch (SocketTimeoutException e) {
                    System.out.println("Error: Timeout esperando respuesta del servidor\n");
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("No se pudo encontrar el host: " + host);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
