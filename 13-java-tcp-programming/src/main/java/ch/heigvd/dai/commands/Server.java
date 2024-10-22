package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.TCPServer;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "6433")
  protected int port;

  @Override
  public Integer call() {
    new TCPServer(port, 0, 100);
    return 0;
  }
}
