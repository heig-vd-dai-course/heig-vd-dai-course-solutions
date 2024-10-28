package ch.heigvd.dai;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServer {
    final private int PORT;
    private int UPPER_BOUND;
    private int LOWER_BOUND;
    private int number;

    public TCPServer(int port, int LB, int UB) {
        PORT = port;
        UPPER_BOUND = UB;
        LOWER_BOUND = LB;
        start();
    }

    private void generateRandomNumber() {
        Random rand = new Random();
        number = rand.nextInt(UPPER_BOUND - LOWER_BOUND + 1) + LOWER_BOUND;
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            String clientMessage;
            System.out.println("Listening on port " + PORT);

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // Accept client connection
                System.out.println("[Server] New client connected from "
                        + socket.getInetAddress().getHostAddress()
                        + ":"
                        + socket.getPort());

                generateRandomNumber();
                try (InputStream input = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                     OutputStream output = socket.getOutputStream();
                     PrintWriter writer = new PrintWriter(output, true)) {

                    send(writer, "Welcome to the guess the number game.");

                    while ((clientMessage = reader.readLine()) != null) {
                        System.out.println("Client sent :" + clientMessage);

                        String[] messageParts = clientMessage.split(" ");
                        String command = messageParts[0].toUpperCase();

                        switch (command) {
                            case "QUIT":
                                send(writer, "Goodbye!");
                                serverSocket.close();
                                break;

                            case "RESTART":
                                send(writer, restartGame());
                                break;
                            case "BOUND":
                                if (messageParts.length == 3) {
                                    try {
                                        LOWER_BOUND = Integer.parseInt(messageParts[1]);
                                        UPPER_BOUND = Integer.parseInt(messageParts[2]);
                                        if (LOWER_BOUND > UPPER_BOUND) {
                                            LOWER_BOUND = UPPER_BOUND;
                                        }
                                        send(writer, restartGame());
                                    } catch (NumberFormatException e) {
                                        send(writer, "Invalid bounds. Please provide valid numbers.");
                                    }
                                } else {
                                    send(writer, "Invalid BOUND command. Use: BOUND <lower_bound> <upper_bound>");
                                }
                                break;

                            case "GUESS":
                                if (messageParts.length == 2) {
                                    try {
                                        int guess = Integer.parseInt(messageParts[1]);

                                        if (guess < LOWER_BOUND || guess > UPPER_BOUND) {
                                            send(writer, "Invalid guess. Guess a number between " + LOWER_BOUND + " and " + UPPER_BOUND + ".");
                                        }
                                        if (guess < number) {
                                            send(writer, "HIGHER");
                                        } else if (guess > number) {
                                            send(writer, "LOWER");
                                        } else {
                                            send(writer, "CORRECT! You've guessed the number!");
                                        }

                                    } catch (NumberFormatException e) {
                                        send(writer, "Invalid input. Please enter a valid number.");
                                    }
                                } else {
                                    send(writer, "Invalid GUESS command. Use: GUESS <number>");
                                }
                                break;

                            default:
                                send(writer, "Unknown command. Please use QUIT, RESTART, BOUND, or GUESS.");
                                break;
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

    private void send(PrintWriter writer, String message) {
        writer.println(message);
        writer.flush();
    }

    private String restartGame() {
        generateRandomNumber();
        return ("Game restarted! Guess a number between " + LOWER_BOUND + " and " + UPPER_BOUND + ".");
    }
}
