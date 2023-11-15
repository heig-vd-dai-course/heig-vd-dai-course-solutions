package ch.heigvd.receivers;

import picocli.CommandLine;

import java.util.concurrent.Callable;

public abstract class AbstractReceiver implements Callable<Integer> {

    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;

}
