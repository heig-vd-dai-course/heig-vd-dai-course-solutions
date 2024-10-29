package ch.heigvd.dai.commands;

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
    throw new UnsupportedOperationException(
        "Please remove this exception and implement this method.");
  }
}
