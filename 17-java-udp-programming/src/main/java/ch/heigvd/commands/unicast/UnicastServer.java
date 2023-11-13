package ch.heigvd.commands.unicast;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Command(name = "server", description = "Start a UDP unicast server")
public class UnicastServer implements Callable<Integer> {

    @CommandLine.ParentCommand
    private ch.heigvd.commands.unicast.Unicast parent;

    @Override
    public Integer call() {
        try (DatagramSocket socket = new DatagramSocket(parent.getPort())) {
            while (true) {
                byte[] receiveData = new byte[1024];

                DatagramPacket datagram = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );

                System.out.println("Server waiting for unicast messages...");

                try {
                    socket.receive(datagram);

                    String message = new String(
                            datagram.getData(),
                            datagram.getOffset(),
                            datagram.getLength(),
                            StandardCharsets.UTF_8
                    );

                    System.out.println("Received unicast message: " + message);
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
