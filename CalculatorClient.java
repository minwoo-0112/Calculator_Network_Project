package calculator;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Calculator Client
 * Connects to the server, sends requests, and displays results.
 */
public class CalculatorClient {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    /** Load server address and port from config file */
    public CalculatorClient(String configFile) {
        ServerConfig config = new ServerConfig(configFile);
        this.serverAddress = config.getServerAddress();
        this.serverPort = config.getServerPort();
    }
    
    /** Connect to the calculator server */
    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected to server: " + serverAddress + ":" + serverPort);
    }
    
    /** Send a calculation request and print server response */
    public void sendRequest(String operation, double operand1, double operand2) {
        try {
            String request = String.format("CALC %s %.2f %.2f", 
                operation.toUpperCase(), operand1, operand2);
            System.out.println("\n[Request] " + request);
            out.println(request);
            
            // Receive 2-line response: status + result
            String statusLine = in.readLine();
            String resultLine = in.readLine();
            
            System.out.println("[Response]");
            System.out.println("  Status: " + statusLine);
            
            String[] statusParts = statusLine.split("\\s+", 2);
            int statusCode = Integer.parseInt(statusParts[0]);
            
            if (statusCode == 200) {
                System.out.println("  Result: " + resultLine);
                System.out.println("\n✓ " + operand1 + " " +
                    getOperationSymbol(operation) + " " + operand2 +
                    " = " + resultLine);
            } else {
                System.out.println("  Error: " + resultLine);
                System.out.println("\n✗ Error occurred: " + resultLine);
            }
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid response format from server");
        }
    }
    
    /** Convert operation keyword to math symbol */
    private String getOperationSymbol(String operation) {
        switch (operation.toUpperCase()) {
            case "ADD": return "+";
            case "SUB": return "-";
            case "MUL": return "×";
            case "DIV": return "÷";
            default: return operation;
        }
    }
    
    /** Close connection and release resources */
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /** Run interactive mode (user inputs operations) */
    public void runInteractive() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Calculator Client ===");
        System.out.println("Available operations: ADD, SUB, MUL, DIV");
        System.out.println("Enter 'quit' to exit\n");
        
        while (true) {
            try {
                System.out.print("Enter operation (e.g., ADD 10 20): ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("quit")) break;
                
                String[] parts = input.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Invalid format. Use: OPERATION NUM1 NUM2");
                    continue;
                }
                
                String operation = parts[0];
                double operand1 = Double.parseDouble(parts[1]);
                double operand2 = Double.parseDouble(parts[2]);
                
                sendRequest(operation, operand1, operand2);
            } catch (NumberFormatException e) {
                System.out.println("Invalid numbers. Please try again.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
    
    /** Main method: connect, run, disconnect */
    public static void main(String[] args) {
        String configFile = "server_info.dat";
        if (args.length > 0) configFile = args[0];
        
        CalculatorClient client = new CalculatorClient(configFile);
        try {
            client.connect();
            client.runInteractive();
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            System.exit(1);
        } finally {
            client.disconnect();
        }
    }
}

/**
 * ServerConfig
 * Load server IP and port from file (default: localhost:1234)
 */
class ServerConfig {
    private static final String DEFAULT_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 1234;
    
    private String serverAddress;
    private int serverPort;
    
    /** Read config file or use defaults */
    public ServerConfig(String configFile) {
        serverAddress = DEFAULT_ADDRESS;
        serverPort = DEFAULT_PORT;
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (key.equals("SERVER_ADDRESS")) serverAddress = value;
                    else if (key.equals("SERVER_PORT")) {
                        try { serverPort = Integer.parseInt(value); }
                        catch (NumberFormatException e) {
                            System.err.println("Invalid port in config file. Using default.");
                        }
                    }
                }
            }
            System.out.println("Loaded server config from file: " + configFile);
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found. Using default: "
                + DEFAULT_ADDRESS + ":" + DEFAULT_PORT);
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
            System.out.println("Using default values.");
        }
    }
    
    /** Get server IP/hostname */
    public String getServerAddress() { return serverAddress; }
    
    /** Get server port */
    public int getServerPort() { return serverPort; }
}
