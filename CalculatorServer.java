package calculator;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Calculator Server
 * Handles arithmetic requests from multiple clients using thread pool.
 */
public class CalculatorServer {
    private static final int DEFAULT_PORT = 1234;
    private static final int THREAD_POOL_SIZE = 10;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;
    
    /** Initialize server socket and thread pool */
    public CalculatorServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        running = true;
        System.out.println("Calculator Server started on port " + port);
    }
    
    /** Accept client connections and handle requests */
    public void start() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) System.err.println("Error accepting client: " + e.getMessage());
            }
        }
    }
    
    /** Stop server and release resources */
    public void stop() {
        running = false;
        threadPool.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }
    
    /** Handles one client connection */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true)
            ) {
                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Received: " + request);
                    String response = processRequest(request);
                    out.println(response);
                    System.out.println("Sent: " + response);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Client disconnected");
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
        
        /** Parse and execute arithmetic operations */
        private String processRequest(String request) {
            try {
                String[] tokens = request.trim().split("\\s+");
                //1. CALC 확인
                //2. 인자 4개 확인
                //3. 숫자 파싱
                //4. 연산 수행
                //5. 결과 반환 
                if (tokens.length < 1 || !tokens[0].equals("CALC"))
                    return "400 Bad Request\nInvalid command";
                if (tokens.length != 4)
                    return "403 Invalid Argument Count\nExpected 2 operands";
                
                String operation = tokens[1];
                double op1, op2;
                try {
                    op1 = Double.parseDouble(tokens[2]);
                    op2 = Double.parseDouble(tokens[3]);
                } catch (NumberFormatException e) {
                    return "402 Invalid Operand\nOperands must be numbers";
                }
                
                double result;
                switch (operation.toUpperCase()) {
                    case "ADD": result = op1 + op2; break;
                    case "SUB": result = op1 - op2; break;
                    case "MUL": result = op1 * op2; break;
                    case "DIV":
                        if (op2 == 0) return "500 Error\nDivision by zero";
                        result = op1 / op2; break;
                    default:
                        return "401 Invalid Operation\nUse: ADD, SUB, MUL, DIV";
                }
                return "200 OK\n" + result;
            } catch (Exception e) {
                return "400 Bad Request\n" + e.getMessage();
            }
        }
    }
    
    /** Entry point */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default: " + DEFAULT_PORT);
            }
        }
        
        try {
            CalculatorServer server = new CalculatorServer(port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                server.stop();
            }));
            server.start();
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
            System.exit(1);
        }
    }
}
