package splitwise.server.commands;

import splitwise.server.UserService;
import splitwise.server.model.SplitWiseConstants;

public class LogoutCommand extends Command{

    public LogoutCommand(UserService userRepository) {
        super(userRepository);
    }

    @Override
    public String execute() {
        return SplitWiseConstants.GOODBYE_MESSAGE;
    }
}
