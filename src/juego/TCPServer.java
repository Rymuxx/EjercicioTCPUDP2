package juego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

// Servidor TCP para el juego de adivinar el número.
public class TCPServer {

    public static void main(String[] args) {
        // Inicia el servidor en el puerto 6000
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Servidor TCP (Juego) iniciado en el puerto 12345. Esperando clientes...");

            while (true) {
                System.out.println("Servidor esperando en accept()...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("¡Cliente aceptado! Conectado desde " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private int numerosecreto;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        private void generarNumeroSecreto() {
            this.numerosecreto = new Random().nextInt(100) + 1;
            System.out.println("Nuevo número secreto generado para " + clientSocket.getInetAddress() + ": " + numerosecreto);
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                while (true) {
                    generarNumeroSecreto();
                    out.println("Adivina el número (entre 1 y 100)");

                    while (true) {
                        String inputLine = in.readLine();
                        if (inputLine == null) {
                            System.out.println("El cliente " + clientSocket.getInetAddress() + " cerró la conexión.");
                            return;
                        }
                        System.out.println("Recibido de " + clientSocket.getInetAddress() + ": " + inputLine);

                        try {
                            int adivina = Integer.parseInt(inputLine.trim());

                            if (adivina < numerosecreto) {
                                out.println("El número es mayor");
                            } else if (adivina > numerosecreto) {
                                out.println("El número es menor");
                            } else {
                                out.println("Número correcto ¿Jugar de nuevo? (s/n)");
                                break; // Adivinó, salir del bucle de la partida
                            }
                        } catch (NumberFormatException e) {
                            out.println("Entrada inválida. Introduce un número.");
                        }
                    }

                    // Después de adivinar, leer la respuesta para jugar de nuevo
                    String response = in.readLine();
                    if (response == null) {
                        System.out.println("El cliente " + clientSocket.getInetAddress() + " cerró la conexión antes de decidir jugar de nuevo.");
                        return;
                    }
                    System.out.println("Respuesta para jugar de nuevo de " + clientSocket.getInetAddress() + ": " + response);

                    if (!response.equalsIgnoreCase("s")) {
                        out.println("Gracias por jugar. Adios.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error de comunicación con el cliente " + clientSocket.getInetAddress() + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Conexión con " + clientSocket.getInetAddress() + " cerrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
