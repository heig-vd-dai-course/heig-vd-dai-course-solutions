package ch.heigvd.dai.commands;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
    name = "emitter",
    description =
        "Start the emitter part of the network application using the fire-and-forget messaging pattern.")
public class Emitter implements Callable<Integer> {
  public static String END_OF_LINE = "\n";
  private static String MESSAGE = "TEMP ";
  private static int ID = 1;
  @CommandLine.Option(
      names = {"-M", "--multicast-address"},
      description = "Multicast address to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "230.1.2.3")
  protected String multicastAddress;

  @CommandLine.Option(
      names = {"-P", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "7337")
  protected int port;

  @CommandLine.Option(
      names = {"-F", "--frequency"},
      description =
          "Frequency of sending the message (in milliseconds) (default: ${DEFAULT-VALUE}).",
      defaultValue = "5000")
  protected int frequency;

  @Override
  public Integer call() {
    int temp = 20;
    System.out.println("[EMITTER] Sending multicast messages...");

    while (true) {
      String messageWithDate = MESSAGE + ID + " " + temp++ + END_OF_LINE;

      // Create a datagram socket
      try (DatagramSocket socket = new DatagramSocket()) {
        // Get the multicast address
        InetAddress castAdress = InetAddress.getByName(multicastAddress);

        // Transform the message into a byte array - always specify the encoding
        byte[] buffer = messageWithDate.getBytes(StandardCharsets.UTF_8);

        // Create a packet with the message, the multicast address and the port
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, castAdress, port);

        // Send the packet
        socket.send(packet);

        // Print the message
        System.out.println("[EMITTER] Message sent: " + messageWithDate);

        // Wait for the next message
        Thread.sleep(frequency);
      } catch (Exception e) {
        System.err.println("[EMITTER] An error occurred: " + e.getMessage());
      }
    }
  }
}
