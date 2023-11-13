package ch.heigvd.commands.unicast;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "unicast",
        description = "UDP example with unicast",
        subcommands = {UnicastClient.class, UnicastServer.class}
)
@Getter
public class Unicast {

    @CommandLine.ParentCommand
    private ch.heigvd.Main parent;

    @Option(
            names = {"-p", "--port"},
            description = "Port to listen/connect to",
            defaultValue = "9876",
            scope = CommandLine.ScopeType.INHERIT
    )
    private int port;
}
