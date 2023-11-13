package ch.heigvd.commands.broadcast;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "broadcast",
        description = "UDP example with broadcast",
        subcommands = {BroadcastClient.class, BroadcastServer.class}
)
@Getter
public class Broadcast {

    @CommandLine.ParentCommand
    private ch.heigvd.Main parent;

    @Option(
            names = {"-p", "--port"},
            description = "Port to listen/connect to (default: 9876)",
            defaultValue = "9876",
            scope = CommandLine.ScopeType.INHERIT
    )
    private int port;
}
