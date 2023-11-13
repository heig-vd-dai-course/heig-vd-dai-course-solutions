package ch.heigvd.commands.multicast;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Command(name = "client", description = "Start a UDP multicast client")
public class MulticastClient implements Callable<Integer> {

    @CommandLine.ParentCommand
    protected ch.heigvd.commands.multicast.Multicast parent;

    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(parent.getPort())) {
            InetAddress multicastAddress = InetAddress.getByName(parent.getHost());
            InetSocketAddress group = new InetSocketAddress(multicastAddress, parent.getPort());
            NetworkInterface networkInterface = NetworkInterface.getByName("en0");

            socket.joinGroup(group, networkInterface);

            String message = "Hello, Multicast Server!";
            byte[] payload = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket datagram = new DatagramPacket(
                    payload,
                    payload.length,
                    group
            );

            socket.send(datagram);

            System.out.println("Message multicasted: " + message);

            socket.leaveGroup(group, networkInterface);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
