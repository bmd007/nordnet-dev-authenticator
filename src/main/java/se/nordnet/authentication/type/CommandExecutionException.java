package se.nordnet.authentication.type;

import lombok.Value;

@Value
public class CommandExecutionException extends RuntimeException {

    int code;
    String output;
    String command;

    public CommandExecutionException(int code, String output, String command) {
        super("Command " + command + " failed with code " + code + " and output: " + output);
        this.code = code;
        this.output = output;
        this.command = command;
    }
}
