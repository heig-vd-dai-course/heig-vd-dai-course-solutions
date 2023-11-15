package ch.heigvd.receivers;

import picocli.CommandLine;

import java.net.*;

@CommandLine.Command(
    name = "multicast-receiver",
    description = "Start an UDP multicast receiver"
)
public class MulticastReceiver extends AbstractReceiver {

    @CommandLine.Option(
        names = {"-i", "--interface"},
        description = "Interface to use",
        scope = CommandLine.ScopeType.INHERIT,
        required = true
    )
    private String interfaceName;

    @CommandLine.Option(
        names = {"-H", "--host"},
        description = "Subnet range/multicast address to use.",
        required = true,
        scope = CommandLine.ScopeType.INHERIT
    )
    protected String host;

    @Override
    public Integer call() throws Exception {
        try (MulticastSocket socket = new MulticastSocket(parent.getPort())) {
            InetAddress multicastAddress = InetAddress.getByName(host);
            InetSocketAddress group = new InetSocketAddress(multicastAddress, parent.getPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);

            socket.joinGroup(group, networkInterface);

            byte[] receiveData = new byte[1024];

            System.out.println("Receiver listening on port " + parent.getPort());

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                    receiveData,
                    receiveData.length
                );

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
