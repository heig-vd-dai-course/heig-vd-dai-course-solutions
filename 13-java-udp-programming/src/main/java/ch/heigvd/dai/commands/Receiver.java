package ch.heigvd.dai.commands;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import picocli.CommandLine;

@CommandLine.Command(
    name = "receiver",
    description =
        "Start the receiver part of the network application using the fire-and-forget messaging pattern for the emitters and the request-reply messaging pattern for the operators.")
public class Receiver implements Callable<Integer> {
  // The rooms with their temperature
  protected Map<String, Integer> roomsTemperature = new ConcurrentHashMap<>();

  @CommandLine.Option(
      names = {"-M", "--multicast-address"},
      description = "Multicast address to use for the emitters (default: ${DEFAULT-VALUE}).",
      defaultValue = "230.1.2.3")
  protected String emittersMulticastAddress;

  @CommandLine.Option(
      names = {"-E", "--emitters-port"},
      description = "Port to use for the emitters (default: ${DEFAULT-VALUE}).",
      defaultValue = "7337")
  protected int emittersPort;

  @CommandLine.Option(
      names = {"-O", "--operators-port"},
      description = "Port to use for the operators (default: ${DEFAULT-VALUE}).",
      defaultValue = "1732")
  protected int operatorsPort;

  @CommandLine.Option(
      names = {"-I", "--interface"},
      description = "Network interface to use",
      required = true)
  protected String networkInterface;

  /**
   * This method will be called when the receiver is started.
   *
   * <p>It will start two workers, one for the emitters and one for the operators. This allows the
   * receiver to handle both aspects of the network application at the same time.
   *
   * <p>Each worker will be started in its own thread and can have access to the roomsTemperature
   * map that follows all the best practices for concurrent access.
   *
   * @return 0 if the receiver was started successfully, 1 otherwise.
   */
  @Override
  public Integer call() {
    try (ExecutorService executorService = Executors.newFixedThreadPool(2); ) {
      executorService.submit(this::emittersWorker);
      executorService.submit(this::operatorsWorker);
    } catch (Exception e) {
      System.out.println("[Receiver] Exception: " + e);

      return 1;
    }

    return 0;
  }

  /**
   * This method will take care of the emitters part of the receiver using the fire-and-forget
   * messaging pattern.
   */
  public Integer emittersWorker() {
    try (MulticastSocket socket = new MulticastSocket(emittersPort)) {
      // Join the multicast group
      InetAddress multicastAddress = InetAddress.getByName(emittersMulticastAddress);
      InetSocketAddress multicastGroup = new InetSocketAddress(multicastAddress, emittersPort);
      NetworkInterface netInterface = NetworkInterface.getByName(networkInterface);
      socket.joinGroup(multicastGroup, netInterface);

      System.out.println(
              "[RECEIVER] Listening for multicast messages on interface " + networkInterface + "...");

      while (!socket.isClosed()) {
        // Create a buffer for the incoming message
        byte[] buffer = new byte[1024];

        // Create a packet for the incoming message
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        // Receive the packet - this is a blocking call
        socket.receive(packet);

        // Transform the message into a string
        String message =
                new String(
                        packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);

        // Print the message
        System.out.println("[RECEIVER] Message received: " + message);

        String[] messageParts = message.split(" ");

        if (messageParts.length == 3 && messageParts[0].equals("TEMP")) {
          try{
            int id = Integer.parseInt(messageParts[1]);
            int temp = Integer.parseInt(messageParts[2]);

            roomsTemperature.put(Integer.toString(id), temp);
            System.out.println("[RECEIVER] Room temperature for " +id + " is " +temp);
          }catch(Exception e){
            System.out.println("[RECEIVER] Exception: " + e);
          }
        }else{
          System.out.println("[RECEIVER] Invalid message received: " + message);
        }
      }

      // Quit the multicast group
      socket.leaveGroup(multicastGroup, netInterface);

    } catch (Exception e) {
      System.out.println("[Receiver] Exception: " + e);
    }



    return 0;
  }

  /**
   * This method will take care of the operators part of the receiver using the request-reply
   * messaging pattern.
   */
  public Integer operatorsWorker() {
    try (DatagramSocket socket = new DatagramSocket(operatorsPort)) {
      System.out.println("[SERVER] Server is listening on port " + operatorsPort + "...");

      while (!socket.isClosed()) {
        byte[]  buffer = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);

        System.out.println("[SERVER] Received request : " + message);

        String response;
        String[] parts = message.split(" ");

        if (parts.length == 2 && "REQ_TEMP".equals(parts[0])) {
          /*
              As the key for the map is a string, there is no need to verify that the user input is a valid number

              If he tries and request something invalid, it will just not be found within the map.
           */
          String roomId = parts[1];
          Integer temperature = roomsTemperature.get(roomId);
          if (temperature != null) {
            response = "TEMP " + temperature;
          } else {
            response = "ERROR 2";
          }
        }else{
          response = "ERROR";
        }

        byte[] responseBuffer = response.getBytes(StandardCharsets.UTF_8);
        DatagramPacket responsePacket =
                new DatagramPacket(
                        responseBuffer,
                        responseBuffer.length,
                        packet.getAddress(),
                        packet.getPort());

        socket.send(responsePacket);

        System.out.println("[SERVER] Response sent : " + response);
      }
    } catch (IOException e) {
      System.err.println("[SERVER] An error occurred: " + e.getMessage());
    }

    return 0;
  }
}
