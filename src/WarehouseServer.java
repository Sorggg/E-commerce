import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WarehouseServer {
    private static final int PORT = 9999;
    private static final String FILE_PATH = "warehouse_data.txt";

    private Map<Integer, Integer> productQuantities = new HashMap<>();
    private final Object lock = new Object();

    public static void main(String[] args) {
        WarehouseServer server = new WarehouseServer();
        server.loadWarehouseData();

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket, server));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWarehouseData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("_");
                if (parts.length == 2) {
                    int code = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);
                    productQuantities.put(code, quantity);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    int getQuantity(int productCode) {
        synchronized (lock) {
            return productQuantities.getOrDefault(productCode, 0);
        }
    }

    void setQuantity(int productCode, int newQuantity) {
        synchronized (lock) {
            productQuantities.put(productCode, newQuantity);
        }
    }

}
