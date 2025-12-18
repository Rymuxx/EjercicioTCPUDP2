package juego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) {
        try (
            Socket socket = new Socket("98.92.91.168", 6000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado al servidor. ¡Adivina el número!");

            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Servidor: " + fromServer);

                // Si el servidor se despide, terminamos el cliente.
                if (fromServer.contains("Gracias por jugar")) {
                    break;
                }

                // Si el servidor pide jugar de nuevo, enviamos la respuesta.
                if (fromServer.contains("¿Jugar de nuevo?")) {
                    System.out.print("Respuesta (s/n): ");
                    String response = scanner.nextLine();
                    out.println(response);
                } else {
                    System.out.print("Tu intento: ");
                    String userInput = scanner.nextLine();
                    out.println(userInput);
                }
            }
            System.out.println("Desconectado del servidor.");

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor o se perdió la conexión: " + e.getMessage());
        }
    }
}
