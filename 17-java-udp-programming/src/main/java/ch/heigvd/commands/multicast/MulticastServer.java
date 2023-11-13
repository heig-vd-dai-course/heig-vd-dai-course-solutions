package ch.heigvd.commands.multicast;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Command(name = "server", description = "Start a UDP multicast server")
public class MulticastServer implements Callable<Integer> {

    @CommandLine.ParentCommand
    protected ch.heigvd.commands.multicast.Multicast parent;

    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(parent.getPort())) {
            InetAddress multicastAddress = InetAddress.getByName(parent.getHost());
            InetSocketAddress group = new InetSocketAddress(multicastAddress, parent.getPort());
            NetworkInterface networkInterface = NetworkInterface.getByName("en0");

            socket.joinGroup(group, networkInterface);

            while (true) {
                byte[] receiveData = new byte[1024];

                DatagramPacket datagram = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );

                System.out.println("Server waiting for multicast messages...");

                try {
                    socket.receive(datagram);

                    String message = new String(
                            datagram.getData(),
                            datagram.getOffset(),
                            datagram.getLength(),
                            StandardCharsets.UTF_8
                    );

                    System.out.println("Received multicast message: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            socket.leaveGroup(group, networkInterface);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
