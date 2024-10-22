package org.example;
import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPServer {
    final private int PORT;
    final private int UPPER_BOUND;
    final private int LOWER_BOUND;
    private int number;

    public TCPServer(int port, int UB, int LB) {
        PORT = port;
        UPPER_BOUND = UB;
        LOWER_BOUND = LB;
        start();
        generateRandomNumber();
    }

    private void generateRandomNumber() {
        Random rand = new Random();
        number = rand.nextInt(LOWER_BOUND, UPPER_BOUND);
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            String clientMessage;
            System.out.println("Listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept(); // Accept client connection
                System.out.println("New client connected");

                try (InputStream input = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                     OutputStream output = socket.getOutputStream();
                     PrintWriter writer = new PrintWriter(output, true)) {

                    writer.println("The game has started, guess a number between : " + LOWER_BOUND + " and " + UPPER_BOUND + ".");

                    while ((clientMessage = reader.readLine()) != null) {
                        System.out.println("Client guessed: " + clientMessage);

                        if (clientMessage.equalsIgnoreCase("QUIT")) {
                            writer.println("Goodbye!");
                            break;
                        }

                        if (clientMessage.equalsIgnoreCase("RESTART")) {
                            generateRandomNumber();
                            writer.println("Game restarted! Guess a number between 1 and 100.");
                            continue;
                        }

                        try {
                            int guess = Integer.parseInt(clientMessage);

                            if (guess < number) {
                                writer.println("HIGHER");
                            } else if (guess > number) {
                                writer.println("LOWER");
                            } else {
                                writer.println("CORRECT! You've guessed the number!");
                            }

                        } catch (NumberFormatException e) {
                            writer.println("Invalid input. Please enter a valid number.");
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}