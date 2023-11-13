package ch.heigvd.commands.broadcast;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Command(name = "client", description = "Start a UDP broadcast client")
public class BroadcastClient implements Callable<Integer> {

    @CommandLine.ParentCommand
    private ch.heigvd.commands.broadcast.Broadcast parent;

    @Option(
            names = {"-H", "--host"},
            description = "Subnet range to send message to (x.x.x.255) (default: 255.255.255.255)",
            defaultValue = "255.255.255.255",
            scope = CommandLine.ScopeType.INHERIT
    )
    private String host;

    @Override
    public Integer call() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress broadcastAddress = InetAddress.getByName(host);

            socket.setBroadcast(true);

            String message = "Hello, Broadcast Server!";
            byte[] payload = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket datagram = new DatagramPacket(
                    payload,
                    payload.length,
                    broadcastAddress,
                    parent.getPort()
            );

            socket.send(datagram);

            System.out.println("Message broadcasted: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
