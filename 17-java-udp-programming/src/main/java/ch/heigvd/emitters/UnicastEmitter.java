package ch.heigvd.emitters;

import picocli.CommandLine.Command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Command(
        name = "unicast",
        description = "Start an UDP unicast emitter"
)
public class UnicastEmitter extends AbstractEmitter {

    @Override
    public Integer call() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(host);
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    String timestamp = dateFormat.format(new Date());

                    String message = "Hello, from unicast client at " + InetAddress.getLocalHost().getHostAddress() + " at " + timestamp;
                    byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket datagram = new DatagramPacket(
                            payload,
                            payload.length,
                            serverAddress,
                            parent.getPort()
                    );

                    socket.send(datagram);

                    System.out.println("Message unicasted: " + message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, delay, frequency, TimeUnit.MILLISECONDS);

            // Keep the program running for a while
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
