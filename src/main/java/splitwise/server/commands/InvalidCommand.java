package splitwise.server.commands;

import splitwise.server.services.SplitWiseService;

public class InvalidCommand extends Command {
    public static final String NOT_SUPPORTED_COMMAND = "Not supported command!";
    
    public InvalidCommand(SplitWiseService splitWiseService) {
	super(splitWiseService);
    }
    
    @Override
    public String execute() {
	return NOT_SUPPORTED_COMMAND;
    }
}
