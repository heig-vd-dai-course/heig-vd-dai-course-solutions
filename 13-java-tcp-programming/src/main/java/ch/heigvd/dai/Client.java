package ch.heigvd.dai;

import java.io.*;
import java.net.Socket;

public class Client {
    final private String HOST;
    final private int PORT;

    public Client(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
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


        } catch (Exception e) {

        }
    }
}

