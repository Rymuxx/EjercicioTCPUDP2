package servidor;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraPrimos implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket receivePacket;

    public CalculadoraPrimos(DatagramSocket socket, DatagramPacket receivePacket) {
        this.socket = socket;
        this.receivePacket = receivePacket;
    }

    @Override
    public void run() {
        try {
            String mensaje = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            System.out.println("SERVIDOR Petición de " + clientAddress.getHostAddress() +
                    ":" + clientPort + " - Número: " + mensaje);

            int numero = Integer.parseInt(mensaje.trim());

            if (numero < 1) {
                String error = "Error: El número debe ser positivo";
                byte[] sendData = error.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        clientAddress, clientPort);
                socket.send(sendPacket);
                return;
            }

            List<Integer> primos = calcularPrimos(numero);

            StringBuilder resultado = new StringBuilder();
            for (int i = 0; i < primos.size(); i++) {
                resultado.append(primos.get(i));
                if (i < primos.size() - 1) {
                    resultado.append(",");
                }
            }

            String respuesta = resultado.toString();
            System.out.println("SERVIDOR: Enviando " + primos.size() + " números primos");

            byte[] sendData = respuesta.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    clientAddress, clientPort);
            socket.send(sendPacket);

        } catch (NumberFormatException e) {
            System.err.println("ERROR: Número inválido recibido");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Integer> calcularPrimos(int n) {
        List<Integer> primos = new ArrayList<>();

        if (n < 2) {
            return primos;
        }

        boolean[] esPrimo = new boolean[n + 1];
        for (int i = 2; i <= n; i++) {
            esPrimo[i] = true;
        }

        for (int p = 2; p * p <= n; p++) {
            if (esPrimo[p]) {
                for (int i = p * p; i <= n; i += p) {
                    esPrimo[i] = false;
                }
            }
        }

        for (int i = 2; i <= n; i++) {
            if (esPrimo[i]) {
                primos.add(i);
            }
        }

        return primos;
    }
}
