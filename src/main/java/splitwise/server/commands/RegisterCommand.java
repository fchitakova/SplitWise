package splitwise.server.commands;


import splitwise.server.UserService;

public class RegisterCommand extends Command{
    public RegisterCommand(String command, UserService userRepository) {
        super(userRepository);
    }


    @Override
    public String execute() {
        return null;
    }
}
