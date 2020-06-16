package splitwise.server.commands;


import splitwise.server.UserService;

public class InvalidCommand extends Command {
    public static final String NOT_SUPPORTED_COMMAND = "Not supported command!";

    public InvalidCommand(String command, UserService userRepository){
        super(userRepository);
    }

    @Override
    public String execute() {
        return NOT_SUPPORTED_COMMAND;
    }
}
