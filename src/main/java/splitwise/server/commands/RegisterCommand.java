package splitwise.server.commands;


import splitwise.server.exceptions.AuthenticationException;
import splitwise.server.services.AuthenticationService;

public class RegisterCommand extends Command{
    public static final String SUCCESSFUL_REGISTRATION = "Successful registration!";
    public static final String REGISTRATION_FAILED ="Registration attempt failed. Try again later.";
    public static final String TAKEN_USERNAME = "Username is already taken. Try using another.";
    public static final String ALREADY_LOGGED_IN = "Registration is not allowed when already logged in.";

    private String username;
    private char[] password;

    private AuthenticationService authenticationService;

    public RegisterCommand(String command, AuthenticationService authenticationService) {
        super(authenticationService);
        this.authenticationService = authenticationService;
        initializeCommandParameters(command);
    }


    private void initializeCommandParameters(String command) {
        String[]commandParts = command.split("\\s+");
        username = commandParts[1];
        password = commandParts[2].toCharArray();
    }


    @Override
    public String execute() {
        if (!isCommandInvokerLoggedIn) {
            boolean isRegistered = authenticationService.checkIfRegistered(username);
            if (isRegistered) {
                return TAKEN_USERNAME;
            }
            String registrationResult = register();
            return registrationResult;
        }
        return ALREADY_LOGGED_IN;
    }


    private String register(){
        try {
            authenticationService.registerUser(username,password);
            authenticationService.setUserAsActive(username);
        } catch (AuthenticationException e) {
            return REGISTRATION_FAILED;
        }

        return SUCCESSFUL_REGISTRATION;
    }


}
