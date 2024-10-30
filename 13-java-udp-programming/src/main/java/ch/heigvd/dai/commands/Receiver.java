package ch.heigvd.dai.commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    throw new UnsupportedOperationException(
        "Please remove this exception and implement this method.");
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
