package juego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor TCP (Juego) iniciado en el puerto 12345. Esperando clientes...");

            while (true) {
                // Acepta una nueva conexión de cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());
                // Crea un nuevo hilo para manejar al cliente y lo inicia
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private int numerosecreto;
        private boolean running = true;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            generarNumeroSecreto();
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
                out.println("Adivina el número (entre 1 y 100)");

                String inputLine;
                // Bucle para leer los intentos del cliente mientras la conexión esté activa
                while (running && (inputLine = in.readLine()) != null) {
                    try {
                        // Convierte la entrada del cliente a un número entero
                        int adivina = Integer.parseInt(inputLine);

                        if (adivina < numerosecreto) {
                            out.println("El número es mayor");
                        } else if (adivina > numerosecreto) {
                            out.println("El número es menor");
                        } else {
                            // Si el cliente adivina el número
                            out.println("Número correcto ¿Jugar de nuevo? (s/n)");
                            // Lee la respuesta del cliente para jugar de nuevo
                            String response = in.readLine();
                            if (response != null && response.equalsIgnoreCase("s")) {
                                generarNumeroSecreto();
                                out.println("Adivina el número (entre 1 y 100)");
                            } else {
                                out.println("Gracias por jugar. Adios.");
                                running = false;
                            }
                        }
                    } catch (NumberFormatException e) {
                        out.println("Entrada inválida. Introduce un número.");
                    }
                }
            } catch (IOException e) {
                // Se produce si el cliente se desconecta inesperadamente
                System.out.println("Cliente " + clientSocket.getInetAddress() + " desconectado abruptamente.");
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
