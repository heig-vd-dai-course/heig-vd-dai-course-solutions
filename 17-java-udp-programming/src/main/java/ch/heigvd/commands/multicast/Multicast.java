package ch.heigvd.commands.multicast;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "multicast",
        description = "UDP example with multicast",
        subcommands = {MulticastClient.class, MulticastServer.class}
)
@Getter
public class Multicast {

    @CommandLine.ParentCommand
    private ch.heigvd.Main parent;

    @Option(
            names = {"-p", "--port"},
            description = "Port to listen/connect to",
            defaultValue = "9876",
            scope = CommandLine.ScopeType.INHERIT
    )
    private int port;

    @Option(
            names = {"-H", "--host"},
            description = "Host to listen/connect to",
            defaultValue = "230.0.0.1",
            scope = CommandLine.ScopeType.INHERIT
    )
    private String host;
}
