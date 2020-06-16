package splitwise.server.commands;


import splitwise.server.UserContextHolder;
import splitwise.server.services.UserService;

public class RegisterCommand extends Command{
    public static final String TAKEN_USERNAME = "Username is already taken.Try using another.";
    public static final String SUCCESSFUL_REGISTRATION = "Successful registration!";

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
            return TAKEN_USERNAME;
        }
        userService.registerUser(username,password);
        UserContextHolder.usernameHolder.set(username);

        return SUCCESSFUL_REGISTRATION;
    }
}
