package splitwise.server.commands;

import splitwise.server.UserService;

public class LogoutCommand extends Command{
    public static final String GOODBYE_MESSAGE = "Split Wise Server: GoodBye";

    public LogoutCommand(UserService userRepository) {
        super(userRepository);
    }

    @Override
    public String execute() {
        return GOODBYE_MESSAGE;
    }
}
