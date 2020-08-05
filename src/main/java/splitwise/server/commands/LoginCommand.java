package splitwise.server.commands;

import splitwise.server.services.AuthenticationService;

import java.util.Deque;

public class LoginCommand extends Command {
    public static final String INVALID_CREDENTIALS = "Invalid username or password!";
    public static final String SUCCESSFUL_LOGIN = "Successful login!";
    public static final String NO_NOTIFICATIONS_TO_SHOW = "No notifications to show.";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;
    public static final String NOTIFICATIONS_TITLE = RED_STAR_SYMBOL + RED_STAR_SYMBOL + RED_STAR_SYMBOL + " Notifications " + RED_STAR_SYMBOL + RED_STAR_SYMBOL + RED_STAR_SYMBOL;
    public static final String ALREADY_LOGGED_IN = "Already logged in. Logout first to log in another account.";
    
    private String username;
    private char[] password;
    
    private AuthenticationService authenticationService;
    
    public LoginCommand(String command, AuthenticationService authenticationService) {
	super(authenticationService);
	this.authenticationService = authenticationService;
	initializeCommandParameters(command);
    }
    
    private void initializeCommandParameters(String command) {
	String[] commandParts = command.split("\\s+");
	username = commandParts[1];
	password = commandParts[2].toCharArray();
    }
    
    @Override
    public String execute() {
	if(isLoginAllowed()) {
	    if(areCredentialsValid()) {
		authenticationService.setUserAsActive(username);
		String commandResult = createSuccessfulLoginResponse();
		return commandResult;
	    } else {
		return INVALID_CREDENTIALS;
	    }
	}
	return ALREADY_LOGGED_IN;
    }
    
    private boolean isLoginAllowed() {
	return !isCommandInvokerLoggedIn;
    }
    
    private boolean areCredentialsValid() {
	return authenticationService.checkCredentialsValidity(username, password);
    }
    
    private String createSuccessfulLoginResponse() {
	StringBuilder response = new StringBuilder(SUCCESSFUL_LOGIN + '\n');
	Deque<String> userNotifications = authenticationService.getUserNotifications(username);
	String loginResponse = appendNotificationsToResponse(response, userNotifications);
	return loginResponse;
    }
    
    private String appendNotificationsToResponse(StringBuilder response, Deque<String> notificationMessages) {
	if(notificationMessages.size()==0) {
	    response.append(NO_NOTIFICATIONS_TO_SHOW);
	    return response.toString();
	}
	response.append(NOTIFICATIONS_TITLE + '\n');
	for(String notification : notificationMessages) {
	    response.append(notification + "\n\n");
	}
	return response.toString();
    }
}
