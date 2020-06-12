package splitwise.server.commands;


import splitwise.server.UserContextHolder;
import splitwise.server.UserService;
import splitwise.server.model.SplitWiseConstants;

import java.util.Deque;
import java.util.Iterator;

public class LoginCommand extends Command{
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
            return SplitWiseConstants.INVALID_CREDENTIALS;
        }
        UserContextHolder.usernameHolder.set(username);
        String successfulLoginResponse = createSuccessfulLoginResponse();
        return successfulLoginResponse;
    }


    private String createSuccessfulLoginResponse(){
        StringBuilder response = new StringBuilder(SplitWiseConstants.SUCCESSFUL_LOGIN+'\n');
        Deque<String> userNotifications = userService.getUserNotifications(username);
        String loginResponse = appendNotificationsToResponse(response,userNotifications);
        return loginResponse;
    }

    private String appendNotificationsToResponse(StringBuilder response,Deque<String>notificationMessages) {
        if(notificationMessages.size()==0) {
            response.append(SplitWiseConstants.NO_NOTIFICATIONS_TO_SHOW);
            return response.toString();
        }
        response.append(SplitWiseConstants.NOTIFICATIONS_TITLE+'\n');
        Iterator<String> iterator = notificationMessages.iterator();
        while (iterator.hasNext()) {
            response.append(iterator.next() + "\n\n");
        }
        return response.toString();
    }
}
