package splitwise.server.commands;


import splitwise.server.UserService;
import splitwise.server.model.SplitWiseConstants;

public class InvalidCommand extends Command {
    public InvalidCommand(String command, UserService userRepository){
        super(userRepository);
    }


    @Override
    public String execute() {
        return SplitWiseConstants.NOT_SUPPORTED_COMMAND;
    }
}
