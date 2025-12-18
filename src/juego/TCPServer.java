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
        // Inicia el servidor en el puerto 12345
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Servidor TCP (Juego) iniciado en el puerto 12345. Esperando clientes...");

            // Bucle infinito para aceptar conexiones de clientes
            while (true) {
                System.out.println("Servidor esperando en accept()...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("¡Cliente aceptado! Conectado desde " + clientSocket.getInetAddress());
                // Crea un nuevo hilo para manejar al cliente y lo inicia
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Clase interna para manejar a cada cliente en un hilo separado
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private int numerosecreto;

        // Constructor que recibe el socket del cliente
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
                // Bucle para permitir jugar varias partidas
                while (true) {
                    generarNumeroSecreto();
                    out.println("Adivina el número (entre 1 y 100)");

                    // Bucle para una sola partida
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
                        return; // Termina el hilo
                    }
                    System.out.println("Respuesta para jugar de nuevo de " + clientSocket.getInetAddress() + ": " + response);

                    if (!response.equalsIgnoreCase("s")) {
                        out.println("Gracias por jugar. Adios.");
                        break; // No quiere jugar más, salir del bucle de partidas
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
