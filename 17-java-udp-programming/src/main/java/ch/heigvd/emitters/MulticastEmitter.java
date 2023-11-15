package ch.heigvd.emitters;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Command(
        name = "multicast",
        description = "Start an UDP multicast emitter"
)
public class MulticastEmitter extends AbstractEmitter {

    @Option(
            names = {"-i", "--interface"},
            description = "Interface to use.",
            scope = CommandLine.ScopeType.INHERIT,
            required = true
    )
    private String interfaceName;

    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(parent.getPort())) {
            InetAddress multicastAddress = InetAddress.getByName(host);
            InetSocketAddress group = new InetSocketAddress(multicastAddress, parent.getPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);

            socket.joinGroup(group, networkInterface);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    String timestamp = dateFormat.format(new Date());

                    String message = "Hello, from multicast client at " + InetAddress.getLocalHost().getHostAddress() + " at " + timestamp;
                    byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket datagram = new DatagramPacket(
                            payload,
                            payload.length,
                            group
                    );

                    socket.send(datagram);

                    System.out.println("Message multicasted: " + message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, delay, frequency, TimeUnit.MILLISECONDS);

            // Keep the program running for a while
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            socket.leaveGroup(group, networkInterface);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
