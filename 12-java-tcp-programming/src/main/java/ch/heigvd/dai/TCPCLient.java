package ch.heigvd.dai;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPCLient {
    final private String HOST;
    final private int PORT;

    public TCPCLient(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
        run();
    }

    private void run() {
        try (
                Socket socket = new Socket(HOST, PORT);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String serverMessage = reader.readLine();
            System.out.println("Server: " + serverMessage);

            String clientMessage;
            do {
                System.out.print("Enter command: ");
                clientMessage = consoleReader.readLine();

                writer.println(clientMessage);
                writer.flush();

                serverMessage = reader.readLine();
                System.out.println("Server: " + serverMessage);

            } while (!clientMessage.equalsIgnoreCase("QUIT"));

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

