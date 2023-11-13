package ch.heigvd.commands.unicast;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

@Command(name = "client", description = "Start a UDP unicast client")
public class UnicastClient implements Callable<Integer> {

    @CommandLine.ParentCommand
    private ch.heigvd.commands.unicast.Unicast parent;

    @Option(
            names = {"-H", "--host"},
            description = "Host to listen/connect to",
            defaultValue = "234.1.2.3"
    )
    private String host;

    @Override
    public Integer call() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "Hello, Unicast Server!";
            byte[] sendData = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(host);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    serverAddress,
                    parent.getPort()
            );

            socket.send(sendPacket);

            System.out.println("Message sent to server: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
