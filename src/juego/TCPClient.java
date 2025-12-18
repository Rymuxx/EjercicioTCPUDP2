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
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado al servidor. ¡Adivina el número!");

            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Servidor: " + fromServer);

                if (fromServer.contains("Gracias por jugar")) {
                    break; // Salir del bucle si el servidor se despide
                }

                if (fromServer.contains("Número correcto")) {
                    System.out.print("Respuesta (s/n): ");
                    String response = scanner.nextLine();
                    out.println(response);
                    if (!response.equalsIgnoreCase("s")) {
                    }
                } else {
                    System.out.print("Tu intento: ");
                    String userInput = scanner.nextLine();
                    out.println(userInput);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
