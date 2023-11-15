package ch.heigvd.receivers;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.*;
import java.util.concurrent.Callable;

@Command(
        name = "receiver",
        description = "Start an UDP receiver"
)
public class Receiver implements Callable<Integer> {
    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;

    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "Subnet range/multicast address to use.",
            required = true,
            scope = CommandLine.ScopeType.INHERIT
    )
    protected String host;

    @Option(
            names = {"-i", "--interface"},
            description = "Interface to use",
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

            byte[] buffer = new byte[1024];

            System.out.println("Receiver listening on port " + parent.getPort());

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                System.out.println("Received message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}