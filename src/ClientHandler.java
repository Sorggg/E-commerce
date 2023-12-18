import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private WarehouseServer server;

    public ClientHandler(Socket clientSocket, WarehouseServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;
            while ((request = reader.readLine()) != null) {
                String[] parts = request.split(" ");
                if (parts.length >= 2) {
                    String command = parts[0];
                    int productCode = Integer.parseInt(parts[1]);

                    switch (command) {
                        case "GET":
                            int quantity = server.getQuantity(productCode);
                            writer.println("Quantity of product " + productCode + ": " + quantity);
                            break;
                        case "SET":
                            if (parts.length == 3) {
                                int newQuantity = Integer.parseInt(parts[2]);
                                server.setQuantity(productCode, newQuantity);
                                writer.println("Quantity of product " + productCode + " set to: " + newQuantity);
                            } else {
                                writer.println("Invalid SET command format");
                            }
                            break;
                        default:
                            writer.println("Invalid command");
                    }
                } else {
                    writer.println("Invalid command format");
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
