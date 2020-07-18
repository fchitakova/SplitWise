package splitwise.server.commands;

import splitwise.server.services.SplitWiseService;

public class HelpCommand extends Command {
    public HelpCommand(SplitWiseService splitWiseService) {
        super(splitWiseService);
    }

    @Override
    public String execute() {
        return null;
    }
}
