package splitwise.server.commands;


import splitwise.server.UserContextHolder;
import splitwise.server.services.UserService;

import java.util.Deque;
import java.util.Iterator;

public class LoginCommand extends Command{
    public static final String INVALID_CREDENTIALS = "Invalid username or password!";
    public static final String SUCCESSFUL_LOGIN = "Successful login!";
    public static final String NO_NOTIFICATIONS_TO_SHOW =  "No notifications to show.";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;
    public static final String NOTIFICATIONS_TITLE = RED_STAR_SYMBOL+RED_STAR_SYMBOL+RED_STAR_SYMBOL+" Notifications " +
            RED_STAR_SYMBOL+RED_STAR_SYMBOL+RED_STAR_SYMBOL;


    private String username;
    private char[] password;

    public LoginCommand(String command, UserService userRepository) {
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
        boolean validCredentials = userService.checkCredentialsValidity(username,password);
        if(!validCredentials) {
            return INVALID_CREDENTIALS;
        }
        UserContextHolder.usernameHolder.set(username);
        String successfulLoginResponse = createSuccessfulLoginResponse();
        return successfulLoginResponse;
    }


    private String createSuccessfulLoginResponse(){
        StringBuilder response = new StringBuilder(SUCCESSFUL_LOGIN+'\n');
        Deque<String> userNotifications = userService.getUserNotifications(username);
        String loginResponse = appendNotificationsToResponse(response,userNotifications);
        return loginResponse;
    }

    private String appendNotificationsToResponse(StringBuilder response,Deque<String>notificationMessages) {
        if(notificationMessages.size()==0) {
            response.append(NO_NOTIFICATIONS_TO_SHOW);
            return response.toString();
        }
        response.append(NOTIFICATIONS_TITLE+'\n');
        Iterator<String> iterator = notificationMessages.iterator();
        while (iterator.hasNext()) {
            response.append(iterator.next() + "\n\n");
        }
        return response.toString();
    }
}
