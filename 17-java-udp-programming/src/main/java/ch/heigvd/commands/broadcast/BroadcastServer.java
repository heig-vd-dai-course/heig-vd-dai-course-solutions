package ch.heigvd.commands.broadcast;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Command(name = "server", description = "Start a UDP broadcast server")
public class BroadcastServer implements Callable<Integer> {

    @CommandLine.ParentCommand
    private ch.heigvd.commands.broadcast.Broadcast parent;

    @Override
    public Integer call() {
        try (DatagramSocket socket = new DatagramSocket(parent.getPort())) {
            while (true) {
                byte[] receiveData = new byte[1024];

                DatagramPacket datagram = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );

                System.out.println("Server waiting for broadcast messages...");

                try {
                    socket.receive(datagram);

                    String message = new String(
                            datagram.getData(),
                            datagram.getOffset(),
                            datagram.getLength(),
                            StandardCharsets.UTF_8
                    );

                    System.out.println("Received broadcast message: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
