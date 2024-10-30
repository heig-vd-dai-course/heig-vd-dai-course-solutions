package ch.heigvd.dai.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
    name = "operator",
    description =
        "Start the operator part of the network application using the request-reply messaging pattern.")
public class Operator implements Callable<Integer> {

  public enum Message {
    REQ_TEMP,
    HELP,
    QUIT,
  }
  public static String END_OF_LINE = "\n";

  @CommandLine.Option(
          names = {"-H", "--host"},
          description = "Host to connect to.",
          required = true)
  protected String host;

  @CommandLine.Option(
          names = {"-p", "--port"},
          description = "Port to use (default: ${DEFAULT-VALUE}).",
          defaultValue = "1732")
  protected int port;

  @Override
  public Integer call() {
    System.out.println("[CLIENT] Sending request...");


    try (DatagramSocket socket = new DatagramSocket()) {
      // Get the server address
      InetAddress serverAddress = InetAddress.getByName(host);

      while (!socket.isClosed()) {
        System.out.print("> ");

        Reader inputReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader bir = new BufferedReader(inputReader);
        String userInput = bir.readLine();

        try {
          String[] userInputParts = userInput.split(" ", 2);
          Message message = Message.valueOf(userInputParts[0].toUpperCase());

          String request = null;

          switch (message) {
            case REQ_TEMP -> {
              int number = Integer.parseInt(userInputParts[1]);

              request = Message.REQ_TEMP + " " + number + END_OF_LINE;
            }
            case HELP -> help();

            case QUIT -> {
              socket.close();
              continue;
            }
          }

          if (request != null) {
            // Transform the message into a byte array - always specify the encoding
            byte[] buffer = request.getBytes(StandardCharsets.UTF_8);

            // Create a packet with the message, the server address and the port
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, port);

            // Send the packet
            socket.send(packet);

            System.out.println("[CLIENT] Request sent: " + request);
          }
        } catch (Exception e) {
          System.out.println("Invalid command. Please try again.");
        }

      }

      // Create a buffer for the incoming response
      byte[] responseBuffer = new byte[1024];

      // Create a packet for the incoming response
      DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

      // Receive the packet - this is a blocking call
      socket.receive(responsePacket);

      // Transform the message into a string
      String response =
              new String(
                      responsePacket.getData(),
                      responsePacket.getOffset(),
                      responsePacket.getLength(),
                      StandardCharsets.UTF_8);

      String[] responseParts = response.split(" ", 2);

      switch (responseParts[0]){
        case "TEMP" -> System.out.println("La température est de " + responseParts[1]);
        case "ERROR" -> {
          if (responseParts.length < 2){
            System.out.println("Invalid message, please try again");
            break;
          }

          System.out.println("Error " + responseParts[1]);
        }
      }
      //System.out.println("[CLIENT] Received response: " + response);
    } catch (Exception e) {
      System.err.println("[CLIENT] An error occurred: " + e.getMessage());
    }

    System.out.println("[CLIENT] Quitting...");
    return 0;
  }

  private static void help() {
    System.out.println("Usage:");
    System.out.println("  " + Message.REQ_TEMP + " <roomId> - Requests the temperature of a room.");
    System.out.println("  " + Message.QUIT + " - Ends the application.");
    System.out.println("  " + Message.HELP + " - Display this help message.");
  }
}
