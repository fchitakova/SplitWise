package splitwise.server.commands;


import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.UserService;

public class RegisterCommand extends Command{
    public static final String SUCCESSFUL_REGISTRATION = "Successful registration!";
    public static final String REGISTRATION_FAILED ="Registration attempt failed. Try again later.";
    public static final String TAKEN_USERNAME = "Username is already taken. Try using another.";
    public static final String ALREADY_LOGGED_IN = "Registration is not allowed when already logged in.";

    private String username;
    private char[] password;

    public RegisterCommand(String command, UserService userRepository) {
        super(userRepository);
        initializeCommandParameters(command);
    }


    private void initializeCommandParameters(String command) {
        String[]commandParts = command.split("\\s+");
        username = commandParts[1];
        password = commandParts[2].toCharArray();
    }


    @Override
    public String execute() {
        if(isCurrentUserAlreadyLoggedIn()){
            return ALREADY_LOGGED_IN;
        }

        boolean isRegistered = userService.checkIfRegistered(username);
        if(isRegistered){
            return TAKEN_USERNAME;
        }
        String registrationResult = register();
        return registrationResult;
    }

    private boolean isCurrentUserAlreadyLoggedIn(){
        String currentLoggedInUsername = userService.getCurrentlyLoggedInUserUsername();
        return currentLoggedInUsername!=null;
    }

    private String register(){
        try {
            userService.registerUser(username,password);
        } catch (UserServiceException e) {
            return REGISTRATION_FAILED;
        }

        userService.setUserAsActive(username);

        return SUCCESSFUL_REGISTRATION;
    }


}
