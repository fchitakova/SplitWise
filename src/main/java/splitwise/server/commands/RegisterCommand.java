package splitwise.server.commands;


import splitwise.server.UserContextHolder;
import splitwise.server.UserService;
import splitwise.server.model.SplitWiseConstants;

public class RegisterCommand extends Command{
    private String username;
    private char[] password;

    public RegisterCommand(String command, UserService userRepository) {
        super(userRepository);
        initializeCommandParameters(command);
    }


    private void initializeCommandParameters(String command) {
        String[]commandParts = command.split("\\s+");
        this.username = commandParts[1];
        this.password = commandParts[2].toCharArray();
    }


    @Override
    public String execute() {
        boolean isRegistered = userService.checkIfRegistered(username);
        if(isRegistered){
            return SplitWiseConstants.TAKEN_USERNAME;
        }
        userService.registerUser(username,password);
        UserContextHolder.usernameHolder.set(username);

        return SplitWiseConstants.SUCCESSFUL_REGISTRATION;
    }
}
